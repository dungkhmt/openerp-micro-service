import React, {useEffect, useRef, useState} from "react";
import {errorNoti, infoNoti, successNoti} from "../../../../utils/notification";
import GenerateQuizTestGroupDialog from "./GenerateQuizTestGroupDialog";
import {isFunction, request} from "../../../../api";
import {Button, Card, CardContent} from "@mui/material";
import StandardTable from "../../../table/StandardTable";
import QuizTestGroupQuestionList from "../QuizTestGroupQuestionList";
import {toast} from "react-toastify";
import {pdf} from "@react-pdf/renderer";
import ExamQuestionsOfParticipantPDFDocument, {
  subPageTotalPagesState
} from "../template/ExamQuestionsOfParticipantPDFDocument";
import FileSaver from "file-saver";
import {makeStyles} from "@material-ui/core/styles";

const useStyles = makeStyles(theme => ({
  tableWrapper: {
    '& [class^=MTableToolbar-actions]>div>div>span>button': {
      padding: 'unset',
      paddingLeft: '8px'
    }
  }
}))

async function generatePdfDocument(documentData, fileName, onCompleted) {
  subPageTotalPagesState.set({
    fulfilled: false,
    totalPages: Array.from(new Array(documentData.length)),
  });

  // Calculate value for elements of subPageTotalPagesState
  await pdf(<ExamQuestionsOfParticipantPDFDocument data={documentData} />).toBlob();
  // Generate PDF only one time
  let done = false;

  // Spend time for subPageTotalPagesState to update new state
  const timer = setInterval(async () => {
    if (subPageTotalPagesState.fulfilled.get() && !done) {
      done = true;
      clearInterval(timer);

      const blob = await pdf(<ExamQuestionsOfParticipantPDFDocument data={documentData} />).toBlob();
      if (isFunction(onCompleted)) onCompleted();
      FileSaver.saveAs(blob, fileName);
    }
  }, 50);
}

export default function QuizGroupList(props) {
  const classes = useStyles();
  let testId = props.testId;
  const toastId = useRef(null);
  const [quizGroups, setQuizGroups] = useState([]);
  const [studentQuestions, setStudentQuestions] = useState();
  const [quizGroupIdsToDelete, setQuizGroupIdsToDelete] = useState([]);
  const [generateQuizGroupDlgOpen, setGenerateQuizGroupDlgOpen] = useState(false);

  useEffect(getQuizGroups, []);

  function getQuizGroups() {
    const successHandler = res => setQuizGroups(res.data);
    const errorHandlers = {
      onError: (error) => errorNoti("Đã xảy ra lỗi trong khi tải dữ liệu!", 3000)
    }
    request("GET", `/get-test-groups-info?testId=${testId}`, successHandler, errorHandlers);
  }

  async function getQuestionsOfParticipants() {
    let questionsOfParticipants;
    let successHandler = (res) => {
      questionsOfParticipants = res.data;
      setStudentQuestions(questionsOfParticipants);
    }
    const errorHandlers = {
      onError: () => errorNoti("Đã xảy ra lỗi trong khi tải dữ liệu", true)
    }

    await request("GET", `get-all-quiz-test-group-with-questions-detail/${testId}`,
                  successHandler, errorHandlers);
    return questionsOfParticipants;
  }

  function updateQuizGroupIdsToDelete(newSelectedGroups) {
    setQuizGroupIdsToDelete(newSelectedGroups.map(quizGroup => quizGroup.quizGroupId));
  }

  function deleteQuizGroups(deletedQuizGroupIds) {
    if (!deletedQuizGroupIds || deletedQuizGroupIds.length === 0) return;
    if (!window.confirm("Bạn có chắc muốn xóa những đề thi này không ?")) return;

    let formData = new FormData();
    formData.append("testId", testId);
    formData.append("quizTestGroupList", deletedQuizGroupIds.join(";"));

    const refreshQuizGroups = (res) => {
      if (res.data < 0) return;
      let remainingQuizGroups = quizGroups.filter(
        (el) => !deletedQuizGroupIds.includes(el.quizGroupId)
      );
      setQuizGroups(remainingQuizGroups);
    }
    const successHandler = (res) => {
      refreshQuizGroups(res);
      successNoti("Xóa đề thi thành công, xem kết quả trên giao diện!", 3000);
    }
    const errorHandlers = {
      onError: () => errorNoti("Đã xảy ra lỗi khi xóa đề thí!", 3000)
    }
    request("POST", "/delete-quiz-test-groups", successHandler, errorHandlers, formData);
  }

  async function exportExamQuestionsOfAllStudents() {
    let questionsOfStudents;

    if (!studentQuestions)
      questionsOfStudents = await getQuestionsOfParticipants();
    else questionsOfStudents = studentQuestions;

    const data = questionsOfStudents.map((quizGroupTestDetailModel) => ({
      userDetail: { id: " ", fullName: " " },
      ...quizGroupTestDetailModel,
    }));

    generatePdfDocument(data, `${testId}.pdf`, () => {
      toast.dismiss(toastId.current);
    });
  }

  const columns = [
    { field: "groupCode", title: "Mã đề" },
    { field: "note", title: "Ghi chú" },
    { field: "numStudent", title: "Số sinh viên" },
    { field: "numQuestion", title: "Số câu hỏi" },
    { field: "", title: "",
      render: (quizGroup) => (
        <DeleteQuizGroupButton deletedGroupIds={[quizGroup.quizGroupId]}
                               variant="outlined"/>
      )
    }
  ]

  const actions = [
    { icon: () => GenerateQuizGroupButton, isFreeAction: true },
    { icon: () => ButtonExportAllDataToPdf, isFreeAction: true },
    {
      icon: () => (
        <DeleteQuizGroupButton deletedGroupIds={quizGroupIdsToDelete}
                               variant="contained"/>
      )
    }
  ]

  const GenerateQuizGroupButton = (
    <Button color="primary"
            variant="contained"
            onClick={(_) => setGenerateQuizGroupDlgOpen(true)}>
      Thêm đề
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

  const DeleteQuizGroupButton = ({deletedGroupIds, variant}) => (
    <Button color="error"
            variant={variant}
            onClick={(_) => deleteQuizGroups(deletedGroupIds)}>
      Xóa
    </Button>
  )

  return (
    <>
      <Card>
        <CardContent className={classes.tableWrapper}>
          <StandardTable
            title="Danh sách đề thi"
            columns={columns}
            data={quizGroups}
            hideCommandBar
            options={{
              selection: true,
              search: true,
              sorting: true
            }}
            actions={actions}
            onSelectionChange={updateQuizGroupIdsToDelete}/>
        </CardContent>
      </Card>

      <QuizTestGroupQuestionList testId={testId} />

      <GenerateQuizTestGroupDialog
        testId={testId}
        onGenerateSuccess={getQuizGroups}
        open={generateQuizGroupDlgOpen}
        onClose={() => setGenerateQuizGroupDlgOpen(false)} />
    </>
  );
}
