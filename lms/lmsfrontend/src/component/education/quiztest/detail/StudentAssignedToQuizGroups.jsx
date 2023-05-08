import {pdf} from "@react-pdf/renderer";
import {isFunction, request} from "api";
import FileSaver from "file-saver";
import React, {useEffect, useRef, useState} from "react";
import {toast} from "react-toastify";
import {errorNoti, infoNoti} from "utils/notification";
import ExamQuestionsOfParticipantPDFDocument from "../template/ExamQuestionsOfParticipantPDFDocument";
import {Button, Card, CardContent} from '@mui/material'
import {useHistory} from "react-router";
import StandardTable from "../../../table/StandardTable";
import {makeStyles} from "@material-ui/core/styles";

const useStyles = makeStyles((theme) => ({
    tableWrapper: {
      '& table thead tr': {
        '& th:nth-child(2)': {
          maxWidth: '160px !important'
        }
      }
    }
}))

async function generatePdfDocument(documentData, fileName, onCompleted) {
  const blob = await pdf(
    <ExamQuestionsOfParticipantPDFDocument data={documentData} />
  ).toBlob();

  if (isFunction(onCompleted)) onCompleted();

  FileSaver.saveAs(blob, fileName);
}

export default function StudentAssignedToQuizGroups(props) {
  const history = useHistory();
  const classes = useStyles();
  let testId = props.testId;
  const toastId = useRef(null);
  const [participantsOfQuizGroups, setParticipantsOfQuizGroups] = useState([]);
  const [studentQuestions, setStudentQuestions] = useState();

  useEffect(getParticipantsOfQuizGroups, []);

  function getParticipantsOfQuizGroups() {
    let successHandler = res => setParticipantsOfQuizGroups(res.data);
    let errorHandlers = {
      onError: (error) => errorNoti("Đã xảy ra lỗi trong khi tải dữ liệu!", 3000)
    }
    request("GET", `get-all-quiz-test-group-participants/${testId}`, successHandler, errorHandlers);
  }

  function navigateToGroupParticipantQuestionsPage({ participantUserLoginId, quizTestGroupId }) {
    let url = `/edu/class/quiztest/teacher-view-questions-of-participant/${participantUserLoginId}/${quizTestGroupId}/${testId}`;
    history.push(url);
  }

  async function exportExamQuestionsOfStudent(studentId) {
    const [userInfoResponse, studentTestDetailResponse] = await Promise.all([
      request("GET", `/get-user-detail/${studentId}`),
      request("GET", `/get-quiz-questions-assigned-to-participant/${testId}/${studentId}`)
    ]);

    let userInfo = userInfoResponse.data;
    let studentTestDetail = studentTestDetailResponse.data;
    let pdfFileName = `${testId} - ${studentTestDetail.courseName} - ${studentId}.pdf`
    let userFullName = `${userInfo?.firstName} ${userInfo?.middleName} ${userInfo?.lastName}`
    const pdfExportedData = [{
      userDetail: { id: studentId, fullName: userFullName },
      ...studentTestDetail
    }];

    generatePdfDocument(pdfExportedData, pdfFileName, )
  }

  async function exportExamQuestionsOfAllStudents() {
    // Using in-memory cache to avoid api call
    let questionsOfStudents = studentQuestions ?? await getQuestionsOfParticipants();

    const pdfExportedData = questionsOfStudents.map(
      ({ participantUserLoginId, fullName, quizGroupTestDetailModel }) => ({
        userDetail: { id: participantUserLoginId, fullName: fullName },
        ...quizGroupTestDetailModel,
      })
    );

    generatePdfDocument(pdfExportedData, `${testId}.pdf`, () => toast.dismiss(toastId.current))
  }

  async function getQuestionsOfParticipants() {
    let data;
    let successHandler = res => {
      data = res.data;
      setStudentQuestions(data);
    };
    await request("GET", `get-all-quiz-test-participation-group-question/${testId}`, successHandler);
    return data;
  }

  const ViewGroupParticipantQuestionsButton = ({ groupParticipant }) => (
    <Button color="primary" variant="outlined"
            onClick={() => navigateToGroupParticipantQuestionsPage(groupParticipant)}>
      Detail
    </Button>
  )

  const ButtonExportSingleDataToPdf = ({ participantLoginId }) => (
    <Button color="primary" variant="outlined"
            onClick={() => exportExamQuestionsOfStudent(participantLoginId) }>
      Export PDF
    </Button>
  )

  const ButtonExportAllDataToPdf = (
    <Button color="primary" variant="contained"
            onClick={() => {
              toastId.current = infoNoti("Hệ thống đang chuẩn bị tệp PDF ...");
              exportExamQuestionsOfAllStudents();
            }}>
      Export PDF
    </Button>
  );

  const actions = [{ icon: () => ButtonExportAllDataToPdf, isFreeAction: true }]

  const columns = [
    { title: "Group ID", field: "quizTestGroupId" },
    { title: "Mã đề", field: "quizTestGroupCode", cellStyle: { width: '160px' } },
    { title: "Login ID", field: "participantUserLoginId" },
    { title: "Họ tên", field: "fullName" },
    { title: "", field: "",
      render: (groupParticipant) => (
        <div style={{ display: 'flex', columnGap: '10px' }}>
          <ViewGroupParticipantQuestionsButton groupParticipant={groupParticipant}/>
          <ButtonExportSingleDataToPdf participantLoginId={groupParticipant.participantUserLoginId}/>
        </div>
      )
    }
  ];

  return (
    <Card>
      <CardContent className={classes.tableWrapper}>
        <StandardTable
          title="Phân thí sinh vào các đề"
          columns={columns}
          data={participantsOfQuizGroups}
          hideCommandBar
          options={{
            selection: false,
            search: true,
            sorting: true,
          }}
          actions={actions}
        />
      </CardContent>
    </Card>
  );
}