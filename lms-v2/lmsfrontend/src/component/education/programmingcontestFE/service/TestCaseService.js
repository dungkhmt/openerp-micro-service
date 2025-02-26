import FileSaver from "file-saver";
import {errorNoti, infoNoti, successNoti, warningNoti} from "utils/notification";
import JSZip from "jszip";

// export const copyAllTestCases = (testCases) => {
//   let allTestCases = "";
//   for (const testCase_ith of testCases) {
//     allTestCases +=
//       "------------- \nINPUT: \n" +
//       testCase_ith.testCase +
//       "\n\nOUTPUT: \n" +
//       testCase_ith.correctAns +
//       "\n\n";
//   }
//   if (allTestCases.length > 0)
//     navigator.clipboard.writeText(allTestCases).then(() => successNoti("Copied", 2000));
//   else warningNoti("No testcase satisfies the requirement", 2000);
// };

export const copyTestCasesWithPublicMode = (testCases, t) => {
  let allTestCases = "";
  for (const testCase of testCases) {
    allTestCases +=
      "------------- \n" + t('input') + ": \n" +
      testCase.testCase +
      "\n\n" + t('output') + ": \n" +
      testCase.correctAns +
      "\n\n";
  }

  if (allTestCases.length > 0) {
    navigator.clipboard.writeText(allTestCases).then(() => successNoti(t("copied")));
  } else {
    infoNoti(t('noTestCase'), 3000);
  }
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

export const downloadAllTestCasesWithPublicMode = (testcases, t) => {
  const zip = new JSZip();
  const files = []

  for (const testcase of testcases) {
    files.push(new Blob(
      [
        t('input') + ": \n" +
        testcase.testCase +
        "\n\n" + t('output') + ": \n" +
        testcase.correctAns,
      ],
      {type: "text/plain;charset=utf-8"}
    ))
  }

  if (files.length > 0) {
    files.forEach((file, index) => {
      zip.file(`TestCase-${index + 1}.txt`, file)
    })

    zip.generateAsync({type: 'blob'})
      .then(content => {
        FileSaver.saveAs(content, 'testcases.zip');
      })
      .catch(e => {
        errorNoti(t("common:error"))
      });
  } else {
    infoNoti(t('noTestCase'), 3000);
  }
};
