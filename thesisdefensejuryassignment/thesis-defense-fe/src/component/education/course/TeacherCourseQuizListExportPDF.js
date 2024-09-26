import pdfMake from "pdfmake/build/pdfmake";
import pdfFonts from "pdfmake/build/vfs_fonts";

pdfMake.vfs = pdfFonts.pdfMake.vfs;

function header(courseId) {
  return [
    {
      text: "Danh sách câu hỏi môn " + courseId,
      bold: true,
      fontSize: 16,
      alignment: "center",
      margin: [0, 0, 0, 20],
    },
  ];
}

function quizItems(quizs) {
  return {
    table: {
      // headers are automatically repeated if the table spans over multiple pages
      // you can declare how many rows should be treated as headers
      headerRows: 1,
      widths: ["auto", "auto", "auto", "auto", "auto"],

      body: [
        // Table Header
        [
          {
            text: "QuestionId",
            style: "itemsHeader",
          },
          {
            text: "Level",
            style: ["itemsHeader", "center"],
          },
          {
            text: "Status",
            style: ["itemsHeader", "center"],
          },
          {
            text: "Topic",
            style: ["itemsHeader", "center"],
          },
          {
            text: "Created date",
            style: ["itemsHeader", "center"],
          },
        ],
        // Items
        // Item 1
        ...quizs.map((quiz) => [
          {
            text: quiz.questionId,
            style: "itemText",
          },
          {
            text: quiz.levelId,
            style: "itemText",
          },
          {
            text: quiz.statusId ? quiz.statusId : "",
            style: "itemText",
          },
          {
            text: quiz.quizCourseTopic.quizCourseTopicId,
            style: "itemText",
          },
          {
            text: quiz.createdStamp ? quiz.createdStamp : "",
            style: "itemNumber",
          },
        ]),
      ],
    },
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
  center: {
    alignment: "center",
  },
};

export function exportQuizsListPdf(quizs, courseId) {
  const docDefinition = {
    // a string or { width: number, height: number }
    pageSize: "A4",

    // by default we use portrait, you can change it to landscape if you wish
    pageOrientation: "landscape",
    // [left, top, right, bottom] or [horizontal, vertical] or just a number for equal margins
    pageMargins: [20, 20, 20, 20],
    footer: function (currentPage, pageCount) {
      return [
        {
          text: "Page " + currentPage.toString() + " of " + pageCount,
          alignment: "center",
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
    content: [...header(courseId), "\n\n", quizItems(quizs)],
    styles: styles,
    defaultStyle: {
      columnGap: 20,
    },
  };
  pdfMake
    .createPdf(docDefinition)
    .download("Danh sách câu hỏi môn " + courseId + ".pdf");
}
