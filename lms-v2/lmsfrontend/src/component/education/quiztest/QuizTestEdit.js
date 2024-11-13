import { useEffect, useState } from "react";
import { useParams } from "react-router";
import { useHistory } from "react-router-dom";
import { request } from "../../../api";
import EditQuizTest from "./detail/EditQuizTest";

const styles = {
  label: {
    fontSize: "20px",
    fontWeight: "lighter",
  },

  descStyle: {
    fontSize: "20px",
    fontWeight: "lighter",
  },

  ansStyle: {
    fontSize: "18px",
    fontWeight: "lighter",
    paddingTop: "15px",
    paddingBottom: "30px",
    paddingLeft: "30px",
  },

  subAnsStyle: {
    fontSize: "18px",
    fontWeight: "lighter",
    paddingBottom: "10px",
  },
};

function QuizTestEdit() {
  let param = useParams();
  let testId = param.id;
  const history = useHistory();
  //const classes = useStyles();

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

  function getListParticipantQuizGroupAssignmentMode() {
    request(
      // token,
      // history,
      "get",
      "get-list-participant-quizgroup-assignment-mode",
      (res) => {
        console.log(
          "get-list-participant-quizgroup-assignment-mode res = ",
          res
        );
        setListParticipantQuizGroupAssignmentMode(res.data);

        //alert('assign questions to groups OK');
      },
      { 401: () => {} }
    );
  }
  function getListQuizTestStatusIds() {
    request(
      // token,
      // history,
      "get",
      "get-list-quiz-test-status-ids",
      (res) => {
        console.log("get-list-quiz-test-status-ids res = ", res);
        setListStatusIds(res.data);
        //alert('assign questions to groups OK');
      },
      { 401: () => {} }
    );
  }
  function getListQuestionStatementViewTypeId() {
    request(
      // token,
      // history,
      "get",
      "get-list-question-statement-view-type-id",
      (res) => {
        console.log("get-list-question-statement-view-type-id res = ", res);
        setListQuestionStatementViewTypeId(res.data);

        //alert('assign questions to groups OK');
      },
      { 401: () => {} }
    );
  }
  function getListQuizTestViewTypeId() {
    request(
      // token,
      // history,
      "get",
      "get-list-quiz-test-view-type-id",
      (res) => {
        console.log("get-list-question-statement-view-type-id res = ", res);
        setListViewTypeIds(res.data);

        //alert('assign questions to groups OK');
      },
      { 401: () => {} }
    );
  }

  async function getQuizTestDetail() {
    request(
      // token,
      // history,
      "get",
      "get-quiz-test?testId=" + testId,
      (res) => {
        console.log(res);
        setQuizTest(res.data);
        setDuration(res.data.duration);
        setStatusId(res.data.statusId);
        setSelectedDate(res.data.scheduleDatetime);
        setQuestionStatementViewTypeId(res.data.questionStatementViewTypeId);
        setParticipantQuizGroupAssignmentMode(
          res.data.participantQuizGroupAssignmentMode
        );
        setJudgeMode(res.data.judgeMode);
        //alert('assign questions to groups OK');
      },
      { 401: () => {} }
    );
  }

  useEffect(() => {
    getListQuizTestStatusIds();
    getListQuestionStatementViewTypeId();
    getListParticipantQuizGroupAssignmentMode();
    getListQuizTestViewTypeId();
    getQuizTestDetail();
    getListJudgeModes();
  }, []);

  return (
    <div>
      <EditQuizTest />
      <br />

      {/* <Grid container spacing={1} justify="center">
        <Card
          style={{
            padding: "3% 10% 7% 10%",
            minWidth: "1024px",
          }}
        >
          Status: {quizTest ? quizTest.statusId : ""}
          <form noValidate autoComplete="off">
            <MuiPickersUtilsProvider utils={DateFnsUtils}>
              <Grid container spacing={10}>
                <Grid item xs={4}>
                  <div style={styles.label}>Ngày thi </div>
                </Grid>
                <Grid item xs={4}>
                  <div style={styles.label}>Giờ thi </div>
                </Grid>
                <Grid item xs={4}>
                  <div style={styles.label}>Thời gian làm bài </div>
                </Grid>
              </Grid>
              <div style={{ marginTop: "-80px" }}>
                <Grid container spacing={10}>
                  <Grid item xs={4}>
                    <KeyboardDatePicker
                      format="dd/MM/yyyy"
                      margin="normal"
                      label="Chọn ngày thi"
                      value={selectedDate}
                      onChange={handleDateChange}
                      KeyboardButtonProps={{
                        "aria-label": "change date",
                      }}
                    />
                  </Grid>
                  <Grid item xs={4}>
                    <KeyboardTimePicker
                      margin="normal"
                      label="Chọn giờ thi"
                      value={selectedDate}
                      onChange={handleDateChange}
                      KeyboardButtonProps={{
                        "aria-label": "change date",
                      }}
                    />
                  </Grid>
                  <Grid item xs={4}>
                    <TextField
                      label="Tính bằng phút"
                      placeholder="90"
                      style={{ marginTop: "15px" }}
                      fullWidth
                      onChange={handleChangeDuration}
                      value={duration}
                      type="number"
                    />
                  </Grid>
                  <Grid item xs={4}>
                    <Typography>Question Statement View</Typography>
                    <TextField
                      autoFocus
                      // required
                      select
                      id="questionStatementViewTypeId"
                      //label="QuestionStatementViewType"
                      //placeholder="QuestionStatementViewType"
                      onChange={(event) => {
                        setQuestionStatementViewTypeId(event.target.value);
                      }}
                      value={questionStatementViewTypeId}
                    >
                      {listQuestionStatementViewTypeId.map((item) => (
                        <MenuItem key={item} value={item}>
                          {item}
                        </MenuItem>
                      ))}
                    </TextField>
                  </Grid>
                  <Grid item xs={4}>
                    <Typography>Status</Typography>
                    <TextField
                      autoFocus
                      // required
                      select
                      id="status"
                      //label="status"
                      //placeholder="status"
                      onChange={(event) => {
                        setStatusId(event.target.value);
                      }}
                      value={statusId}
                    >
                      {listStatusIds.map((item) => (
                        <MenuItem key={item} value={item}>
                          {item}
                        </MenuItem>
                      ))}
                    </TextField>
                  </Grid>
                  <Grid item xs={4}>
                    <Typography>View Type</Typography>
                    <TextField
                      autoFocus
                      // required
                      select
                      id="ViewTypeId"
                      //label="ViewType"
                      //placeholder="ViewType"
                      onChange={(event) => {
                        setViewTypeId(event.target.value);
                      }}
                      value={viewTypeId}
                    >
                      {listViewTypeIds.map((item) => (
                        <MenuItem key={item} value={item}>
                          {item}
                        </MenuItem>
                      ))}
                    </TextField>
                  </Grid>
                  <Grid item xs={4}>
                    <Typography>
                      Participant QuizGroup Assignment Mode
                    </Typography>
                    <TextField
                      autoFocus
                      // required
                      select
                      id="participantQuizGroupAssignmentMode"
                      //label="participantQuizGroupAssignmentMode"
                      //placeholder="participantQuizGroupAssignmentMode"
                      onChange={(event) => {
                        setParticipantQuizGroupAssignmentMode(
                          event.target.value
                        );
                      }}
                      value={participantQuizGroupAssignmentMode}
                    >
                      {listParticipantQuizGroupAssignmentMode.map((item) => (
                        <MenuItem key={item} value={item}>
                          {item}
                        </MenuItem>
                      ))}
                    </TextField>
                  </Grid>
                  <Grid item xs={4}>
                    <Typography>JudgeMode</Typography>
                    <TextField
                      autoFocus
                      // required
                      select
                      id="judgeMode"
                      //label="participantQuizGroupAssignmentMode"
                      //placeholder="participantQuizGroupAssignmentMode"
                      onChange={(event) => {
                        setJudgeMode(event.target.value);
                      }}
                      value={judgeMode}
                    >
                      {listJudgeModes.map((item) => (
                        <MenuItem key={item} value={item}>
                          {item}
                        </MenuItem>
                      ))}
                    </TextField>
                  </Grid>

                  <Grid item xs={4}>
                    <Button
                      variant="contained"
                      color="primary"
                      //fullWidth
                      onClick={(e) => {
                        handleSubmit();
                      }}
                    >
                      Lưu
                    </Button>
                  </Grid>
                </Grid>
              </div>

              {/* <div style={styles.label}>Ngày thi </div> */}
      {/* </MuiPickersUtilsProvider>
          </form> */}
      {/*
            <Button
              variant="contained"
              color="primary"
              fullWidth
              onClick={(e) => {
                handleOpenQuizTest();
              }}
            >
              Mở
            </Button>
            <Button
              variant="contained"
              color="primary"
              fullWidth
              onClick={(e) => {
                handleHideQuizTest();
              }}
            >
              Ẩn
            </Button>
            */}
      {/* </Card>
      </Grid> */}
    </div>
  );
}

export default QuizTestEdit;
