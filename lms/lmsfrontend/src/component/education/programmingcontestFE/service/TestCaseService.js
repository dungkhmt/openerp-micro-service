import FileSaver from "file-saver";
import {successNoti} from "../../../../utils/notification";

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
  navigator.clipboard.writeText(allTestCases).then(() => successNoti("Copied", 1000));
};

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