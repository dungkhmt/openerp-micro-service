package vn.edu.hust.soict.judge0client.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.edu.hust.soict.judge0client.utils.Base64Utils;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Judge0Submission {

    @JsonProperty("source_code")
    String sourceCode;

    @JsonProperty("language_id")
    Integer languageId;

    @JsonProperty("compiler_options")
    String compilerOptions;

    @JsonProperty("command_line_arguments")
    String commandLineArguments;

    String stdin;

    @JsonProperty("expected_output")
    String expectedOutput;

    @JsonProperty("cpu_time_limit")
    Float cpuTimeLimit;

    @JsonProperty("cpu_extra_time")
    Float cpuExtraTime;

    @JsonProperty("wall_time_limit")
    Float wallTimeLimit;

    @JsonProperty("memory_limit")
    Float memoryLimit;

    @JsonProperty("stack_limit")
    Integer stackLimit;

    @JsonProperty("max_processes_and_or_threads")
    Integer maxProcessesAndOrThreads;

    @JsonProperty("enable_per_process_and_thread_time_limit")
    Boolean enablePerProcessAndThreadTimeLimit;

    @JsonProperty("enable_per_process_and_thread_memory_limit")
    Boolean enablePerProcessAndThreadMemoryLimit;

    @JsonProperty("max_file_size")
    Integer maxFileSize;

    @JsonProperty("redirect_stderr_to_stdout")
    Boolean redirectStderrToStdout;

    @JsonProperty("enable_network")
    Boolean enableNetwork;

    @JsonProperty("number_of_runs")
    Integer numberOfRuns;

    @JsonProperty("additional_files")
    String additionalFiles;

    @JsonProperty("callback_url")
    String callbackUrl;

    String stdout;

    String stderr;

    @JsonProperty("compile_output")
    String compileOutput;

    String message;

    @JsonProperty("exit_code")
    Integer exitCode;

    @JsonProperty("exit_signal")
    Integer exitSignal;

    Judge0Status status;

    Judge0Language language;

    @JsonProperty("status_id")
    Integer statusId;

    @JsonProperty("created_at")
    Date createdAt;

    @JsonProperty("finished_at")
    Date finishedAt;

    String token;

    Float time;

    @JsonProperty("wall_time")
    Float wallTime;

    Float memory;

    public void decodeBase64() {
        this.sourceCode = Base64Utils.decodeBase64(this.sourceCode);
//        this.compilerOptions = Base64Utils.decodeBase64(this.compilerOptions);
//        this.commandLineArguments = Base64Utils.decodeBase64(this.commandLineArguments);
        this.stdin = Base64Utils.decodeBase64(this.stdin);
        this.expectedOutput = Base64Utils.decodeBase64(this.expectedOutput);
        this.additionalFiles = Base64Utils.decodeBase64(this.additionalFiles);
//        this.callbackUrl = Base64Utils.decodeBase64(this.callbackUrl);
        this.stdout = Base64Utils.decodeBase64(this.stdout);
        this.stderr = Base64Utils.decodeBase64(this.stderr);
        this.compileOutput = Base64Utils.decodeBase64(this.compileOutput);
        this.message = Base64Utils.decodeBase64(this.message);
    }

    public void encodeBase64() {
        this.sourceCode = Base64Utils.encodeBase64(this.sourceCode);
//        this.compilerOptions = Base64Utils.encodeBase64(this.compilerOptions);
//        this.commandLineArguments = Base64Utils.encodeBase64(this.commandLineArguments);
        this.stdin = Base64Utils.encodeBase64(this.stdin);
        this.expectedOutput = Base64Utils.encodeBase64(this.expectedOutput);
        this.additionalFiles = Base64Utils.encodeBase64(this.additionalFiles);
//        this.callbackUrl = Base64Utils.encodeBase64(this.callbackUrl);
//        this.stdout = Base64Utils.encodeBase64(this.stdout);
//        this.stderr = Base64Utils.encodeBase64(this.stderr);
//        this.compileOutput = Base64Utils.encodeBase64(this.compileOutput);
//        this.message = Base64Utils.encodeBase64(this.message);
    }

    public static Judge0Submission getSubmissionDetailsAfterExecution(Judge0Submission submission) {
        return submission == null ? null : Judge0Submission.builder()
                .status(submission.getStatus())
                .stderr(submission.getStderr())
                .stdout(submission.getStdout())
                .compileOutput(submission.getCompileOutput())
                .message(submission.getMessage())
                .time(submission.getTime())
                .memory(submission.getMemory())
                .build();
    }
}

