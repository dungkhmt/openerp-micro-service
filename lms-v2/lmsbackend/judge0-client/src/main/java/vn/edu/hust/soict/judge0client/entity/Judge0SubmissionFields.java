package vn.edu.hust.soict.judge0client.entity;

import lombok.Getter;

@Getter
public enum Judge0SubmissionFields {
    SOURCE_CODE("source_code"),
    LANGUAGE_ID("language_id"),
    COMPILER_OPTIONS("compiler_options"),
    COMMAND_LINE_ARGUMENTS("command_line_arguments"),
    STDIN("stdin"),
    EXPECTED_OUTPUT("expected_output"),
    CPU_TIME_LIMIT("cpu_time_limit"),
    CPU_EXTRA_TIME("cpu_extra_time"),
    WALL_TIME_LIMIT("wall_time_limit"),
    MEMORY_LIMIT("memory_limit"),
    STACK_LIMIT("stack_limit"),
    MAX_PROCESSES_AND_OR_THREADS("max_processes_and_or_threads"),
    ENABLE_PER_PROCESS_AND_THREAD_TIME_LIMIT("enable_per_process_and_thread_time_limit"),
    ENABLE_PER_PROCESS_AND_THREAD_MEMORY_LIMIT("enable_per_process_and_thread_memory_limit"),
    MAX_FILE_SIZE("max_file_size"),
    REDIRECT_STDERR_TO_STDOUT("redirect_stderr_to_stdout"),
    ENABLE_NETWORK("enable_network"),
    NUMBER_OF_RUNS("number_of_runs"),
    ADDITIONAL_FILES("additional_files"),
    CALLBACK_URL("callback_url"),
    STDOUT("stdout"),
    STDERR("stderr"),
    COMPILE_OUTPUT("compile_output"),
    MESSAGE("message"),
    EXIT_CODE("exit_code"),
    EXIT_SIGNAL("exit_signal"),
    STATUS("status"),
    LANGUAGE("language"),
    STATUS_ID("status_id"),
    CREATED_AT("created_at"),
    FINISHED_AT("finished_at"),
    TOKEN("token"),
    TIME("time"),
    WALL_TIME("wall_time"),
    MEMORY("memory"),
    ALL("*");

    private final String field;

    Judge0SubmissionFields(String field) {
        this.field = field;
    }
}
