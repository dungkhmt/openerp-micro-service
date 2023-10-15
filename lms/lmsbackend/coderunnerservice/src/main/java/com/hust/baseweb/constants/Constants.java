package com.hust.baseweb.constants;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@AllArgsConstructor(onConstructor_ = @Autowired)
@Data
public class Constants {

    private Map<String, Integer> MapLevelOrder = new HashMap<>();

    @Bean
    public void initConstants() {
        MapLevelOrder.put("easy", 1);
        MapLevelOrder.put("medium", 2);
        MapLevelOrder.put("hard", 3);
    }

    public static final String SPLIT_TEST_CASE = "testcasedone";

    public enum TestCaseSubmissionError {
        FILE_LIMIT("File size limit exceeded"),
        MEMORY_LIMIT("Segmentation fault"),
        TIME_LIMIT("Killed");

        private final String value;

        TestCaseSubmissionError(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    public enum DockerImage {
        GCC("gcc:12.3"), JAVA("eclipse-temurin:17"), PYTHON3("python:3.7-bookworm");

        private final String value;

        DockerImage(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }


    public enum DockerContainer {
        GCC("/gcc"), JAVA("/java"), PYTHON3("/python3");

        private final String value;

        DockerContainer(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum ProblemResultEvaluationType {
        NORMAL("NORMAL_EVALUATION"),
        CUSTOM("CUSTOM_EVALUATION");

        private final String value;

        ProblemResultEvaluationType(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

}
