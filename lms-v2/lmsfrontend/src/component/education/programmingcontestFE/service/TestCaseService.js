import FileSaver from "file-saver";
import {errorNoti, successNoti, warningNoti} from "utils/notification";
import JSZip from "jszip";

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
  else warningNoti("No testcase satisfies the requirement", 2000);
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
  else warningNoti("No testcase satisfies the requirement", 2000);
};

// export const downloadAllTestCases = (testcases) => {
//   for (let i = 0; i < testcases.length; i++) {
//     const testCase_ith = testcases[i];
//     const blob = new Blob(
//       [
//         "INPUT: \n" +
//         testCase_ith.testCase +
//         "\n\nOUTPUT: \n" +
//         testCase_ith.correctAns,
//       ],
//       {type: "text/plain;charset=utf-8"}
//     );
//     FileSaver.saveAs(blob, "Testcase_" + (i + 1) + ".txt");
//   }
// };

export const downloadAllTestCasesWithPublicMode = (testcases, publicOnly) => {
  const zip = new JSZip();
  const files = []

  for (const testcase of testcases) {
    if (publicOnly && testcase?.isPublic !== "Y") continue;

    files.push(new Blob(
      [
        "INPUT: \n" +
        testcase.testCase +
        "\n\nOUTPUT: \n" +
        testcase.correctAns,
      ],
      {type: "text/plain;charset=utf-8"}
    ))
  }

  if (files.length > 0) {
    files.forEach((file, index) => {
      zip.file(`Testcase-${index + 1}.txt`, file)
    })

    zip.generateAsync({type: 'blob'})
      .then(content => {
        // successNoti(files.length + " testcases downloaded", 2000);
        FileSaver.saveAs(content, 'testcases.zip');
      })
      .catch(e => {
        errorNoti('Error generating archive')
      });
  } else {
    warningNoti("No testcase satisfies the requirement", 2000);
  }
};
