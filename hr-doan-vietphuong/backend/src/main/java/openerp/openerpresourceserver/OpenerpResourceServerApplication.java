package openerp.openerpresourceserver;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.entity.Setting;
import openerp.openerpresourceserver.enums.SettingEnum;
import openerp.openerpresourceserver.repo.SettingRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@SpringBootApplication
@RequiredArgsConstructor
public class OpenerpResourceServerApplication {
    private final SettingRepository settingRepository;

    public static void main(String[] args) {
        SpringApplication.run(OpenerpResourceServerApplication.class, args);
    }

    @PostConstruct
    private void initializeData() {
        createInitialSetting();
    }

    private void createInitialSetting() {
        createInitialAttendanceCycleSetting();
    }

    private void createInitialAttendanceCycleSetting() {
        if (settingRepository.findById(SettingEnum.ATTENDANCE_CYCLE.name()).isEmpty())
            settingRepository.save(
                    Setting
                            .builder()
                            .key(SettingEnum.ATTENDANCE_CYCLE.name())
                            .value("""
                                    {"end_day":"28","type":"MONTHLY"}
                                    """)
                            .build()
            );
    }
}
