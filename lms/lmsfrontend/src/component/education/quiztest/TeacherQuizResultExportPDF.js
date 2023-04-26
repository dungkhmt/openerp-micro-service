import htmlToPdfmake from "html-to-pdfmake";
import pdfMake from "pdfmake/build/pdfmake";
import pdfFonts from "pdfmake/build/vfs_fonts";

pdfMake.vfs = pdfFonts.pdfMake.vfs;

function header(testId) {
  return [
    {
      text: "Kết quả " + testId,
      bold: true,
      fontSize: 16,
      alignment: "center",
      margin: [0, 0, 0, 20],
    },
  ];
}

const questionContentDefaultStyles = {
  b: { bold: true, fontSize: 16 },
  strong: { bold: true, fontSize: 16 },
  u: { decoration: "underline", fontSize: 16, bold: true },
  s: { decoration: "lineThrough", bold: true, fontSize: 16 },
  em: { italics: true, bold: true, fontSize: 16 },
  i: { italics: true, bold: true },
  h1: { fontSize: 24, bold: true, marginBottom: 5 },
  h2: { fontSize: 22, bold: true, marginBottom: 5 },
  h3: { fontSize: 20, bold: true, marginBottom: 5 },
  h4: { fontSize: 18, bold: true, marginBottom: 5 },
  h5: { fontSize: 16, bold: true, marginBottom: 5 },
  h6: { fontSize: 14, bold: true, marginBottom: 5 },
  a: {
    color: "blue",
    decoration: "underline",
    bold: true,
    fontSize: 16,
  },
  strike: { decoration: "lineThrough", bold: true, fontSize: 16 },
  p: {
    margin: [0, 5, 10, 10],
    // bold: true,
    fontSize: 12,
  },
  ul: { marginBottom: 5, bold: true, fontSize: 16 },
  li: { marginLeft: 5, bold: true, fontSize: 16 },
  table: { marginBottom: 5, bold: true, fontSize: 16 },
  th: { bold: true, fillColor: "#EEEEEE", fontSize: 16 },
};

const answerDefaultStyles = {
  b: { bold: true },
  strong: { bold: true },
  u: { decoration: "underline" },
  s: { decoration: "lineThrough" },
  em: { italics: true },
  i: { italics: true },
  h1: { fontSize: 24, bold: true, marginBottom: 5 },
  h2: { fontSize: 22, bold: true, marginBottom: 5 },
  h3: { fontSize: 20, bold: true, marginBottom: 5 },
  h4: { fontSize: 18, bold: true, marginBottom: 5 },
  h5: { fontSize: 16, bold: true, marginBottom: 5 },
  h6: { fontSize: 14, bold: true, marginBottom: 5 },
  a: { color: "blue", decoration: "underline" },
  strike: { decoration: "lineThrough" },
  ul: { marginBottom: 5 },
  li: { marginLeft: 5 },
  table: { marginBottom: 5 },
  th: { bold: true, fillColor: "#EEEEEE" },
};

function resultDetailList(dataPdf) {
  let contentResult = [];

  dataPdf.map((resultDetail) => {
    let studentInfo = {
      text:
        "Họ và tên          " +
        resultDetail.fullName +
        "\n" +
        "Điểm                  " +
        resultDetail.totalGrade +
        "\n" +
        "Nhóm                " +
        resultDetail.groupId +
        "\n" +
        "Code                " +
        resultDetail.quizGroupCode +
        "\n\n" +
        "Chi tiết bài làm:",
      fontSize: 13,
      width: "auto",
      margin: [0, 3, 5, 5],
      padding: [0, 0, 8, 0],
    };

    let studentResult = [];
    studentResult.push(htmlToPdfmake(`<hr/>`));
    studentResult.push(studentInfo);

    //
    const { listQuestion } = resultDetail;
    listQuestion.map((question, index) => {
      const { content, listAnswer, grade, listchooseAns } = question;
      //
      let questionContent = htmlToPdfmake(
        `<br/><p>Câu ${index + 1}. ${content.slice(3)}`,
        {
          defaultStyles: questionContentDefaultStyles,
        }
      );

      console.log(questionContent);

      let questionDetail = [];
      questionDetail.push(questionContent);

      //
      listAnswer.forEach((ans, index) => {
        let pele = {
          margin: [0, 5, 0, 10],
          bold: true,
          display: "inline",
        };
        let ansContent = ans.choiceAnswerContent;

        if (ans.isCorrectAnswer === "Y") {
          // Add check-mark symbols
          ansContent = "<p>√ " + ansContent.substring(3);

          if (listchooseAns.includes(ans.choiceAnswerId)) {
            pele["color"] = "blue";
            pele["decoration"] = "underline";
          } else {
            pele["color"] = "green";
          }
        } else {
          pele["margin"] = [10, 5, 0, 10];

          if (listchooseAns.includes(ans.choiceAnswerId)) {
            pele["color"] = "red";
            pele["decoration"] = "underline";
          } else {
            pele["bold"] = false;
          }
        }

        questionDetail.push(
          htmlToPdfmake(ansContent, {
            defaultStyles: { ...answerDefaultStyles, p: pele },
          })
        );
      });

      //
      let quesGrade = {
        text: `Điểm: ${grade}`,
        style: "itemGrade",
      };
      questionDetail.push(quesGrade);

      studentResult.push(questionDetail);
    });

    contentResult.push(studentResult);
  });

  return contentResult;
  // {
  //   content: [
  //     [
  //          {
  //              text: "\n Họ và tên: "+{}+  "Điểm: "+ 3 +"\n Nhóm"+ 1+"\n Chi tiết bài làm:",
  //              fontSize: 18,
  //              width: 'auto',
  //          },
  //           {
  //              text:'-----------------------------------------------------------------------------------------------------------------------------------------------------------',
  //              margin: [0, 5, 0, 5],
  //              width: 'auto',
  //          },
  //         [
  //            [
  //                {
  //                    text: "Câu 1. Đệ quy có nhớ có cần sử dụng  bộ nhớ ko?",
  //                    style: 'itemQuestion'
  //                },
  //                {
  //                    text: "Có",
  //                    style: 'itemchoose'
  //                },
  //                {
  //                    text: "Không",
  //                    style: 'itemText'
  //                },
  //                {
  //                    text: "Điểm : 15",
  //                    style: 'itemGrade'
  //                }
  //            ],
  //            [
  //                {
  //                    text: "Câu 2. Đệ quy có nhớ có cần sử dụng  bộ nhớ ko?",
  //                    style: 'itemQuestion'
  //                },
  //                {
  //                    text: "Có",
  //                    style: 'itemtext'
  //                },
  //                {
  //                    text: "Không",
  //                    style: 'itemchoose'
  //                },
  //                {
  //                    text: "Điểm : 15",
  //                    style: 'itemGrade'
  //                }
  //            ]

  //         ],
  //     ],
  //     '\n\n',
  //   ]
  // }
}

function resultList(students) {
  return {
    columns: [
      { width: "*", text: "" },
      {
        width: "auto",
        table: {
          // headers are automatically repeated if the table spans over multiple pages
          // you can declare how many rows should be treated as headers
          headerRows: 1,
          widths: ["auto", "auto", "auto"],
          body: [
            // Table Header
            [
              {
                text: "Họ và tên",
                style: ["itemsHeader", "center"],
              },
              {
                text: "Nhóm",
                style: ["itemsHeader", "center"],
              },
              {
                text: "Điểm",
                style: ["itemsHeader", "center"],
              },
            ],
            // Items
            // Item 1
            ...students.map((student) => [
              {
                text: student.fullName,
                style: "itemText",
              },
              {
                text: student.groupId,
                style: "itemText",
              },
              {
                text: student.grade.toString(),
                style: "itemText",
              },
            ]),
          ],
        },
      },
      { width: "*", text: "" },
    ],
  };
}

let styles = {
  // Items Header
  itemsHeader: {
    margin: [0, 5, 0, 5],
    bold: true,
    alignment: "center",
  },
  itemNumber: {
    margin: [0, 5, 0, 5],
    alignment: "center",
  },
  itemText: {
    margin: [0, 5, 0, 5],
    alignment: "center",
  },
  itemAns: {
    margin: [0, 5, 0, 5],
    alignment: "left",
  },
  center: {
    alignment: "center",
  },
  itemQuestion: {
    margin: [0, 5, 0, 5],
    bold: true,
    fontSize: 16,
    alignment: "left",
  },
  itemGrade: {
    margin: [0, 5, 0, 5],
    italics: true,
    alignment: "left",
  },
};

export function exportResultListPdf(
  studentListResult,
  resultExportPDFData,
  testId
) {
  console.log("export pdf result ", studentListResult);

  studentListResult.sort(function (firstEl, secondEl) {
    if (firstEl.fullName === null || secondEl.fullName === null) return -1;
    if (firstEl.fullName.toLowerCase() < secondEl.fullName.toLowerCase()) {
      return -1;
    }
    if (firstEl.fullName.toLowerCase() > secondEl.fullName.toLowerCase()) {
      return 1;
    }

    return 0;
  });

  let resultData = [];
  studentListResult.map((student, index) => {
    let tmp = {};
    tmp.fullName = student.fullName;
    tmp.groupId = student.groupId;
    tmp.quizGroupCode = student.quizGroupCode;
    tmp.grade = student.grade;
    resultData.push(tmp);
  });

  let dataDetail = resultDetailList(resultExportPDFData);
  console.log(dataDetail);
  const docDefinitions = {
    // a string or { width: number, height: number }
    pageSize: "A4",

    // by default we use portrait, you can change it to landscape if you wish
    pageOrientation: "portrait",
    // [left, top, right, bottom] or [horizontal, vertical] or just a number for equal margins
    pageMargins: [40, 20, 20, 40],
    footer: function (currentPage, pageCount) {
      return [
        {
          text: "Trang " + currentPage.toString(),
          alignment: "right",
          margin: [0, 0, 20, 0],
        },
      ];
    },
    // header: function(currentPage, pageCount, pageSize) {
    //     // you can apply any logic and return any valid pdfmake element
    //     return [
    //     { text: 'simple text', alignment: (currentPage % 2) ? 'left' : 'right' },
    //     { canvas: [ { type: 'rect', x: 170, y: 32, w: pageSize.width - 170, h: 40 } ] }
    //     ]
    // },
    content: [
      ...header(testId),

      "\n\n",
      resultList(resultData),
      "\n\n",
      dataDetail,
    ],
    styles: styles,
    defaultStyle: {
      columnGap: 20,
    },
  };

  pdfMake
    .createPdf(docDefinitions)
    .download("Danh sách kết quả test  " + testId + ".pdf");
}
