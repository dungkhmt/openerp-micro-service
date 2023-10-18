package com.hust.baseweb.util.executor;

import com.hust.baseweb.applications.programmingcontest.entity.TestCaseEntity;
import com.hust.baseweb.constants.ComputerLanguage;
import com.hust.baseweb.constants.Constants;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;

public class GccExecutor {
    private static final String BUILD_COMMAND_C = "gcc -std=c17 -w -o main main.c -lm";
    private static final String BUILD_COMMAND_CPP_11 = "g++ -std=c++11 -w -o main main.cpp";
    private static final String BUILD_COMMAND_CPP_14 = "g++ -std=c++14 -w -o main main.cpp";
    private static final String BUILD_COMMAND_CPP_17 = "g++ -std=c++17 -w -o main main.cpp";

    private static final String SOURCECODE_DELIMITER = "CPP_FILE" + RandomStringUtils.randomAlphabetic(10);
    ;

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

    private String getFileExtension(ComputerLanguage.Languages language){
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
    private static final String MEMORY_LIMIT_ERROR = Constants.TestCaseSubmissionError.MEMORY_LIMIT.getValue();

    private static final int DEFAULT_INITIAL_MEMORY = 10 * 1024;

    public GccExecutor() {

    }

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
        String testcase = "cat <<'" + SOURCECODE_DELIMITER + "' >> testcase" + 0 + ".txt \n"
                + testCase.getTestCase() + "\n"
                + testCase.getCorrectAnswer() + "\n"
                + solutionOutput + "\n"
                + SOURCECODE_DELIMITER + "\n";
        genTestCase += testcase;

        String[] commands = {
                SHFileStart,
                "mkdir -p " + tmpName,
                "cd " + tmpName,
                "cat <<'" + SOURCECODE_DELIMITER + "' >> main" + getFileExtension(language),
                sourceChecker,
                SOURCECODE_DELIMITER,
                getBuildCmd(language),
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

        String sourceSH = String.join("\n", commands);
        return sourceSH;

    }

    public String genSubmitScriptFile(
            List<TestCaseEntity> testCaseEntities,
            String source,
            String tmpName,
            int timeLimit,
            int memoryLimit,
            ComputerLanguage.Languages language
    ) {
        StringBuilder genTestCase = new StringBuilder();
        for (int i = 0; i < testCaseEntities.size(); i++) {
            String testcase = "cat <<'" + SOURCECODE_DELIMITER + "' >> testcase" + i + ".txt \n"
                    + testCaseEntities.get(i).getTestCase() + "\n"
                    + SOURCECODE_DELIMITER + "\n";
            genTestCase.append(testcase);
        }

        String outputFileName = tmpName + "_output.txt";
        String errorFileName = tmpName + "_error.txt";
        String[] commands = {
                SHFileStart,
                "mkdir -p " + tmpName,
                "cd " + tmpName,
                "cat <<'DELIMITER' >> main" + getFileExtension(language),
                source,
                "DELIMITER",
                getBuildCmd(language),
                "FILE=main",
                "if test -f \"$FILE\"; then",
                genTestCase.toString(),
                "n=0",
                "start=$(date +%s%N)",
                "while [ \"$n\" -lt " + testCaseEntities.size() + " ]",
                "do",
                "f=\"testcase\"$n\".txt\"",
                // "cat $f | timeout " + timeLimit + "s " + "./main  || echo Time Limit Exceeded",
                "cat $f | (ulimit -t " + timeLimit
                        + " -v " + (memoryLimit * 1024 + DEFAULT_INITIAL_MEMORY)
                        + " -f 30000; "
                        + "./main > " + outputFileName + "; ) &> " + errorFileName,
                "ERROR=$(head -1 " + errorFileName + ")",
                "FILE_LIMIT='" + FILE_LIMIT_ERROR + "'",
                "TIME_LIMIT='" + TIME_LIMIT_ERROR + "'",
                "MEMORY_LIMIT='" + MEMORY_LIMIT_ERROR + "'",
                "case $ERROR in",
                "*\"$FILE_LIMIT\"*)",
                "echo $FILE_LIMIT",
                ";;",
                "*\"$TIME_LIMIT\"*)",
                "echo $TIME_LIMIT",
                ";;",
                "*\"$MEMORY_LIMIT\"*)",
                "echo $MEMORY_LIMIT",
                ";;",
                "*)",
                "cat " + outputFileName,
                ";;",
                "esac",
                "echo " + Constants.SPLIT_TEST_CASE,
                "n=`expr $n + 1`",
                "done",
                "end=$(date +%s%N)",
                "echo",
                "echo \"$(($(($end-$start))/1000000))\"",
                "echo successful",
                "else",
                "echo Compile Error",
                "fi",
                "cd ..",
                "rm -rf " + tmpName + " &",
                "rm -rf " + tmpName + ".sh" + " &",
                "rm -rf " + tmpName
        };

        String sourceSH = String.join("\n", commands);
        return sourceSH;
    }
}
