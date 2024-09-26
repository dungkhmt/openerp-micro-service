import {Grid, MenuItem} from "@material-ui/core/";
import {Button, Card, CardActions, CardContent, FormControl, FormLabel, Stack, TextField,} from "@mui/material";
import React, {useEffect, useMemo, useState} from "react";
import {useParams} from "react-router";
import {useHistory} from "react-router-dom";
import {request} from "../../../../api";
import {DateTimePicker} from "@mui/x-date-pickers/DateTimePicker";
import {LocalizationProvider} from "@mui/x-date-pickers/LocalizationProvider";
import {AdapterDateFns} from "@mui/x-date-pickers/AdapterDateFns";
import SelectItem from "../../../common/form/SelectItem";
import {CardHeader} from "@material-ui/core";
import {errorNoti, successNoti} from "../../../../utils/notification";

function QuizTestEdit() {
  let param = useParams();
  let testId = param.id;
  const history = useHistory();
  const [quizTest, setQuizTest] = useState(null);

  const [duration, setDuration] = useState(null);
  const [selectedDate, setSelectedDate] = useState(null);

  const [questionStatementViewTypeId, setQuestionStatementViewTypeId] =
    useState(null);
  const [listQuestionStatementViewTypeId, setListQuestionStatementViewTypeId] =
    useState([]);

  const [
    participantQuizGroupAssignmentMode,
    setParticipantQuizGroupAssignmentMode,
  ] = useState(null);
  const [
    listParticipantQuizGroupAssignmentMode,
    setListParticipantQuizGroupAssignmentMode,
  ] = useState([]);

  const [viewTypeId, setViewTypeId] = useState(null);
  const [listViewTypeIds, setListViewTypeIds] = useState([]);

  const [statusId, setStatusId] = useState(null);
  const [listStatusIds, setListStatusIds] = useState([]);

  const [judgeMode, setJudgeMode] = useState(null);
  const [listJudgeModes, setListJudgeModes] = useState([]);

  const quizTestStatusOptions = useMemo(() => {
    return listStatusIds.map((statusId) => ({
      value: statusId,
      label: statusId,
    }));
  }, [listStatusIds]);

  const questionListViewOptions = useMemo(() => {
    return listViewTypeIds.map((viewTypeId) => ({
      value: viewTypeId,
      label: viewTypeId,
    }));
  }, [listViewTypeIds]);

  const participantQuizGroupAssignmentModeOptions = useMemo(() => {
    return listParticipantQuizGroupAssignmentMode.map((mode) => ({
      value: mode,
      label: mode,
    }));
  }, [listParticipantQuizGroupAssignmentMode]);

  const questionStatementViewOptions = useMemo(() => {
    return listQuestionStatementViewTypeId.map((typeId) => ({
      value: typeId,
      label: typeId,
    }));
  }, [listQuestionStatementViewTypeId]);

  const judgeModes = useMemo(() => {
    return listJudgeModes.map((judgeModeId) => ({
      value: judgeModeId,
      label: judgeModeId,
    }));
  }, [listJudgeModes]);

  useEffect(() => {
    getListQuizTestStatusIds();
    getListQuestionStatementViewTypeId();
    getListParticipantQuizGroupAssignmentMode();
    getListQuizTestViewTypeId();
    getListJudgeModes();
    getQuizTestDetail();
  }, []);

  function getListParticipantQuizGroupAssignmentMode() {
    let successHandler = (res) =>
      setListParticipantQuizGroupAssignmentMode(res.data);
    request(
      "GET",
      "get-list-participant-quizgroup-assignment-mode",
      successHandler
    );
  }

  function getListQuizTestStatusIds() {
    request("GET", "get-list-quiz-test-status-ids", (res) =>
      setListStatusIds(res.data)
    );
  }
  function getListJudgeModes() {
    request(
      // token,
      // history,
      "get",
      "get-list-judge-modes",
      (res) => {
        console.log("get-list-judge-modes res = ", res);
        setListJudgeModes(res.data);

        //alert('assign questions to groups OK');
      },
      { 401: () => {} }
    );
  }

  function getListQuestionStatementViewTypeId() {
    request("GET", "get-list-question-statement-view-type-id", (res) =>
      setListQuestionStatementViewTypeId(res.data)
    );
  }
  function getListQuizTestViewTypeId() {
    request("GET", "get-list-quiz-test-view-type-id", (res) =>
      setListViewTypeIds(res.data)
    );
  }

  async function getQuizTestDetail() {
    request("GET", "get-quiz-test?testId=" + testId, (res) => {
      setQuizTest(res.data);
      setDuration(res.data.duration);
      setStatusId(res.data.statusId);
      setSelectedDate(res.data.scheduleDatetime);
      setViewTypeId(res.data.viewTypeId);
      setQuestionStatementViewTypeId(res.data.questionStatementViewTypeId);
      setParticipantQuizGroupAssignmentMode(
        res.data.participantQuizGroupAssignmentMode
      );
      setJudgeMode(res.data.judgeMode);
    });
  }

  function updateQuizTest() {
    let updatedInfo = {
      testId,
      scheduleDate: selectedDate,
      duration,
      statusId,
      questionStatementViewTypeId,
      viewTypeId,
      participantQuizGroupAssignmentMode,
      judgeMode: judgeMode,
    };

    let successHandler = (res) =>
      successNoti("Cập nhật quiz test thành công", true);
    let errorHandlers = {
      onError: (error) =>
        errorNoti("Đã xảy ra lỗi khi cập nhật quiz test", true),
    };
    request(
      "POST",
      "update-quiz-test",
      successHandler,
      errorHandlers,
      updatedInfo
    );

    history.push("/edu/class/quiztest/detail/" + testId);
  }

  return (
    <Grid container spacing={1} justify="center">
      <Card style={{ padding: "3% 10% 3% 10%", minWidth: "1024px" }}>
        <CardHeader
          title={
            <span>
              Chỉnh sửa quiz test <em>{quizTest?.testName}</em>
            </span>
          }
        />

        <CardContent>
          <Stack spacing={2}>
            <Stack direction="row" spacing={2}>
              <FormControl style={{ width: "100%" }}>
                <FormLabel>Tên bài test</FormLabel>
                <TextField value={quizTest?.testName} disabled />
              </FormControl>

              <FormControl style={{ width: "100%" }}>
                <FormLabel>Mã bài test</FormLabel>
                <TextField defaultValue={testId} disabled />
              </FormControl>
            </Stack>

            <Stack direction="row" spacing={2}>
              <FormControl style={{ width: "100%" }}>
                <FormLabel>Thời gian bắt đầu</FormLabel>
                <LocalizationProvider dateAdapter={AdapterDateFns}>
                  <DateTimePicker
                    value={selectedDate}
                    onChange={(value) => setSelectedDate(value)}
                    renderInput={(params) => <TextField {...params} />}
                  />
                </LocalizationProvider>
              </FormControl>

              <FormControl style={{ width: "100%" }}>
                <FormLabel>Thời gian làm bài</FormLabel>
                <TextField
                  type="number"
                  value={duration}
                  onChange={(event) => setDuration(event.target.value)}
                  placeholder="Nhập thời gian làm bải"
                />
              </FormControl>
            </Stack>

            <Stack direction="row" spacing={2}>
              <SelectItem
                label="Trạng thái"
                style={{ width: "100%" }}
                value={statusId}
                options={quizTestStatusOptions}
                onChange={(value) => setStatusId(value)}
              />
              <SelectItem
                label="Dạng hiển thị danh sách câu hỏi"
                style={{ width: "100%" }}
                value={viewTypeId}
                options={questionListViewOptions}
                onChange={(value) => setViewTypeId(value)}
              />
            </Stack>

            <Stack direction="row" spacing={2}>
              <SelectItem
                label="Participant-quiz group assignment mode"
                style={{ width: "100%" }}
                value={participantQuizGroupAssignmentMode}
                options={participantQuizGroupAssignmentModeOptions}
                onChange={(value) =>
                  setParticipantQuizGroupAssignmentMode(value)
                }
              />
              <SelectItem
                label="Question statement view"
                style={{ width: "100%" }}
                value={questionStatementViewTypeId}
                options={questionStatementViewOptions}
                onChange={(value) => setQuestionStatementViewTypeId(value)}
              />
            </Stack>
            <Stack direction="row" spacing={2}>
              <SelectItem
                label="Participant-quiz group assignment mode"
                style={{ width: "100%" }}
                value={judgeMode}
                options={judgeModes}
                onChange={(value) => setJudgeMode(value)}
              />
            </Stack>
          </Stack>
        </CardContent>

        <CardActions
          style={{ justifyContent: "flex-end", padding: "8px 16px" }}
        >
          <Button color="primary" variant="contained" onClick={updateQuizTest}>
            Cập nhật
          </Button>
        </CardActions>
      </Card>
    </Grid>
  );
}

export default QuizTestEdit;
