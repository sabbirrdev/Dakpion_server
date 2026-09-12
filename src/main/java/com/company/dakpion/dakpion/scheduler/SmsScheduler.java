package com.company.dakpion.dakpion.scheduler;

import com.company.dakpion.dakpion.config.DakpionProperties;
import com.company.dakpion.dakpion.constant.SmsStatus;
import com.company.dakpion.dakpion.entity.DakpionSmsLogEntity;
import com.company.dakpion.dakpion.gateway.sms.SmsNetBdGateway;
import com.company.dakpion.dakpion.gateway.sms.dto.SmsBalanceReport;
import com.company.dakpion.dakpion.gateway.sms.dto.SmsDeliveryReport;
import com.company.dakpion.dakpion.repository.DakpionSmsLogRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Polls non-terminal SMS logs every 3 minutes for delivery status.
 * Also runs an hourly balance check and warns when below threshold.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnBean(SmsNetBdGateway.class)
public class SmsScheduler {

    private static final BigDecimal LOW_BALANCE_THRESHOLD = new BigDecimal("10.00");
    private static final List<SmsStatus> PENDING_STATUSES = List.of(SmsStatus.PENDING, SmsStatus.SENT);

    private final DakpionSmsLogRepo smsLogRepo;
    private final SmsNetBdGateway smsNetBdGateway;

    /**
     * Every 3 minutes: reconcile delivery status for all non-terminal SMS logs younger than 24h.
     */
    @Scheduled(fixedRateString = "PT3M")
    public void reconcileDeliveryStatuses() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        List<DakpionSmsLogEntity> pending = smsLogRepo.findByStatusInAndCreatedAtAfter(PENDING_STATUSES, cutoff);

        if (pending.isEmpty()) return;
        log.info("[SmsScheduler] Reconciling {} pending SMS logs", pending.size());

        for (DakpionSmsLogEntity log : pending) {
            if (log.getRequestId() == null || log.getRequestId().isBlank()) continue;
            try {
                SmsDeliveryReport report = smsNetBdGateway.getDeliveryReport(log.getRequestId());
                SmsStatus newStatus = mapReportStatus(report.getRequestStatus());
                BigDecimal charge = extractCharge(report);

                log.setStatus(newStatus);
                log.setLastCheckedAt(LocalDateTime.now());
                if (charge != null) log.setCharge(charge);
                if (report.getErrorCode() != null) log.setErrorCode(report.getErrorCode());
                smsLogRepo.save(log);
            } catch (Exception e) {
                SmsScheduler.log.warn("[SmsScheduler] Failed to reconcile log {}: {}", log.getId(), e.getMessage());
            }
        }
    }

    /**
     * Every hour: check balance and warn if below threshold.
     */
    @Scheduled(fixedRateString = "PT1H")
    public void checkBalance() {
        try {
            SmsBalanceReport balance = smsNetBdGateway.getBalance();
            if (balance.getBalance() != null && balance.getBalance().compareTo(LOW_BALANCE_THRESHOLD) < 0) {
                log.warn("[SmsScheduler] LOW BALANCE ALERT! Current balance: {} BDT (threshold: {})",
                        balance.getBalance(), LOW_BALANCE_THRESHOLD);
            } else {
                log.info("[SmsScheduler] Balance OK: {} BDT",
                        balance.getBalance() != null ? balance.getBalance() : "unknown");
            }
        } catch (Exception e) {
            log.error("[SmsScheduler] Failed to check SMS balance: {}", e.getMessage());
        }
    }

    private SmsStatus mapReportStatus(String reportStatus) {
        if (reportStatus == null) return SmsStatus.UNKNOWN;
        return switch (reportStatus.toLowerCase()) {
            case "delivered" -> SmsStatus.DELIVERED;
            case "completed" -> SmsStatus.DELIVERED;
            case "failed", "rejected", "undelivered" -> SmsStatus.FAILED;
            default -> SmsStatus.SENT;
        };
    }

    private BigDecimal extractCharge(SmsDeliveryReport report) {
        if (report.getRecipients() != null && !report.getRecipients().isEmpty()) {
            return report.getRecipients().stream()
                    .map(SmsDeliveryReport.RecipientReport::getCharge)
                    .filter(c -> c != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return null;
    }
}
