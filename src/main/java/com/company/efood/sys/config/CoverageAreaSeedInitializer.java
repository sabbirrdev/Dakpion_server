package com.company.efood.sys.config;

import com.company.efood.sys.entity.CoverageArea;
import com.company.efood.sys.repository.CoverageAreaRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CoverageAreaSeedInitializer implements ApplicationRunner {

    private final CoverageAreaRepo coverageAreaRepo;

    @Override
    public void run(ApplicationArguments args) {
        if (coverageAreaRepo.count() == 0) {
            seedArea("Lokmanpur Bazar", "Natore", "Baraigram", 10.0, "Lokmanpur local coverage area (৳10 charge)");
            seedArea("Natore Town / Sadar", "Natore", "Natore Sadar", 20.0, "Natore town area (৳20 charge)");
            seedArea("Baraigram Town", "Natore", "Baraigram", 15.0, "Baraigram town area (৳15 charge)");
            seedArea("Rajshahi City Center", "Rajshahi", "Boalia", 30.0, "Rajshahi city central zone (৳30 charge)");
            seedArea("Dhaka Mirpur Zone", "Dhaka", "Mirpur", 25.0, "Dhaka Mirpur coverage zone (৳25 charge)");
            seedArea("Dhaka Dhanmondi Zone", "Dhaka", "Dhanmondi", 30.0, "Dhaka Dhanmondi coverage zone (৳30 charge)");
            System.out.println("✅ [SEED] Initialized " + coverageAreaRepo.count() + " coverage areas with custom delivery charges.");
        }
    }

    private void seedArea(String name, String district, String ps, Double charge, String desc) {
        CoverageArea area = new CoverageArea();
        area.setAreaName(name);
        area.setDistrict(district);
        area.setPoliceStation(ps);
        area.setDeliveryCharge(charge);
        area.setActive(true);
        area.setDescription(desc);
        area.setEntryUser(0L);
        area.setEntryDate(LocalDateTime.now());
        coverageAreaRepo.save(area);
    }
}
