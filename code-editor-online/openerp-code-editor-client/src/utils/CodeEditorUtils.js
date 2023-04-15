import { PROGRAMMING_LANGUAGES } from "./constants";

export function getLanguageFileType(language) {
  if (language === PROGRAMMING_LANGUAGES.CPP.value) {
    return "cpp";
  }
  if (language === PROGRAMMING_LANGUAGES.JAVA.value) {
    return "java";
  }
  if (language === PROGRAMMING_LANGUAGES.PYTHON.value) {
    return "py";
  }
  return "";
}
