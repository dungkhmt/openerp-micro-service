package com.hust.baseweb.applications.programmingcontest.utils.executor;

import com.hust.baseweb.applications.programmingcontest.constants.Constants;
import com.hust.baseweb.applications.programmingcontest.entity.TestCaseEntity;

import java.util.List;

import static com.hust.baseweb.applications.programmingcontest.constants.Constants.SOURCECODE_HEREDOC_DELIMITER;

public class JavaExecutor {

    private static final String buildCmd = "javac Main.java";
    private static final String suffixes = ".java";
    private static final String SHFileStart = "#!/bin/bash\n";

    private static final String TIME_LIMIT_ERROR = Constants.TestCaseSubmissionError.TIME_LIMIT.getValue();
    private static final String FILE_LIMIT_ERROR = Constants.TestCaseSubmissionError.FILE_LIMIT.getValue();
    private static final String MEMORY_LIMIT_ERROR = Constants.TestCaseSubmissionError.MEMORY_LIMIT.getValue();

    private static final int DEFAULT_INITIAL_MEMORY = 10;

    public JavaExecutor() {

    }

    public String generateScriptFileWithTestCaseAndCorrectSolution(
        String source,
        String testCase,
        String tmpName,
        int timeLimit
    ) {
        String sourceSH = SHFileStart
                          +
                          "mkdir -p " +
                          tmpName +
                          "\n"
                          +
                          "cd " +
                          tmpName +
                          "\n"
                          +
                          "cat <<'" + SOURCECODE_HEREDOC_DELIMITER + "' >> Main" +
                          suffixes +
                          "\n"
                          +
                          source +
                          "\n"
                          +
                          SOURCECODE_HEREDOC_DELIMITER +
                          "\n"
                          +
                          "cat <<'" + SOURCECODE_HEREDOC_DELIMITER + "' >> testcase.txt \n"
                          +
                          testCase +
                          "\n"
                          +
                          SOURCECODE_HEREDOC_DELIMITER +
                          "\n"
                          +
                          buildCmd +
                          "\n"
                          +
                          "FILE=Main.class" +
                          "\n"
                          +
                          "if test -f \"$FILE\"; then" +
                          "\n"
                          +
                          "    cat testcase.txt | timeout " +
                          timeLimit +
                          "s " +
                          "java Main  && echo -e \"\\nSuccessful\"  || echo Time Limit Exceeded" +
                          "\n"
                          +
                          "else\n"
                          +
                          "  echo Compile Error\n"
                          +
                          "fi" +
                          "\n"
                          +
                          "cd .. \n"
                          +
                          "rm -rf " +
                          tmpName +
                          " & " +
                          "\n"
                          +
                          "rm -rf " +
                          tmpName +
                          ".sh" +
                          " & " +
                          "\n";
        return sourceSH;
    }

    public String checkCompile(String source, String tmpName) {
        String sourceSH = SHFileStart
                          + "mkdir -p " + tmpName + "\n"
                          + "cd " + tmpName + "\n"
                          + "cat <<'" + SOURCECODE_HEREDOC_DELIMITER + "' >> Main" + suffixes + "\n"
                          + source + "\n"
                          + SOURCECODE_HEREDOC_DELIMITER + "\n"
                          + buildCmd + "\n"
                          + "FILE=Main.class" + "\n"
                          + "if test -f \"$FILE\"; then" + "\n"
                          + "  echo Successful\n"
                          + "else\n"
                          + "  echo Compile Error\n"
                          + "fi" + "\n"
                          + "cd .. \n"
                          + "rm -rf " + tmpName + " & " + "\n"
                          + "rm -rf " + tmpName + ".sh" + " & " + "\n";
        return sourceSH;
    }

    public String genSubmitScriptFile(
        List<TestCaseEntity> testCases,
        String source,
        String tmpName,
        int timeLimit,
        int memoryLimit
    ) {
        StringBuilder genTestCase = new StringBuilder();
        for (int i = 0; i < testCases.size(); i++) {
            String testcase = "cat <<'" + SOURCECODE_HEREDOC_DELIMITER + "' >> testcase" + i + ".txt \n"
                              + testCases.get(i).getTestCase() + "\n"
                              + SOURCECODE_HEREDOC_DELIMITER + "\n";
            genTestCase.append(testcase);
        }

        String outputFileName = tmpName + "_output.txt";
        String errorFileName = tmpName + "_error.txt";

        String sourceSH = SHFileStart
                          +
                          "mkdir -p " +
                          tmpName +
                          "\n"
                          +
                          "cd " +
                          tmpName +
                          "\n"
                          +
                          "cat <<'" +
                          SOURCECODE_HEREDOC_DELIMITER +
                          "' >> Main" +
                          suffixes +
                          "\n"
                          +
                          source +
                          "\n"
                          +
                          SOURCECODE_HEREDOC_DELIMITER +
                          "\n"
                          +
                          buildCmd +
                          "\n"
                          +
                          "FILE=Main.class" +
                          "\n"
                          +
                          "if test -f \"$FILE\"; then" +
                          "\n"
                          +
                          genTestCase +
                          "\n"
                          +
                          "n=0\n"
                          +
                          "start=$(date +%s%N)\n"
                          +
                          "while [ \"$n\" -lt " +
                          testCases.size() +
                          " ]" +
                          "\n"
                          +
                          "do\n"
                          +
                          "f=\"testcase\"$n\".txt\"" +
                          "\n"
                          +
                          "cat $f | (ulimit -t " +
                          timeLimit
                          // + " -v " + (memoryLimit * 1024)
                          +
                          " -f 25000; "
                          +
                          "java -Xmx" +
                          (memoryLimit + DEFAULT_INITIAL_MEMORY) +
                          "m Main > " +
                          outputFileName +
                          " 2>&1; ) &> " +
                          errorFileName +
                          "\n"
//                          + "java -Xlint:none -Xmx" + memoryLimit + "m Main > " + outputFileName + " 2>&1; ) &> " + errorFileName + "\n"
                          +
                          "ERROR=$(head -1 " +
                          errorFileName +
                          ") \n"
                          +
                          "FILE_LIMIT='" +
                          FILE_LIMIT_ERROR +
                          "' \n"
                          +
                          "TIME_LIMIT='" +
                          TIME_LIMIT_ERROR +
                          "' \n"
                          +
                          "MEMORY_LIMIT='" +
                          MEMORY_LIMIT_ERROR +
                          "' \n"
                          +
                          "case $ERROR in \n"
                          +
                          "  *\"$FILE_LIMIT\"*) \n"
                          +
                          "    echo $FILE_LIMIT \n"
                          +
                          "    ;; \n"
                          +
                          "  *\"$TIME_LIMIT\"*) \n"
                          +
                          "    echo $TIME_LIMIT \n"
                          +
                          "    ;; \n"
                          +
                          "  *\"$MEMORY_LIMIT\"*) \n"
                          +
                          "    echo $MEMORY_LIMIT \n"
                          +
                          "    ;; \n"
                          +
                          "  *) \n"
                          +
                          "    cat " +
                          outputFileName +
                          " \n"
                          +
                          "    ;; \n"
                          +
                          "esac \n"
                          +
                          "echo " +
                          Constants.SPLIT_TEST_CASE +
                          "\n"
                          +
                          "n=`expr $n + 1`\n"
                          +
                          "done\n"
                          +
                          "end=$(date +%s%N)\n"
                          +
                          "echo \n"
                          +
                          "echo \"$(($(($end-$start))/1000000))\"\n"
                          +
                          "echo successful\n"
                          +
                          "else\n"
                          +
                          "echo Compile Error\n"
                          +
                          "fi" +
                          "\n"
                          +
                          "cd .. \n"
                          +
                          "rm -rf " +
                          tmpName +
                          " & " +
                          "\n"
                          +
                          "rm -rf " +
                          tmpName +
                          ".sh" +
                          " & " +
                          "\n"
                          +
                          "rm -rf " +
                          tmpName +
                          "\n";

        //   + "cat $f | timeout " + timeout + "s" + " java Main   || echo Time Limit Exceeded" + "\n"
        //   + "echo " + Constants.SPLIT_TEST_CASE + "\n"
        //   + "n=`expr $n + 1`\n"
        //   + "done\n"
        //   + "end=$(date +%s%N)\n"
        //   + "echo \n"
        //   + "echo \"$(($(($end-$start))/1000000))\"\n"
        //   + "echo successful\n"
        //   + "else\n"
        //   + "echo Compile Error\n"
        //   + "fi" + "\n"
        //   + "cd .. \n"
        //   + "rm -rf " + tmpName + " & " + "\n"
        //   + "rm -rf " + tmpName + ".sh" + " & " + "\n";
        return sourceSH;
    }
}
