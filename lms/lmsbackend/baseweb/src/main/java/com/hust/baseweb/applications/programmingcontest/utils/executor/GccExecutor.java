package com.hust.baseweb.applications.programmingcontest.utils.executor;


import com.hust.baseweb.applications.programmingcontest.constants.Constants;
import com.hust.baseweb.applications.programmingcontest.entity.TestCaseEntity;
import com.hust.baseweb.applications.programmingcontest.utils.ComputerLanguage;

import java.util.List;

import static com.hust.baseweb.applications.programmingcontest.constants.Constants.SOURCECODE_HEREDOC_DELIMITER;

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
    private static final String SHEBANG = "#!/bin/bash\n";

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

        String[] sourceSH = {
            SHEBANG,
            "mkdir -p " + tmpName,
            "cd " + tmpName,
            "cat <<'" + SOURCECODE_HEREDOC_DELIMITER + "' >> main" + getFileExtension(cppVersion),
            source,
            SOURCECODE_HEREDOC_DELIMITER,
            "cat <<'" + SOURCECODE_HEREDOC_DELIMITER + "' >> testcase.txt ",
            testCase,
            SOURCECODE_HEREDOC_DELIMITER,
            getBuildCmd(cppVersion),
            "FILE=main",
            "if test -f \"$FILE\"; then",
            "    cat testcase.txt | timeout " +
            timeLimit +
            "s " +
            "./main && echo -e \"\\nSuccessful\" || echo Time Limit Exceeded",
            "else",
            "  echo Compile Error",
            "fi",
            "cd .. ",
            "rm -rf " + tmpName + " & ",
            "rm -rf " + tmpName + ".sh" + " & " + "\n"};

        return String.join("\n", sourceSH);
    }

    public String checkCompile(String source, String tmpName, ComputerLanguage.Languages language) {

        String[] sourceSH = {
            SHEBANG,
            "mkdir -p " + tmpName,
            "cd " + tmpName,
            "cat <<'" + SOURCECODE_HEREDOC_DELIMITER + "' >> main" + getFileExtension(language),
            source,
            SOURCECODE_HEREDOC_DELIMITER,
            getBuildCmd(language),
            "FILE=main",
            "if test -f \"$FILE\"; then",
            "  echo Successful",
            "else",
            "  echo Compile Error",
            "fi",
            "cd .. ",
            "rm -rf " + tmpName + " & ",
            "rm -rf " + tmpName + ".sh" + " & " + "\n"};

        return String.join("\n", sourceSH);
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
        //for(int i = 0; i < testCaseEntities.size(); i++){
        String testcase = String.join("\n", new String[]{
            "cat <<'" + SOURCECODE_HEREDOC_DELIMITER + "' >> testcase" + 0 + ".txt ",
            testCase.getTestCase(),
            testCase.getCorrectAnswer(),
            solutionOutput,
            SOURCECODE_HEREDOC_DELIMITER + "\n"
        });

        genTestCase += testcase;
        //}

        String[] sourceSH = {
            SHEBANG,
            "mkdir -p " + tmpName,
            "cd " + tmpName,
            "cat <<'" + SOURCECODE_HEREDOC_DELIMITER + "' >> main" + getFileExtension(language),
            sourceChecker,
            SOURCECODE_HEREDOC_DELIMITER,
            getBuildCmd(language),
            "FILE=main",
            "if test -f \"$FILE\"; then",
            genTestCase,
            "n=0",
            "start=$(date +%s%N)",
            "while [ \"$n\" -lt " + 1 + " ]",
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
            "rm -rf " + tmpName + ".sh" + " & " + "\n"};
        return String.join("\n", sourceSH);

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
            String testcase = String.join("\n", new String[]{
                "cat <<'" + SOURCECODE_HEREDOC_DELIMITER + "' >> testcase" + i + ".txt ",
                testCaseEntities.get(i).getTestCase(),
                SOURCECODE_HEREDOC_DELIMITER + "\n"
            });
            genTestCase.append(testcase);
        }

        String outputFileName = tmpName + "_output.txt";
        String errorFileName = tmpName + "_error.txt";
        String[] sourceSH = {
            SHEBANG,
            "mkdir -p " + tmpName,
            "cd " + tmpName,
            "cat <<'" + SOURCECODE_HEREDOC_DELIMITER + "' >> main" + getFileExtension(language),
            source,
            SOURCECODE_HEREDOC_DELIMITER,
            getBuildCmd(language),
            "FILE=main",
            "if test -f \"$FILE\"; then",
            String.valueOf(genTestCase),
            "n=0",
            "start=$(date +%s%N)",
            "while [ \"$n\" -lt " + testCaseEntities.size() + " ]",
            "do",
            "f=\"testcase\"$n\".txt\"",
//            "cat $f | timeout " + timeLimit + "s " + "./main  || echo Time Limit Exceeded" ,
            "cat $f | (ulimit -t " + timeLimit + " -v " + (memoryLimit * 1024 + DEFAULT_INITIAL_MEMORY) + " -f 30000; " + "./main > " + outputFileName + "; ) &> " + errorFileName,
            "ERROR=$(head -1 " + errorFileName + ") ",
            "FILE_LIMIT='" + FILE_LIMIT_ERROR + "' ",
            "TIME_LIMIT='" + TIME_LIMIT_ERROR + "' ",
            "MEMORY_LIMIT='" + MEMORY_LIMIT_ERROR + "' ",
            "case $ERROR in ",
            "  *\"$FILE_LIMIT\"*) ",
            "    echo $FILE_LIMIT ",
            "    ;; ",
            "  *\"$TIME_LIMIT\"*) ",
            "    echo $TIME_LIMIT ",
            "    ;; ",
            "  *\"$MEMORY_LIMIT\"*) ",
            "    echo $MEMORY_LIMIT ",
            "    ;; ",
            "  *) ",
            "    cat " + outputFileName + " ",
            "    ;; ",
            "esac ",
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
            "rm -rf " + tmpName + ".sh" + " & ",
            "rm -rf " + tmpName + "\n"};

        return String.join("\n", sourceSH);
    }
}
