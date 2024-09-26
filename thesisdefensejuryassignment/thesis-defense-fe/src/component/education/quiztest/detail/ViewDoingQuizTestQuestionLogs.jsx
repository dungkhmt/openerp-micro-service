import React, {useEffect, useState} from "react";
import {request} from "../../../../api";
import {errorNoti} from "../../../../utils/notification";
import {Card, CardContent} from "@mui/material";
import StandardTable from "../../../table/StandardTable";

export default function ViewDoingQuizTestQuestionLogs(props) {
  const testId = props.testId;
  const [doingQuizQuestionLogs, setDoingQuizQuestionLogs] = useState([]);

  const columns = [
    { title: "User", field: "userLoginId" },
    { title: "Date", field: "date" },
  { title: "Quiz group ID", field: "quizGroupId" },
    { title: "Question ID", field: "questionId" },
    { title: "Choice answer ID", field: "choiceAnswerId" },
  ];

  useEffect(getDoingQuizQuestionLogs, [])

  function getDoingQuizQuestionLogs() {
    let successHandler = res => setDoingQuizQuestionLogs(res.data);
    let errorHandlers = {
      onError: (error) => errorNoti("Đã xảy ra lỗi trong khi tải dữ liệu!", true)
    }
    request("GET", `get-history-log-quiz_group_question_participation_execution_choice/${testId}`,
      successHandler, errorHandlers);
  }

  return (
    <Card>
      <CardContent>
        <StandardTable
          title="Lịch sử làm quiz"
          columns={columns}
          data={doingQuizQuestionLogs}
          hideCommandBar
          options={{
            selection:false,
            search: true,
            sorting: true
          }}
        />
      </CardContent>
    </Card>
  );
}
