import {Button, Tooltip} from "@material-ui/core/";
import AddIcon from "@material-ui/icons/Add";
import MaterialTable from "material-table";
import React, {useEffect, useState} from "react";
//import { useDispatch, useSelector } from "react-redux";
import {Link, useHistory} from "react-router-dom";
import {request} from "../../../api";
import withScreenSecurity from "../../withScreenSecurity";
import QuizTestsOfParticipantRole from "./QuizTestsOfParticipantRole";

function QuizTestList() {
  const history = useHistory();

  const [quizTestList, setQuizTestList] = useState([]);

  const columns = [
    {
      title: "TestId",
      field: "testId",
      render: (rowData) => (
        <Link
          to={{
            pathname: `/edu/class/quiztest/detail/${rowData.testId}`,
          }}
        >
          {rowData.testId}
        </Link>
      ),
    },
    { title: "TestName", field: "testName" },
  ];
  function getQuizTestList() {
    request("get", "get-all-quiz-test-by-user", (res) => {
      setQuizTestList(res.data);
    });
  }

  useEffect(() => {
    getQuizTestList();
  }, []);
  return (
    <div>
      <MaterialTable
        columns={columns}
        data={quizTestList}
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
    </div>
  );
}

const screenName = "SCREEN_VIEW_QUIZ_TEST_TEACHER";
export default withScreenSecurity(QuizTestList, screenName, true);
