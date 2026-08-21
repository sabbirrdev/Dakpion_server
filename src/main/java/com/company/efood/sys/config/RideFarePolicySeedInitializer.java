package com.company.efood.sys.config;

import com.company.efood.sys.entity.RideFarePolicy;
import com.company.efood.sys.repository.RideFarePolicyRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RideFarePolicySeedInitializer implements ApplicationRunner {

    private final RideFarePolicyRepo farePolicyRepo;

    @Override
    public void run(ApplicationArguments args) {
        if (farePolicyRepo.count() == 0) {
            seedPolicy("BIKE", 30.0, 15.0, 45.0, 10.0, 5.0, "Motorcycle ride fare policy");
            seedPolicy("CNG", 50.0, 20.0, 70.0, 12.0, 10.0, "Auto rickshaw / CNG fare policy");
            seedPolicy("CAR", 100.0, 35.0, 150.0, 15.0, 20.0, "Car ride fare policy");
            seedPolicy("BICYCLE", 20.0, 10.0, 30.0, 8.0, 5.0, "Bicycle eco ride fare policy");
            seedPolicy("COVERED_VAN", 150.0, 45.0, 200.0, 15.0, 25.0, "Covered van / parcel pickup policy");
            System.out.println("✅ [SEED] Initialized " + farePolicyRepo.count() + " ride fare policies.");
        }
    }

    private void seedPolicy(String vType, Double base, Double perKm, Double min, Double comm, Double bonus, String desc) {
        RideFarePolicy p = new RideFarePolicy();
        p.setVehicleType(vType);
        p.setBaseFare(base);
        p.setPerKmRate(perKm);
        p.setMinimumFare(min);
        p.setPlatformCommissionPercent(comm);
        p.setRiderBonusPerRide(bonus);
        p.setActive(true);
        p.setDescription(desc);
        p.setEntryUser(0L);
        p.setEntryDate(LocalDateTime.now());
        farePolicyRepo.save(p);
    }
}
