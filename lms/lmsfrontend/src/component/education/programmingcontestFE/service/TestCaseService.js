import FileSaver from "file-saver";
import {successNoti, warningNoti} from "../../../../utils/notification";

export const copyAllTestCases = (testCases) => {
  let allTestCases = "";
  for (const testCase_ith of testCases) {
    allTestCases +=
      "------------- \nINPUT: \n" +
      testCase_ith.testCase +
      "\n\nOUTPUT: \n" +
      testCase_ith.correctAns +
      "\n\n";
  }
  if (allTestCases.length > 0)
    navigator.clipboard.writeText(allTestCases).then(() => successNoti("Copied", 2000));
  else warningNoti("No testcases satisfied", 2000);
};

export const copyTestCasesWithPublicMode = (testCases, publicOnly) => {
  let allTestCases = "";
  for (const testCase_ith of testCases) {
    if (publicOnly && testCase_ith?.isPublic !== "Y") continue;

    allTestCases +=
      "------------- \nINPUT: \n" +
      testCase_ith.testCase +
      "\n\nOUTPUT: \n" +
      testCase_ith.correctAns +
      "\n\n";
  }

  if (allTestCases.length > 0)
    navigator.clipboard.writeText(allTestCases).then(() => successNoti("Copied", 1000));
  else warningNoti("No testcases satisfied", 2000);};

export const downloadAllTestCases = (testCases) => {
  for (let i = 0; i < testCases.length; i++) {
    const testCase_ith = testCases[i];
    const blob = new Blob(
      [
        "INPUT: \n" +
        testCase_ith.testCase +
        "\n\nOUTPUT: \n" +
        testCase_ith.correctAns,
      ],
      {type: "text/plain;charset=utf-8"}
    );
    FileSaver.saveAs(blob, "Testcase_" + (i + 1) + ".txt");
  }
};

export const downloadAllTestCasesWithPublicMode = (testCases, publicOnly) => {
  let count = 0;
  for (let i = 0; i < testCases.length; i++) {
    const testCase_ith = testCases[i];
    if (publicOnly && testCase_ith?.isPublic !== "Y") continue;
    const blob = new Blob(
      [
        "INPUT: \n" +
        testCase_ith.testCase +
        "\n\nOUTPUT: \n" +
        testCase_ith.correctAns,
      ],
      {type: "text/plain;charset=utf-8"}
    );
    FileSaver.saveAs(blob, "Testcase_" + (i + 1) + ".txt");
    count++;
  }

  if (count > 0)
    successNoti(count + " testcases downloaded", 2000);
  else warningNoti("No testcases satisfied", 2000);
};