package com.hust.baseweb.util.executor;

import com.hust.baseweb.constants.ComputerLanguage;
import com.hust.baseweb.constants.Constants;
import com.hust.baseweb.applications.programmingcontest.entity.TestCaseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GccExecutor {
    private static final String BUILD_COMMAND_C = "gcc -std=c17 -w -o main main.c";
    private static final String BUILD_COMMAND_CPP_11 = "g++ -std=c++11 -w -o main main.cpp";
    private static final String BUILD_COMMAND_CPP_14 = "g++ -std=c++14 -w -o main main.cpp";
    private static final String BUILD_COMMAND_CPP_17 = "g++ -std=c++17 -w -o main main.cpp";

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
        //for(int i = 0; i < testCaseEntities.size(); i++){
        String testcase = "cat <<EOF >> testcase" + 0 + ".txt \n"
                + testCase.getTestCase() + "\n"
                + testCase.getCorrectAnswer() + "\n"
                + solutionOutput + "\n"
                + "EOF" + "\n";
        genTestCase += testcase;
        //}

        String sourceSH = SHFileStart
                + "mkdir -p " + tmpName + "\n"
                + "cd " + tmpName + "\n"
                + "cat <<EOF >> main" + getFileExtension(language) + "\n"
                + sourceChecker + "\n"
                + "EOF" + "\n"
                + getBuildCmd(language) + "\n"
                + "FILE=main" + "\n"
                + "if test -f \"$FILE\"; then" + "\n"
                + genTestCase + "\n"
                + "n=0\n"
                + "start=$(date +%s%N)\n"
                + "while [ \"$n\" -lt " + 1 + " ]" + "\n"
                + "do\n"
                + "f=\"testcase\"$n\".txt\"" + "\n"
                + "cat $f | timeout " + timeLimit + "s " + "./main  || echo Time Limit Exceeded" + "\n"
                + "echo " + Constants.SPLIT_TEST_CASE + "\n"
                + "n=`expr $n + 1`\n"
                + "done\n"
                + "end=$(date +%s%N)\n"
                + "echo \n"
                + "echo \"$(($(($end-$start))/1000000))\"\n"
                + "echo successful\n"
                + "else\n"
                + "echo Compile Error\n"
                + "fi" + "\n"
                + "cd .. \n"
                + "rm -rf " + tmpName + " & " + "\n"
                + "rm -rf " + tmpName + ".sh" + " & " + "\n";
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
            String testcase = "cat <<EOF >> testcase" + i + ".txt \n"
                    + testCaseEntities.get(i).getTestCase() + "\n"
                    + "EOF" + "\n";
            genTestCase.append(testcase);
        }

        String outputFileName = tmpName + "_output.txt";
        String errorFileName = tmpName + "_error.txt";
        String sourceSH = SHFileStart
                + "mkdir -p " + tmpName + "\n"
                + "cd " + tmpName + "\n"
                + "cat <<EOF >> main" + getFileExtension(language) + "\n"
                + source + "\n"
                + "EOF" + "\n"
                + getBuildCmd(language) + "\n"
                + "FILE=main" + "\n"
                + "if test -f \"$FILE\"; then" + "\n"
                + genTestCase + "\n"
                + "n=0\n"
                + "start=$(date +%s%N)\n"
                + "while [ \"$n\" -lt " + testCaseEntities.size() + " ]" + "\n"
                + "do\n"
                + "f=\"testcase\"$n\".txt\"" + "\n"
                //   + "cat $f | timeout " + timeLimit + "s " + "./main  || echo Time Limit Exceeded" + "\n"
                + "cat $f | (ulimit -t " + timeLimit
                + " -v " + (memoryLimit * 1024 + DEFAULT_INITIAL_MEMORY)
                + " -f 30000; "
                + "./main > " + outputFileName + "; ) &> " + errorFileName + "\n"
                + "ERROR=$(head -1 " + errorFileName + ") \n"
                + "FILE_LIMIT='" + FILE_LIMIT_ERROR + "' \n"
                + "TIME_LIMIT='" + TIME_LIMIT_ERROR + "' \n"
                + "MEMORY_LIMIT='" + MEMORY_LIMIT_ERROR + "' \n"
                + "case $ERROR in \n"
                + "  *\"$FILE_LIMIT\"*) \n"
                + "    echo $FILE_LIMIT \n"
                + "    ;; \n"
                + "  *\"$TIME_LIMIT\"*) \n"
                + "    echo $TIME_LIMIT \n"
                + "    ;; \n"
                + "  *\"$MEMORY_LIMIT\"*) \n"
                + "    echo $MEMORY_LIMIT \n"
                + "    ;; \n"
                + "  *) \n"
                + "    cat " + outputFileName + " \n"
                + "    ;; \n"
                + "esac \n"
                + "echo " + Constants.SPLIT_TEST_CASE + "\n"
                + "n=`expr $n + 1`\n"
                + "done\n"
                + "end=$(date +%s%N)\n"
                + "echo \n"
                + "echo \"$(($(($end-$start))/1000000))\"\n"
                + "echo successful\n"
                + "else\n"
                + "echo Compile Error\n"
                + "fi" + "\n"
                + "cd .. \n"
                + "rm -rf " + tmpName + " & " + "\n"
                + "rm -rf " + tmpName + ".sh" + " & " + "\n"
                + "rm -rf " + tmpName + "\n";
        return sourceSH;
    }
}
