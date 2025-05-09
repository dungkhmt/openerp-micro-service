package com.hust.baseweb.util.executor;

import com.hust.baseweb.applications.programmingcontest.entity.TestCaseEntity;
import com.hust.baseweb.constants.ComputerLanguage;
import com.hust.baseweb.constants.Constants;

import java.util.ArrayList;
import java.util.List;

import static com.hust.baseweb.constants.Constants.SOURCECODE_HEREDOC_DELIMITER;

public class GccExecutor {

    private static final String BUILD_COMMAND_C = "gcc -std=c17 -w -DONLINE_JUDGE -O2 -lm -fmax-errors=3 -o main main.c";
    private static final String BUILD_COMMAND_CPP_11 = "g++ -std=c++11 -w -DONLINE_JUDGE -O2 -lm -fmax-errors=3 -march=native -s -Wl,-z,stack-size=268435456 -o main main.cpp";
    private static final String BUILD_COMMAND_CPP_14 = "g++ -std=c++14 -w -DONLINE_JUDGE -O2 -lm -fmax-errors=3 -march=native -s -Wl,-z,stack-size=268435456 -o main main.cpp";
    private static final String BUILD_COMMAND_CPP_17 = "g++ -std=c++17 -w -DONLINE_JUDGE -O2 -lm -fmax-errors=3 -march=native -s -Wl,-z,stack-size=268435456 -o main main.cpp";

    private String getBuildCmd(ComputerLanguage.Languages language) {
        switch (language) {
            case C:
                return BUILD_COMMAND_C;
            case CPP11:
                return BUILD_COMMAND_CPP_11;
            case CPP14:
                return BUILD_COMMAND_CPP_14;
            default:
                return BUILD_COMMAND_CPP_17;
        }
    }

    private String getFileExtension(ComputerLanguage.Languages language) {
        if (language == ComputerLanguage.Languages.C) {
            return FILE_EXTENSION_C;
        }
        return FILE_EXTENSION_CPP;
    }

    private static final String FILE_EXTENSION_C = ".c";
    private static final String FILE_EXTENSION_CPP = ".cpp";
    private static final String SHFileStart = "#!/bin/bash\n";

    private static final String TIME_LIMIT_ERROR = Constants.TestCaseSubmissionError.TIME_LIMIT.getValue();
    private static final String FILE_LIMIT_ERROR = Constants.TestCaseSubmissionError.FILE_LIMIT.getValue();
    private static final String MEMORY_LIMIT_ERROR = Constants.TestCaseSubmissionError.MEMORY_ERROR.getValue();
    private static final String PROGRAM_ERROR = "timeout: the monitored command dumped core";

    private static final int DEFAULT_INITIAL_MEMORY = 10 * 1024;

    public String generateScriptFileWithTestCaseAndCorrectSolution(
            String source,
            String testCase,
            String tmpName,
            int timeLimit,
            ComputerLanguage.Languages cppVersion
    ) {
        List<TestCaseEntity> testCaseEntities = new ArrayList<>();
        TestCaseEntity testCaseEntity = new TestCaseEntity();

        testCaseEntity.setTestCase(testCase);
        testCaseEntities.add(testCaseEntity);

        return genSubmitScriptFile(testCaseEntities, source, tmpName, timeLimit, 100, cppVersion);
    }

    public String genSubmitScriptFileChecker(
            String sourceChecker,
            TestCaseEntity testCase,
            String solutionOutput,
            String tmpName,
            int timeLimit,
            ComputerLanguage.Languages language
    ) {
        String genTestCase = "";
        String testcase = "cat <<'" + SOURCECODE_HEREDOC_DELIMITER + "' >> testcase" + 0 + ".txt \n"
                + testCase.getTestCase() + "\n"
                + testCase.getCorrectAnswer() + "\n"
                + solutionOutput + "\n"
                + SOURCECODE_HEREDOC_DELIMITER + "\n";
        genTestCase += testcase;

        String[] commands = {
                SHFileStart,
                "mkdir -p " + tmpName,
                "cd " + tmpName,
                "cat <<'" + SOURCECODE_HEREDOC_DELIMITER + "' >> main" + getFileExtension(language),
                sourceChecker,
                SOURCECODE_HEREDOC_DELIMITER,
                "ulimit -f 2097152 && " + getBuildCmd(language), // The maximum size limit of the .o file is 1GB. If it exceeds this limit, the program may require allocating a large variable.
                "FILE=main",
                "if test -f \"$FILE\"; then",
                genTestCase,
                "n=0",
                "start=$(date +%s%N)",
                "while [ \"$n\" -lt 1 ]",
                "do",
                "f=\"testcase\"$n\".txt\"",
                "cat $f | timeout " + timeLimit + "s " + "./main  || echo Time Limit Exceeded",
                "echo " + Constants.SPLIT_TEST_CASE,
                "n=`expr $n + 1`",
                "done",
                "end=$(date +%s%N)",
                "echo ",
                "echo \"$(($(($end-$start))/1000000))\"",
                "echo successful",
                "else",
                "echo Compile Error",
                "fi",
                "cd .. ",
                "rm -rf " + tmpName + " & ",
                "rm -rf " + tmpName + ".sh" + " & " + "\n"
        };

        return String.join("\n", commands);
    }

    public String genSubmitScriptFile(
            List<TestCaseEntity> testCases,
            String source,
            String tmpName,
            int timeLimit,
            int memoryLimit,
            ComputerLanguage.Languages language
    ) {
        StringBuilder genTestCase = new StringBuilder();

        for (int i = 0; i < testCases.size(); i++) {
            String testcase = "cat <<'" + SOURCECODE_HEREDOC_DELIMITER + "' >> testcase" + i + ".txt \n"
                    + testCases.get(i).getTestCase() + "\n"
                    + SOURCECODE_HEREDOC_DELIMITER + "\n";

            genTestCase.append(testcase);
        }

        String outputCombinedFile = tmpName + "_output_combined.txt";
        String shellFile = tmpName + "_shell.txt";
        String errorFile = tmpName + "_error.txt";
        String runCommand = "timeout " + (timeLimit + 1) + "s ./main > " + outputCombinedFile + " 2> " + errorFile;

        String[] lines = {
                SHFileStart,
                "mkdir -p " + tmpName,
                "cd " + tmpName,
                "cat <<'" + SOURCECODE_HEREDOC_DELIMITER + "' >> main" + getFileExtension(language),
                source,
                SOURCECODE_HEREDOC_DELIMITER,
                "ulimit -f 2097152 && " + getBuildCmd(language), // The maximum size limit of the .o file is 1GB. If it exceeds this limit, the program may require allocating a large variable.
                "FILE=main",
                "if test -f \"$FILE\"; then",
                genTestCase.toString(),
                "  CPU_TIME_LIMIT=" + timeLimit + " # second",
                "  VIRTUAL_MEM_LIMIT=" + (memoryLimit * 1024 + DEFAULT_INITIAL_MEMORY) + " # KB",
                "  OUTPUT_SIZE_LIMIT=25000 # KB",
                "  WALL_CLOCK_TIME_LIMIT=" + (timeLimit + 1) + " # second",
                "  OUTPUT_FILE=\"" + outputCombinedFile + "\"",
                "  ERROR_FILE=\"" + errorFile + "\"",
                "  SHELL_FILE=\"" + shellFile + "\"",
                "  FILE_LIMIT_EXCEED='" + FILE_LIMIT_ERROR + "'",
                "  TIME_LIMIT_EXCEED='" + TIME_LIMIT_ERROR + "'",
                "  MEMORY_RELATED_ERROR='" + MEMORY_LIMIT_ERROR + "'",
                "  PROGRAM_ERROR='" + PROGRAM_ERROR + "'",
                "  n=0",
                "  while [ \"$n\" -lt " + testCases.size() + " ]",
                "  do",
                "    f=\"testcase\"$n\".txt\"",
                "    testcase_submission_status='Successful'",
                "    start=$(date +%s%N)",
                "    cat $f | (ulimit -t $CPU_TIME_LIMIT; ulimit -v $VIRTUAL_MEM_LIMIT; ulimit -f $OUTPUT_SIZE_LIMIT; " + runCommand + " ) &> \"$SHELL_FILE\"",
                "    exit_status=$?",
                "    end=$(date +%s%N)",
                "    # Check if $SHELL_FILE is empty",
                "    if [ -s $SHELL_FILE ]; then",
                "      # Extract the error message",
                "      extracted_message=$(awk -F ':' '{ sub(/ *[0-9]+ */, \"\", $3); sub(/ \\(core dumped\\) /, \" \", $3); sub(/ *" + runCommand.replace(".", "\\.").replace("/", "\\/") + "/, \" \", $3); print $3 }' \"$SHELL_FILE\")",
                "      case $extracted_message in",
                "        *\"$TIME_LIMIT_EXCEED\"\\ *)",
                "          testcase_submission_status=\"$TIME_LIMIT_EXCEED\"",
                "          ;;",
                "        *\"$MEMORY_RELATED_ERROR\"\\ *)",
                "          testcase_submission_status=\"$MEMORY_RELATED_ERROR\"",
                "          ;;",
                "        *\"$FILE_LIMIT_EXCEED\"\\ *)",
                "          testcase_submission_status=\"$FILE_LIMIT_EXCEED\"",
                "          ;;",
                "        *)",
                "          testcase_submission_status=\"$extracted_message\"",
                "          ;;",
                "      esac",
                "    else",
                "      # Check the exit status if terminated due to timeout",
                "      if [ $exit_status -eq 124 ]; then",
                "        testcase_submission_status=\"$TIME_LIMIT_EXCEED\"",
                "      fi",
                "    fi",
                "    cat $OUTPUT_FILE",
                "    if [ -s $ERROR_FILE ]; then",
                "        if grep -qF \"$PROGRAM_ERROR\" \"$ERROR_FILE\"; then\n",
                "            :",
                "        else\n",
                "            # combine stdout and stderr",
                "            cat $ERROR_FILE",
                "        fi",
                "    fi",
                "    echo " + Constants.SPLIT_TEST_CASE,
                "    echo",
                "    echo \"$(($(($end-$start))/1000000))\"",
                "    echo \"$testcase_submission_status\"",
                "    n=`expr $n + 1`",
                "  done",
                "else",
                "  echo Compile Error",
                "fi",
                "cd ..",
                "rm -rf " + tmpName + " &",
                "rm -rf " + tmpName + ".sh &"
        };

        return String.join("\n", lines);
    }
}
