import {IconButton} from "@mui/material";
import {pdf} from "@react-pdf/renderer";
import {isFunction, request} from "api";
import FileSaver from "file-saver";
import MaterialTable from "material-table";
import {useEffect, useRef, useState} from "react";
import {VscFilePdf} from "react-icons/vsc";
import {Link} from "react-router-dom";
import {toast} from "react-toastify";
import {infoNoti} from "utils/notification";
import ExamQuestionsOfParticipantPDFDocument from "./template/ExamQuestionsOfParticipantPDFDocument";

const generatePdfDocument = async (documentData, fileName, onCompleted) => {
  const blob = await pdf(
    <ExamQuestionsOfParticipantPDFDocument data={documentData} />
  ).toBlob();

  if (isFunction(onCompleted)) onCompleted();

  FileSaver.saveAs(blob, fileName);
};

function QuizTestGroupParticipants(props) {
  let testId = props.testId;

  //
  const toastId = useRef(null);

  //
  const [participants, setParticipants] = useState([]);
  const [studentQuestions, setStudentQuestions] = useState();
  const [resultExportPDFData, setResultExportPDFData] = useState([]);

  //
  const columns = [
    { title: "Group Id", field: "quizTestGroupId" },
    { title: "Group Code", field: "quizTestGroupCode" },
    { title: "UserLoginId", field: "participantUserLoginId" },
    { title: "FullName", field: "fullName" },
    {
      title: "View Question",
      field: "quizTestGroupId",
      render: (rowData) => (
        <Link
          to={`/edu/class/quiztest/teacher-view-questions-of-participant/${rowData["participantUserLoginId"]}/${rowData["quizTestGroupId"]}/${testId}`}
        >
          VIEW
        </Link>
      ),
    },
    {
      render: (rowData) => (
        <IconButton
          color="primary"
          aria-label="export PDF"
          onClick={() => {
            exportExamQuestions(rowData["participantUserLoginId"]);
          }}
        >
          <VscFilePdf />
        </IconButton>
      ),
    },
  ];

  //
  const exportExamQuestions = (studentId) => {
    request(
      "GET",
      `/get-quiz-questions-assigned-to-participant/${testId}/${studentId}`,
      (res1) => {
        const { courseName } = res1.data;

        request("GET", `/get-user-detail/${studentId}`, (res2) => {
          const userInfo = res2.data;

          generatePdfDocument(
            [
              {
                userDetail: {
                  id: studentId,
                  fullName: `${userInfo?.firstName} ${userInfo?.middleName} ${userInfo?.lastName}`,
                },
                ...res1.data,
              },
            ],
            `${testId} - ${courseName} - ${studentId}.pdf`
          );
        });
      }
    );
  };

  const exportExamQuestionsOfAllStudents = async () => {
    let questionsOfStudents;

    if (!studentQuestions)
      questionsOfStudents = await getQuestionsOfParticipants();
    else questionsOfStudents = studentQuestions;

    const data = questionsOfStudents.map(
      ({ participantUserLoginId, fullName, quizGroupTestDetailModel }) => ({
        userDetail: { id: participantUserLoginId, fullName: fullName },
        ...quizGroupTestDetailModel,
      })
    );

    generatePdfDocument(data, `${testId}.pdf`, () => {
      toast.dismiss(toastId.current);
    });
  };

  function getGeneralResult() {
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
            listAnswer: elm.quizChoiceAnswerList,
            listchooseAns: elm.chooseAnsIds,
          };

          if (objectPdf[elm.participationUserLoginId] == null) {
            let userObj = {
              fullName: elm.participationFullName,
              groupId: elm.quizGroupId,
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

        // console.log(objectPdf);
        Object.keys(objectPdf).map((ele) => {
          dataPdf.push(objectPdf[ele]);
        });

        // console.log(dataPdf);

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
        console.log("dataPdf = ", dataPdf);

        setResultExportPDFData(dataPdf);
      },
      {},
      input
    );
  }

  async function getQuestionsOfParticipants() {
    let data;

    await request(
      "get",
      "/get-all-quiz-test-participation-group-question/" + testId,
      (res) => {
        data = res.data;
        setStudentQuestions(data);
      },
      { 401: () => {} }
    );

    return data;
  }

  function getParticipants() {
    request(
      "get",
      "get-all-quiz-test-group-participants/" + testId,
      (res) => {
        //alert('assign students to groups OK');
        setParticipants(res.data);
      },
      { 401: () => {} }
    );
  }

  useEffect(() => {
    getParticipants();
    getGeneralResult();
    // getQuestionsOfParticipants();
  }, []);

  return (
      <MaterialTable
        title={"Phân thí sinh vào các đề"}
        columns={columns}
        data={participants}
        actions={[
          {
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
              toastId.current = infoNoti("Hệ thống đang chuẩn bị tệp PDF ...");
              exportExamQuestionsOfAllStudents();
              // exportQuizQuestionAssigned2StudentPdf(
              //   //students,
              //   participants,
              //   resultExportPDFData,
              //   studentQuestions,
              //   testId
              // );
            },
          },
        ]}
      />
  );
}

export default QuizTestGroupParticipants;
