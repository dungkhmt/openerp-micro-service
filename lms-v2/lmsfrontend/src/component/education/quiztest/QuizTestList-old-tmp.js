import {Button, Tooltip} from "@material-ui/core/";
import AddIcon from "@material-ui/icons/Add";
import MaterialTable from "material-table";
import {useEffect, useState} from "react";
import {Link, useHistory} from "react-router-dom";
import QuizTestListOfCurrentTeacher from "../../../views/Education/quiztest/teacher/QuizTestListOfCurrentTeacher";
import withScreenSecurity from "../../withScreenSecurity";
import QuizTestsOfParticipantRole from "./QuizTestsOfParticipantRole";
import {request} from "../../../api";

const nextLine = <pre></pre>;

function createData(
  testId,
  testName,
  scheduleDatetime,
  duration,
  classId,
  classUuid
) {
  //date = Date.parse(scheduleDatetime);
  let index = scheduleDatetime.indexOf("T");
  let date = scheduleDatetime.substring(0, index);
  let excuteTime = scheduleDatetime.substring(
    index + 1,
    scheduleDatetime.indexOf(".")
  );
  //console.log(scheduleDatetime)
  return {
    testId,
    testName,
    datetime: date,
    excuteTime: excuteTime,
    duration: duration + " phút",
    classId,
    classUuid,
  };
}

const rows = [];

const headerProperties = {
  headerStyle: {
    fontSize: 14,
    backgroundColor: "rgb(63, 81, 181)",
    color: "white",
  },
};

const columns = [
  {
    field: "testId",
    title: "Mã kỳ thi",
    render: (rowData) => (
      <Link
        to={{
          pathname: `/edu/class/quiztest/detail/${rowData.testId}`,
        }}
        style={{
          textDecoration: "none",
          whiteSpace: "pre-wrap" /* css-3 */,
          whiteSpace: "-moz-pre-wrap" /* Mozilla, since 1999 */,
          whiteSpace: "-pre-wrap" /* Opera 4-6 */,
          whiteSpace: "-o-pre-wrap" /* Opera 7 */,
          wordWrap: "break-word" /* Internet Explorer 5.5+ */,
        }}
      >
        {rowData.testId}
      </Link>
    ),
    ...headerProperties,
  },
  {
    field: "testName",
    title: "Tên kỳ thi",
    ...headerProperties,
  },
  {
    field: "classId",
    title: "Mã lớp",
    render: (rowData) => (
      <Link
        to={{
          pathname: `/edu/teacher/class/${rowData.classUuid}`,
        }}
        style={{
          textDecoration: "none",
        }}
      >
        {rowData.classId}
      </Link>
    ),
    ...headerProperties,
  },
  {
    field: "datetime",
    title: "Ngày thi",
    ...headerProperties,
  },
  {
    field: "excuteTime",
    title: "Giờ thi",
    ...headerProperties,
    width: "10%",
  },
  {
    field: "duration",
    title: "Thời lượng",
    cellStyle: {
      textAlign: "right",
    },
    headerStyle: { ...headerProperties.headerStyle, textAlign: "right" },
  },
];

function QuizTestList() {
  const history = useHistory();

  const [quizTestList, setQuizTestList] = useState([]);

  function getAllQuizTestByUser() {
    request("get", "/get-all-quiz-test-by-user", (res) => {
      const list = res.data;

      request("get", "/edu/class/list/teacher", (res2) => {
        const listClass = res2.data;

        rows.splice(0, rows.length);
        list.map((elm, index) => {
          let foundIndex = -1;
          for (let index = 0; index < listClass.length; index++) {
            if (listClass[index].id == elm.classId) {
              foundIndex = index;
              break;
            }
          }

          if (foundIndex == -1) {
            //alert("Something went wrong !!!");
          } else
            rows.push(
              createData(
                elm.testId,
                elm.testName,
                elm.scheduleDatetime,
                elm.duration,
                listClass[foundIndex].classCode,
                elm.classId
              )
            );
        });

        setQuizTestList(rows);
      });
    });
  }

  useEffect(() => {
    getAllQuizTestByUser();
  }, []);

  /* let body = {
        'testId': testId,
        'testName': quizName,
        'scheduleDatetime': selectedDate,
        'duration': duration,
        'courseId': selectedCourse,
        'classId': selectedClass
    }; */

  return (
    <>
      <QuizTestListOfCurrentTeacher />

      {/* <Grid container spacing={5} justify='flex-end' direction="row">
                    <Tooltip title="Thêm mới một đề thi" aria-label="Thêm mới một đề thi" placement="top">
                        <Button
                            variant="contained"
                            color="primary"
                            onClick={() => { 
                                history.push('create-quiz-test');
                            }}
                        >
                            <AddIcon  style={{ color: 'white' }} fontSize='default' />
                            &nbsp;&nbsp;&nbsp;Thêm mới&nbsp;&nbsp;
                        </Button>
                    </Tooltip>
            </Grid> */}
      {nextLine}
      <MaterialTable
        title="Danh sách kỳ thi"
        columns={columns}
        data={quizTestList}
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
          sorting: false,
          actionsColumnIndex: -1,
          pageSize: 8,
          tableLayout: "fixed",
        }}
        style={{
          fontSize: 14,
        }}
        actions={[
          {
            icon: () => {
              return (
                <Tooltip
                  title="Thêm mới một kỳ thi"
                  aria-label="Thêm mới một kỳ thi"
                  placement="top"
                >
                  <Button
                    variant="contained"
                    color="primary"
                    onClick={() => {
                      history.push("create-quiz-test");
                    }}
                  >
                    <AddIcon style={{ color: "white" }} fontSize="default" />
                    &nbsp;&nbsp;&nbsp;Thêm mới&nbsp;&nbsp;
                  </Button>
                </Tooltip>
              );
            },
            isFreeAction: true,
          },
        ]}
      />
      <QuizTestsOfParticipantRole />
    </>
  );
}

const screenName = "SCREEN_VIEW_QUIZ_TEST_TEACHER";
export default withScreenSecurity(QuizTestList, screenName, true);
