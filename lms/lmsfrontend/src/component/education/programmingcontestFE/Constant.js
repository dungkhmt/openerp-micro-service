export const NORMAL_EVALUATION = "NORMAL_EVALUATION";
export const CUSTOM_EVALUATION = "CUSTOM_EVALUATION";

export const SUBMISSION_MODE_SOURCE_CODE = "SUBMISSION_MODE_SOURCE_CODE";
export const SUBMISSION_MODE_SOLUTION_OUTPUT = "SUBMISSION_MODE_SOLUTION_OUTPUT";
export const SUBMISSION_MODE_NOT_ALLOWED = "SUBMISSION_MODE_NOT_ALLOWED";

export const getSubmissionModeFromConstant = (mode) => {
  switch (mode) {
    case SUBMISSION_MODE_SOURCE_CODE:
      return "Normal";
    case SUBMISSION_MODE_SOLUTION_OUTPUT:
      return "Submit only output";
    case SUBMISSION_MODE_NOT_ALLOWED:
      return "NOT allow submitting";
    default:
      return "NORMAL";
  }
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