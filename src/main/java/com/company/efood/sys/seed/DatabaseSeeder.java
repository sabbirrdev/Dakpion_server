package com.company.efood.sys.seed;

import com.company.efood.sys.entity.AppUser;
import com.company.efood.sys.entity.Category;
import com.company.efood.sys.entity.PasswordPolicy;
import com.company.efood.sys.repository.AppUserRepo;
import com.company.efood.sys.repository.CategoryRepo;
import com.company.efood.sys.repository.PasswordPolicyRepo;
import com.company.efood.sys.utils.AppUserType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@Order(1) // Runs immediately upon context startup after Hibernate schema initialization
@RequiredArgsConstructor
public class DatabaseSeeder implements ApplicationRunner {

    private final AppUserRepo appUserRepo;
    private final CategoryRepo categoryRepo;
    private final PasswordPolicyRepo passwordPolicyRepo;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seeding.enabled:true}")
    private boolean isSeedingEnabled;

    @Value("${app.seeding.seed-test-data:false}")
    private boolean seedTestData;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!isSeedingEnabled) {
            log.info("ℹ️ [DatabaseSeeder] Seeding is disabled by configuration (app.seeding.enabled=false).");
            return;
        }

        log.info("🚀 [DatabaseSeeder] Starting idempotent database initialization...");

        PasswordPolicy defaultPolicy = seedDefaultPasswordPolicy();
        seedSystemSuperAdmin(defaultPolicy);
        seedCoreCategories();

        if (seedTestData) {
            seedLocalDevelopmentTestData(defaultPolicy);
        }

        log.info("✅ [DatabaseSeeder] Database seeding finished successfully.");
    }

    private PasswordPolicy seedDefaultPasswordPolicy() {
        final String policyName = "Simple Policy";
        return passwordPolicyRepo.findByName(policyName).orElseGet(() -> {
            PasswordPolicy policy = new PasswordPolicy();
            policy.setName(policyName);
            policy.setMinLength(5);
            policy.setPasswordAge(60);
            policy.setSequential(false);
            policy.setSpecialChar(false);
            policy.setAlphanumeric(false);
            policy.setUpperLower(false);
            policy.setMatchUsername(false);
            policy.setActive(true);
            policy.setEntryUser(0L);
            policy.setEntryDate(LocalDateTime.now());
            PasswordPolicy saved = passwordPolicyRepo.save(policy);
            log.info("🌱 [DatabaseSeeder] Seeded default password policy: '{}'", policyName);
            return saved;
        });
    }

    private void seedSystemSuperAdmin(PasswordPolicy policy) {
        final String adminUsername = "admin";
        AppUser adminUser = appUserRepo.findByUsername(adminUsername).orElseGet(AppUser::new);
        adminUser.setUsername(adminUsername);
        adminUser.setPassword(passwordEncoder.encode("admin123"));
        adminUser.setDisplayName("System Admin");
        adminUser.setAppUserType(AppUserType.SYSTEM_ADMIN);
        adminUser.setUserTypeId(0);
        adminUser.setPasswordPolicy(policy);
        adminUser.setActive(true);
        adminUser.setAccountExpired(false);
        adminUser.setAccountLocked(false);
        adminUser.setCredentialsExpired(false);
        if (adminUser.getId() == null) {
            adminUser.setEntryUser(0L);
            adminUser.setEntryDate(LocalDateTime.now());
        } else {
            adminUser.setUpdateDate(LocalDateTime.now());
        }

        appUserRepo.save(adminUser);
        log.info("🌱 [DatabaseSeeder] Super Admin [admin / admin123] successfully configured.");
    }

    private void seedCoreCategories() {
        record SeedCat(String name, String nameBn, String desc) {}

        List<SeedCat> coreCategories = List.of(
                new SeedCat("Food", "খাবার", "Food delivery category"),
                new SeedCat("Grocery", "মুদির দোকান", "Daily grocery category"),
                new SeedCat("Vegetables", "সবজি", "Vegetable items category"),
                new SeedCat("Medicine", "ওষুধ", "Medicine and pharmacy category"),
                new SeedCat("Ride Service", "রাইড সার্ভিস", "Ride-sharing and transport category")
        );

        for (SeedCat cat : coreCategories) {
            if (!categoryRepo.existsByCategoryName(cat.name())) {
                Category entity = new Category();
                entity.setCategoryName(cat.name());
                entity.setCategoryNameBangla(cat.nameBn());
                entity.setDescription(cat.desc());
                entity.setActive(true);
                entity.setEntryUser(0L);
                entity.setEntryDate(LocalDateTime.now());

                categoryRepo.save(entity);
                log.info("🌱 [DatabaseSeeder] Seeded Category: '{}' ({})", cat.name(), cat.nameBn());
            }
        }
    }

    private void seedLocalDevelopmentTestData(PasswordPolicy policy) {
        final String demoRider = "rider.demo@ekhanei.com";
        if (!appUserRepo.existsByUsername(demoRider)) {
            AppUser rider = new AppUser();
            rider.setUsername(demoRider);
            rider.setPassword(passwordEncoder.encode("RiderPass123!"));
            rider.setDisplayName("Demo Delivery Rider");
            rider.setAppUserType(AppUserType.RAIDER);
            rider.setUserTypeId(2);
            rider.setPasswordPolicy(policy);
            rider.setActive(true);
            rider.setAccountExpired(false);
            rider.setAccountLocked(false);
            rider.setCredentialsExpired(false);
            rider.setEntryUser(0L);
            rider.setEntryDate(LocalDateTime.now());

            appUserRepo.save(rider);
            log.info("🌱 [DatabaseSeeder] Seeded local dev demo rider: '{}'", demoRider);
        }
    }
}
