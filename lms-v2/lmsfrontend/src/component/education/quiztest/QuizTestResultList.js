//import IconButton from '@material-ui/core/IconButton';
import MaterialTable from "material-table";
import {useEffect, useState} from "react";
import ReactExport from "react-data-export";
import {request} from "../../../api";
import {toFormattedDateTime} from "../../../utils/dateutils";
import {localization} from "../../../utils/MaterialTableUtils";
import {exportResultListPdf} from "./TeacherQuizResultExportPDF.js";
import ViewHistoryLogQuizGroupQuestionParticipationExecutionChoice
  from "./ViewHistoryLogQuizGroupQuestionParticipationExecutionChoice";

const ExcelFile = ReactExport.ExcelFile;
const ExcelSheet = ReactExport.ExcelFile.ExcelSheet;

// const useStyles = makeStyles({
//   table: {
//     minWidth: 700,
//   },
// });

const headerProperties = {
  headerStyle: {
    fontSize: 16,
    backgroundColor: "rgb(63, 81, 181)",
    color: "white",
  },
};

export default function QuizTestStudentListResult(props) {
  // const classes = useStyles();

  //
  const [studentListResult, setStudentListResult] = useState([]);
  const [resultExportPDFData, setResultExportPDFData] = useState([]);
  const [fetchedResult, setfetchedResults] = useState(false);

  //
  let testId = props.testId;
  let isGeneral = props.isGeneral;

  const TableBorderStyle = "medium";
  const TableHeaderStyle = {
    style: {
      font: { sz: "14", bold: true },
      alignment: { vertical: "center", horizontal: "center" },
      border: {
        top: { style: TableBorderStyle },
        bottom: { style: TableBorderStyle },
        left: { style: TableBorderStyle },
        right: { style: TableBorderStyle },
      },
    },
  };

  const TableCellStyle = {
    style: {
      font: { sz: "14" },
      alignment: { vertical: "center", horizontal: "center" },
      border: {
        top: { style: TableBorderStyle },
        bottom: { style: TableBorderStyle },
        left: { style: TableBorderStyle },
        right: { style: TableBorderStyle },
      },
    },
  };

  const DataSet = [
    {
      columns: [
        {
          title: "UserLoginID",
          ...TableHeaderStyle,
          width: { wch: "50" },
        },
        {
          title: "Họ và tên",
          ...TableHeaderStyle,
          width: { wch: "50" },
        },
        {
          title: "Nhóm",
          ...TableHeaderStyle,
          width: { wch: "35" },
        },
        {
          title: "Điểm",
          ...TableHeaderStyle,
          width: { wch: "25" },
        },
      ],
      data: !fetchedResult
        ? []
        : studentListResult.map((student) => {
            return [
              {
                value: student.userLoginId,
                ...TableCellStyle,
              },
              {
                value: student.fullName,
                ...TableCellStyle,
              },
              {
                value: student.groupId,
                ...TableCellStyle,
              },
              {
                value: student.grade,
                ...TableCellStyle,
              },
            ];
          }),
    },
  ];

  const columns = [
    {
      field: "userLoginId",
      title: "MSSV",
      ...headerProperties,
    },
    {
      field: "fullName",
      title: "Họ và tên",
      ...headerProperties,
      width: "40%",
    },
    {
      field: "groupId",
      title: "Group",
      ...headerProperties,
    },
    {
      field: "questionId",
      title: "Question ID",
      ...headerProperties,
    },
    {
      field: "grade",
      title: "Điểm",
      ...headerProperties,
    },
    {
      field: "createdStamp",
      title: "Thời gian submit",
      ...headerProperties,
    },
  ];

  const generalColumns = [
    {
      field: "userLoginId",
      title: "UserLoginID",
      ...headerProperties,
      width: "40%",
    },
    {
      field: "fullName",
      title: "Họ và tên",
      ...headerProperties,
      width: "40%",
    },
    {
      field: "groupId",
      title: "Group",
      ...headerProperties,
    },
    {
      field: "grade",
      title: "Điểm",
      ...headerProperties,
    },
  ];

  async function getStudentListResultGeneral() {
    let input = { testId: testId };

    request(
      "POST",
      "/get-quiz-test-participation-execution-result",
      (res) => {
        let dataPdf = [];
        let objectPdf = {};

        res.data.map((elm) => {
          let question = {
            content: elm.questionContent,
            grade: elm.grade,
            quizGroupCode: elm.quizGroupCode,
            listAnswer: elm.quizChoiceAnswerList,
            listchooseAns: elm.chooseAnsIds,
          };

          if (objectPdf[elm.participationUserLoginId] == null) {
            let userObj = {
              fullName: elm.participationFullName,
              groupId: elm.quizGroupId,
              quizGroupCode: elm.quizGroupCode,
              listQuestion: [],
              totalGrade: elm.grade,
            };
            objectPdf[elm.participationUserLoginId] = userObj;
          } else {
            objectPdf[elm.participationUserLoginId]["totalGrade"] += elm.grade;
          }

          objectPdf[elm.participationUserLoginId]["listQuestion"].push(
            question
          );
        });

        console.log("objectPDF = ", objectPdf);
        Object.keys(objectPdf).map((ele) => {
          dataPdf.push(objectPdf[ele]);
        });

        console.log("dataPDF = ", dataPdf);

        dataPdf.sort(function (firstEl, secondEl) {
          if (firstEl.fullName === null || secondEl.fullName === null)
            return -1;
          if (
            firstEl.fullName.toLowerCase() < secondEl.fullName.toLowerCase()
          ) {
            return -1;
          }
          if (
            firstEl.fullName.toLowerCase() > secondEl.fullName.toLowerCase()
          ) {
            return 1;
          }

          return 0;
        });
        //after sort
        // console.log(dataPdf);

        setResultExportPDFData(dataPdf);

        let temp = [];
        let objectResult = {};
        res.data.map((elm, index) => {
          if (objectResult[elm.participationUserLoginId] == null) {
            let userObj = {
              groupId: elm.quizGroupId,
              quizGroupCode: elm.quizGroupCode,
              fullName: elm.participationFullName,
              grade: elm.grade,
              userLoginId: elm.participationUserLoginId,
            };
            objectResult[elm.participationUserLoginId] = userObj;
          } else {
            objectResult[elm.participationUserLoginId]["grade"] += elm.grade;
          }
        });
        Object.keys(objectResult).map((ele, ind) => {
          temp.push(objectResult[ele]);
        });

        // console.log(temp);

        setStudentListResult(temp);
        setfetchedResults(true);
      },
      {},
      input
    );
  }

  async function getStudentListResult() {
    let input = { testId: testId };

    request(
      // token,
      // history,
      "Post",
      "/get-quiz-test-participation-execution-result",
      (res) => {
        let temp = [];
        res.data.map((elm, index) => {
          temp.push({
            userLoginId: elm.participationUserLoginId,
            fullName: elm.participationFullName,
            groupId: elm.quizGroupId,
            quizGroupCode: elm.quizGroupCode,
            questionId: elm.questionId,
            grade: elm.grade,
            createdStamp: elm.createdStamp
              ? toFormattedDateTime(elm.createdStamp)
              : "",
          });
        });
        setStudentListResult(temp);
      },
      {},
      input
    );
  }

  useEffect(() => {
    if (isGeneral) {
      getStudentListResultGeneral();
    } else {
      getStudentListResult();
    }

    return () => {};
  }, []);

  return (
    <>
      <MaterialTable
        title=""
        columns={isGeneral ? generalColumns : columns}
        data={studentListResult}
        //icons={tableIcons}
        localization={localization}
        options={{
          search: true,
          actionsColumnIndex: 2,
          pageSize: 10,
          tableLayout: "fixed",
          //selection: true
        }}
        actions={[
          studentListResult.length === 0 || !isGeneral
            ? null
            : {
                icon: () => (
                  <ExcelFile
                    filename={"Danh sách kết quả " + testId}
                    element={
                      <img
                        alt="Xuất Excel"
                        src="/static/images/icons/excel_icon.png"
                        style={{ width: "35px", height: "35px" }}
                      />
                    }
                  >
                    <ExcelSheet
                      dataSet={DataSet}
                      name={"Danh sách kết quả " + testId}
                    />
                  </ExcelFile>
                ),
                tooltip: "Xuất Excel",
                isFreeAction: true,
              },
          studentListResult.length === 0 || !isGeneral
            ? null
            : {
                icon: () => (
                  <img
                    alt="Xuất PDF"
                    src="/static/images/icons/pdf_icon.png"
                    style={{ width: "35px", height: "35px" }}
                  />
                ),
                tooltip: "Xuất PDF",
                isFreeAction: true,
                onClick: () => {
                  exportResultListPdf(
                    studentListResult,
                    resultExportPDFData,
                    testId
                  );
                },
              },
        ]}
      />
      <ViewHistoryLogQuizGroupQuestionParticipationExecutionChoice
        testId={testId}
      />
    </>
  );
}
