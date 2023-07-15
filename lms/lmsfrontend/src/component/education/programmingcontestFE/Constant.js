export const NORMAL_EVALUATION = "NORMAL_EVALUATION";
export const CUSTOM_EVALUATION = "CUSTOM_EVALUATION";

export const SUBMISSION_MODE_SOURCE_CODE = "SUBMISSION_MODE_SOURCE_CODE";
export const SUBMISSION_MODE_SOLUTION_OUTPUT = "SUBMISSION_MODE_SOLUTION_OUTPUT";

export const getSubmissionModeFromConstant = (mode) => {
  return mode === SUBMISSION_MODE_SOLUTION_OUTPUT ? "OUTPUT ONLY" : "NORMAL";
}

export const COMPUTER_LANGUAGES = Object.freeze({
  CPP: 'CPP',
  JAVA: 'JAVA',
  PYTHON: 'PYTHON3'
})

export const DEFAULT_CODE_SEGMENT_CPP = "//CPP \n" +
  "#include <bits/stdc++.h> \n\n" +
  "int main() \n" +
  "{ \n\n" +
  "}";

export const DEFAULT_CODE_SEGMENT_JAVA = "//JAVA \n" +
  "import java.util.*; \n\n" +
  "@SuppressWarnings({\"unchecked\", \"deprecation\"})\n" +
  "public class Main { \n" +
  "    public static void main(String[] args) { \n\n" +
  "    }\n" +
  "}";

export const DEFAULT_CODE_SEGMENT_PYTHON = "#PYTHON \n";