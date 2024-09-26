import React, { useEffect, useState } from "react";
import { request } from "../../../../api";
import MaterialTable from "material-table";
import { Button, Tooltip } from "@material-ui/core/";

import AddIcon from "@material-ui/icons/Add";
import { Link } from "react-router-dom";
import LearningSessionFormAddQuizTest from "./LearningSessionFormAddQuizTest";
import LearningSessionFormAddQuizInClassTests from "./LearningSessionFormAddQuizInClassTests";

export default function LearningSessionTeacherViewQuizTestList(props) {
  const sessionId = props.sessionId;
  const isCourse = props.isCourse;
  const [quizTests, setQuizTests] = useState([]);
  const [open, setOpen] = useState(false);

  const headerProperties = {
    headerStyle: {
      fontSize: 14,
      backgroundColor: "rgb(63, 81, 181)",
      color: "white",
    },
  };

  const columns = [
    {
      title: "Interactive Quiz Id",
      field: isCourse ? "id" : "interactive_quiz_id",
      render: (rowData) => (
        <Link
          to={{
            pathname: isCourse
              ? `/edu/teacher/course/detail/interactive-quiz/${rowData.id}`
              : `/edu/teacher/class/detail/interactive-quiz/${rowData.interactive_quiz_id}`,
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
          {isCourse ? rowData.id : rowData.interactive_quiz_id}
        </Link>
      ),
    },
    {
      title: "Interactive Quiz Name",
      field: isCourse ? "interactiveQuizName" : "interactive_quiz_name",
    },
    { title: "Status", field: "statusId" },
  ];
  function getQuizTestOfSession() {
    request(
      "get",
      isCourse
        ? "/edu/course/get-interactive-quiz-of-course-session/" + sessionId
        : "/get-list-interactive-quiz-by-session/" + sessionId,
      (res) => {
        console.log(res);
        setQuizTests(res.data);
      },
      { 401: () => {} }
    );
  }

  function handleAddQuizTest() {
    setOpen(true);
  }

  function updateQuizListWhenCreateSuccess(res) {
    setQuizTests([...quizTests, res.data]);
  }

  useEffect(() => {
    getQuizTestOfSession();
  }, []);
  return (
    <div>
      <MaterialTable
        title="Danh sách quiz test"
        columns={columns}
        data={quizTests}
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
                    onClick={handleAddQuizTest}
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
      <LearningSessionFormAddQuizInClassTests
        isCourse={isCourse}
        open={open}
        setOpen={setOpen}
        sessionId={sessionId}
        onCreateSuccess={updateQuizListWhenCreateSuccess}
      />
    </div>
  );
}
