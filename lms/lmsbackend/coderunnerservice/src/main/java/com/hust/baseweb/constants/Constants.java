package com.hust.baseweb.constants;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor(onConstructor_ = @Autowired)
@Data
public class Constants {

    public static final String SPLIT_TEST_CASE = "testcasedone" + RandomStringUtils.randomAlphabetic(10);
    public static final String SOURCECODE_HEREDOC_DELIMITER = RandomStringUtils.randomAlphabetic(10);

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
