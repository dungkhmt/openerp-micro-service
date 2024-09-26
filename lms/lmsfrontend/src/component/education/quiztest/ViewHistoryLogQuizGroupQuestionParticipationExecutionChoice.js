import {Box} from "@material-ui/core/";
//import IconButton from '@material-ui/core/IconButton';
import {makeStyles} from "@material-ui/core/styles";
import MaterialTable from "material-table";
import React, {useEffect, useState} from "react";
import {request} from "../../../api";

const useStyles = makeStyles({
  table: {
    minWidth: 700,
  },
});

function ViewHistoryLogQuizGroupQuestionParticipationExecutionChoice(props) {
  const [data, setData] = useState([]);
  let testId = props.testId;
  const columns = [
    { title: "User", field: "userLoginId" },
    { title: "Date", field: "date" },
    { title: "quizGroupId", field: "quizGroupId" },
    { title: "questionId", field: "questionId" },
    { title: "choiceAnswerId", field: "choiceAnswerId" },
  ];

  useEffect(() => {
    function getHistoryLogQuizGroupQuestionParticipationExecutionChoice() {
      request(
        // token,
        // history,
        "get",
        "get-history-log-quiz_group_question_participation_execution_choice/" +
          testId,
        (res) => {
          // console.log(res);
          //alert('assign students to groups OK');
          setData(res.data);
        },
        { 401: () => {} }
      );
    }

    getHistoryLogQuizGroupQuestionParticipationExecutionChoice();
  }, []);

  return (
    <Box pt={3}>
      <MaterialTable title={"Lịch sử làm quiz"} columns={columns} data={data} />
    </Box>
  );
}

export default ViewHistoryLogQuizGroupQuestionParticipationExecutionChoice;
