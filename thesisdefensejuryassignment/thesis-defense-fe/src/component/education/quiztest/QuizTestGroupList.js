import {Button, Checkbox, TextField, Tooltip, Typography,} from "@material-ui/core/";
import {blue, green, grey} from "@material-ui/core/colors";
import {createTheme, makeStyles, ThemeProvider,} from "@material-ui/core/styles";
import {Delete} from "@material-ui/icons";
import AddCircleOutlineIcon from "@material-ui/icons/AddCircleOutline";
import {pdf} from "@react-pdf/renderer";
import {isFunction, request} from "api";
import FileSaver from "file-saver";
import MaterialTable from "material-table";
import {useEffect, useReducer, useRef, useState} from "react";
import {toast} from "react-toastify";
import SimpleBar from "simplebar-react";
import {infoNoti} from "utils/notification";
import PrimaryButton from "../../button/PrimaryButton";
import TertiaryButton from "../../button/TertiaryButton";
import CustomizedDialogs from "../../dialog/CustomizedDialogs";
import ErrorDialog from "../../dialog/ErrorDialog";
import QuizTestGroupQuestionList from "./QuizTestGroupQuestionList";
import ExamQuestionsOfParticipantPDFDocument, {
  subPageTotalPagesState
} from "./template/ExamQuestionsOfParticipantPDFDocument";

export const style = (theme) => ({
  testBtn: {
    marginLeft: 40,
    marginTop: 32,
  },
  wrapper: {
    padding: "32px 0px",
  },
  answerWrapper: {
    "& label": {
      "&>:nth-child(2)": {
        display: "inline-block",
        "& p": {
          margin: 0,
          textAlign: "justify",
        },
      },
    },
  },
  answer: {
    width: "100%",
    marginTop: 20,
  },
  quizStatement: {
    fontSize: "1rem",
    "&>p:first-of-type": {
      display: "inline",
    },
  },
  list: {
    paddingBottom: 0,
    width: 330,
  },
  dialogContent: { paddingBottom: theme.spacing(1), width: 362 },
  listItem: {
    borderRadius: 6,
    "&:hover": {
      backgroundColor: grey[200],
    },
    "&.Mui-selected": {
      backgroundColor: blue[500],
      color: theme.palette.getContrastText(blue[500]),
      "&:hover": {
        backgroundColor: blue[500],
      },
    },
  },
  btn: {
    textTransform: "none",
  },
});

const useStyles = makeStyles((theme) => style(theme));

const headerProperties = {
  headerStyle: {
    fontSize: 16,
    backgroundColor: "rgb(63, 81, 181)",
    color: "white",
  },
};

let count = 0;

export const generatePdfDocument = async (
  documentData,
  fileName,
  onCompleted
) => {
  subPageTotalPagesState.set({
    fulfilled: false,
    totalPages: Array.from(new Array(documentData.length)),
  });

  // Calculate value for elements of subPageTotalPagesState
  await pdf(
    <ExamQuestionsOfParticipantPDFDocument data={documentData} />
  ).toBlob();

  // Generate PDF only one time
  let done = false;

  // Spend time for subPageTotalPagesState to update new state
  const timer = setInterval(async () => {
    if (subPageTotalPagesState.fulfilled.get() && !done) {
      // console.log("I AM HERE");
      done = true;
      clearInterval(timer);

      // Generate and save file
      const blob = await pdf(
        <ExamQuestionsOfParticipantPDFDocument data={documentData} />
      ).toBlob();

      if (isFunction(onCompleted)) onCompleted();

      FileSaver.saveAs(blob, fileName);
    }
  }, 50);

  // console.log("subPageTotalPagesState = " + subPageTotalPagesState.get());
};

export default function QuizTestGroupList(props) {
  // const classes = useStyles();
  const toastId = useRef(null);

  const [studentQuestions, setStudentQuestions] = useState();
  const [, forceUpdate] = useReducer((x) => x + 1, 0);

  const [selectedAll, setSelectedAll] = useState(false);

  const [open, setOpen] = useState(false);
  const [error, setError] = useState(false);
  const theme = createTheme({
    palette: {
      primary: green,
    },
  });
  const classes = useStyles();

  const columns = [
    {
      field: "groupCode",
      title: "Mã đề",
      ...headerProperties,
    },
    {
      field: "note",
      title: "Ghi chú",
      ...headerProperties,
      width: "40%",
    },
    {
      field: "numStudent",
      title: "Số sinh viên",
      ...headerProperties,
      type: "numeric",
    },
    {
      field: "numQuestion",
      title: "Số câu hỏi",
      ...headerProperties,
      type: "numeric",
    },
    {
      field: "selected",
      title: "    Chọn",
      ...headerProperties,
      width: "10%",
      type: "numeric",
      render: (rowData) => (
        <Checkbox
          checked={rowData.selected}
          onChange={(e) => {
            rowData.selected = e.target.checked;
            if (rowData.selected == false) {
              count--;
              setSelectedAll(false);
            } else {
              count++;
            }
            if (count == groupList.length) {
              setSelectedAll(true);
            }
            forceUpdate();
          }}
        />
      ),
    },
  ];

  let testId = props.testId;

  const [groupList, setGroupList] = useState([]);
  const [numberGroups, setNumberGroups] = useState(1);

  const onOpenDialog = () => {
    setOpen(true);
  };
  const handleClose = () => {
    setOpen(false);
  };
  const handleChangeNumberGroups = (event) => {
    setNumberGroups(event.target.value);
  };

  async function getQuestionsOfParticipants() {
    let data;

    await request(
      "get",
      //"get-all-quiz-test-participation-group-question/" + testId,
      "get-all-quiz-test-group-with-questions-detail/" + testId,
      (res) => {
        data = res.data;
        setStudentQuestions(data);
      },
      { 401: () => {} }
    );

    return data;
  }

  async function getStudentList() {
    request(
      // token,
      // history,
      "GET",
      "/get-test-groups-info?testId=" + testId,
      (res) => {
        let temp = [];
        res.data.map((elm, index) => {
          temp.push({
            groupCode: elm.groupCode,
            note: elm.note,
            numStudent: elm.numStudent,
            numQuestion: elm.numQuestion,
            quizGroupId: elm.quizGroupId,
            selected: false,
          });
        });
        setGroupList(temp);
        console.log(res.data);
      }
    );
    count = 0;
  }

  const handleGenerateQuizGroup = (e) => {
    handleClose();
    let data = { quizTestId: testId, numberOfQuizTestGroups: numberGroups };

    request(
      // token,
      // history,
      "post",
      "generate-quiz-test-group",
      (res) => {
        console.log(res);
        alert("Thêm đề thành công");
      },
      { rest: () => setError(true) },

      data
    );
  };

  const handleDeleteQuizGroup = (e) => {
    if (!window.confirm("Bạn có chắc muốn xóa những đề thi này không ???")) {
      return;
    }

    let acceptList = [];
    groupList.map((v, i) => {
      if (v.selected == true) {
        acceptList.push(v.quizGroupId);
      }
    });

    if (acceptList.length != 0) {
      let result = -1;
      let formData = new FormData();
      formData.append("testId", testId);
      formData.append("quizTestGroupList", acceptList.join(";"));

      request(
        // token,
        // history,
        "POST",
        "/delete-quiz-test-groups",
        (res) => {
          result = res.data;

          if (result >= 0) {
            let temp = groupList.filter(
              (el) => !acceptList.includes(el.userLoginId)
            );
            setGroupList(temp);
            count = 0;
          }
        },
        {},
        formData
      );
    }
  };

  const exportExamQuestionsOfAllStudents = async () => {
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
  };

  useEffect(() => {
    getStudentList();
    return () => {};
  }, []);

  return (
    <>
      <MaterialTable
        title=""
        columns={columns}
        data={groupList}
        //icons={tableIcons}
        localization={{
          header: {
            actions: "",
          },
          body: {
            emptyDataSourceMessage: "Không có bản ghi nào để hiển thị",
            filterRow: {
              filterTooltip: "Lọc",
            },
          },
        }}
        options={{
          search: true,
          actionsColumnIndex: -1,
          pageSize: 10,
          tableLayout: "fixed",
          //selection: true
        }}
        actions={[
          {
            icon: () => {
              return (
                <Tooltip
                  title="Thêm đề mới"
                  aria-label="Thêm đề mới"
                  placement="top"
                >
                  <ThemeProvider theme={theme}>
                    <Button
                      variant="contained"
                      color="primary"
                      onClick={(e) => {
                        //handleGenerateQuizGroup(e);
                        onOpenDialog();
                      }}
                      style={{ color: "white" }}
                    >
                      <AddCircleOutlineIcon
                        style={{ color: "white" }}
                        fontSize="default"
                      />
                      &nbsp;&nbsp;&nbsp;Thêm đề&nbsp;&nbsp;
                    </Button>
                  </ThemeProvider>
                </Tooltip>
              );
            },
            isFreeAction: true,
          },
          {
            icon: () => {
              return (
                <Tooltip
                  title="Xóa đề được chọn"
                  aria-label="Xóa đề được chọn"
                  placement="top"
                >
                  <Button
                    variant="contained"
                    color="secondary"
                    onClick={(e) => {
                      handleDeleteQuizGroup(e);
                    }}
                  >
                    <Delete style={{ color: "white" }} fontSize="default" />
                    &nbsp;&nbsp;&nbsp;Xóa&nbsp;&nbsp;
                  </Button>
                </Tooltip>
              );
            },
            isFreeAction: true,
          },
          {
            icon: () => {
              return (
                <Tooltip
                  title="Chọn tất cả"
                  aria-label="Chọn tất cả"
                  placement="top"
                >
                  <Checkbox
                    checked={selectedAll}
                    onChange={(e) => {
                      let tempS = e.target.checked;
                      setSelectedAll(e.target.checked);

                      if (tempS) count = groupList.length;
                      else count = 0;

                      groupList.map((value, index) => {
                        value.selected = tempS;
                      });
                    }}
                  />
                  {/* <div>&nbsp;&nbsp;&nbsp;Chọn tất cả&nbsp;&nbsp;</div> */}
                </Tooltip>
              );
            },
            isFreeAction: true,
          },

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
      <QuizTestGroupQuestionList testId={testId} />

      <CustomizedDialogs
        open={open}
        handleClose={handleClose}
        title="Sinh thêm đề"
        content={
          <>
            <Typography color="textSecondary" gutterBottom>
              Nhập số lượng đề cần sinh thêm
            </Typography>
            <SimpleBar
              style={{
                height: "100%",
                maxHeight: 400,
                width: 330,
                overflowX: "hidden",
                overscrollBehaviorY: "none", // To prevent tag <main> be scrolled when menu's scrollbar reach end
              }}
            ></SimpleBar>
            <TextField
              required
              id="standard-required"
              label="Required"
              defaultValue="1"
              onChange={handleChangeNumberGroups}
            />
          </>
        }
        actions={
          <>
            <TertiaryButton onClick={handleClose}>Huỷ</TertiaryButton>
            <PrimaryButton onClick={handleGenerateQuizGroup}>
              Sinh thêm đề
            </PrimaryButton>
          </>
        }
        style={{ content: classes.dialogContent }}
      />
      <ErrorDialog open={error} />
    </>
  );
}
