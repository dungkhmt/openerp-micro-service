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

import java.util.Optional;

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
        createInitialReportColorSetting();
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

    private void createInitialReportColorSetting() {
        Optional<Setting> settingOptional = settingRepository.findById(SettingEnum.REPORT_COLORS.name());
        String value = """
                {
                    "CCO": {
                        "red": 217,
                        "green": 234,
                        "blue": 211
                    },
                    "QCC": {
                        "red": 134,
                        "green": 196,
                        "blue": 244
                    },
                    "NP": {
                        "red": 255,
                        "green": 192,
                        "blue": 0
                    },
                    "NKP": {
                        "red": 255,
                        "green": 255,
                        "blue": 0
                    }
                }
                """.trim();
        if (settingOptional.isEmpty()) {
            Setting reportColorSetting = Setting.builder()
                    .key(SettingEnum.REPORT_COLORS.name())
                    .value(value)
                    .build();
            settingRepository.save(reportColorSetting);
        } else {
            Setting setting = settingOptional.get();
            setting.setValue(value);
            settingRepository.save(setting);
        }
    }
}
