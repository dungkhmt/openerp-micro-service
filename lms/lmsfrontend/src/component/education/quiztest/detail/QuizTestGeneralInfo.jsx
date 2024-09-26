import {Box, CircularProgress, Typography} from "@material-ui/core/";
import {teal} from "@material-ui/core/colors";
import {Skeleton} from "@material-ui/lab";
import {request} from "api";
import PrimaryButton from "component/button/PrimaryButton";
import TertiaryButton from "component/button/TertiaryButton";
import {useEffect, useState} from "react";
import {FcCalendar, FcClock} from "react-icons/fc";
import {Link as RouterLink} from "react-router-dom";
import {addZeroBefore} from "utils/dateutils";
import {Card, CardContent, CardHeader} from "@mui/material";
import {errorNoti} from "../../../../utils/notification";

const styles = {
  btn: {
    ml: 1,
  },
  courseName: { fontWeight: (theme) => theme.typography.fontWeightMedium },
  testName: { fontSize: "1.25rem", pt: 1 },
  time: {
    pl: 0.75,
    color: teal[800],
    fontWeight: (theme) => theme.typography.fontWeightMedium,
    fontSize: "1rem",
  },
};

const WEEK_DAYS = [
  "Chủ nhật",
  "Thứ hai",
  "Thứ ba",
  "Thứ tư",
  "Thứ năm",
  "Thứ sáu",
  "Thứ bảy",
];

export default function QuizTestGeneralInfo(props) {
  let testId = props.testId;
  const [testInfo, setTestInfo] = useState();
  const [courseInfo, setCourseInfo] = useState();
  const [isProcessing, setIsProcessing] = useState(false);

  useEffect(getQuizTestDetail, []);

  async function getQuizTestDetail() {
    let testInfoRes = await request("GET", `/get-quiz-test?testId=${testId}`);
    let testInfo = testInfoRes.data;
    let courseInfoRes = await request("GET", `/edu/class/${testInfo.classId}`);
    let courseInfo = courseInfoRes.data;

    const date = new Date(testInfo.scheduleDatetime),
      currentTime = new Date();
    const year =
      currentTime.getFullYear() === date.getFullYear()
        ? ""
        : ` ${date.getFullYear()},`;
    const scheduleDateTime =
      `${WEEK_DAYS[date.getDay()]},${date.getDate()} Tháng ${
        date.getMonth() + 1
      }, ${year} ` +
      `lúc ${addZeroBefore(date.getHours(), 2)}:${addZeroBefore(
        date.getMinutes(),
        2
      )}`;

    console.log("courseInfo", courseInfo);

    setTestInfo({ ...testInfo, scheduleDateTime });
    setCourseInfo(courseInfo);
  }

  function autoAssignStudents2QuizGroup() {
    let data = { quizTestId: testId };
    let successHandler = (res) => {
      alert("Auto assign students to groups " + res.data);
    };
    let errorHandlers = {
      onError: () => errorNoti("Đã xảy ra lỗi, vui lòng thử lại!"),
    };
    request(
      "POST",
      "auto-assign-participants-2-quiz-test-group",
      successHandler,
      errorHandlers,
      data
    );
  }

  function autoAssignQuestions2QuizGroup() {
    let data = { quizTestId: testId, numberQuestions: 10 };
    let successHandler = (res) => {
      alert("Auto assign questions to groups " + res.data);
    };
    let errorHandlers = {
      onError: () => errorNoti("Đã xảy ra lỗi, vui lòng thử lại!"),
    };
    request(
      "POST",
      "auto-assign-question-2-quiz-group",
      successHandler,
      errorHandlers,
      data
    );
  }

  function processUpdateSubmissionParticipant() {
    setIsProcessing(true);

    request(
      "get",
      "summarize-quiz-test-execution-choice/" + testId,
      (res) => {
        alert("summarize finished " + res.data);
        setIsProcessing(false);
      },
      { 401: () => {} }
    );
  }
  return courseInfo ? (
    <>
      <Card>
        <CardHeader
          title={
            <Typography variant="h5" sx={styles.courseName}>
              {`${courseInfo.name} (${courseInfo.courseId})`}
            </Typography>
          }
          action={
            <Box display="flex" justifyContent="flex-end">
              {isProcessing ? <CircularProgress /> : ""}
              <PrimaryButton
                sx={styles.btn}
                onClick={autoAssignStudents2QuizGroup}
              >
                Phân đề cho SV
              </PrimaryButton>

              <PrimaryButton
                sx={styles.btn}
                onClick={autoAssignQuestions2QuizGroup}
              >
                Phân câu hỏi cho đề
              </PrimaryButton>
              {testInfo.judgeMode === "BATCH_LAZY_EVALUATION" ? (
                <PrimaryButton
                  sx={styles.btn}
                  onClick={processUpdateSubmissionParticipant}
                >
                  Xử lý cập nhật bài làm SV
                </PrimaryButton>
              ) : null}
            </Box>
          }
        />
        <CardContent>
          <Typography variant="subtitle1" sx={styles.testName}>
            {`Kỳ thi: ${testInfo.testName}`}
          </Typography>
          <Typography variant="subtitle1" sx={styles.testName}>
            {`Mã kỳ thi: ${testInfo.testId}`}
          </Typography>
          <Typography variant="subtitle1" sx={styles.testName}>
            {`Trạng thái: ${testInfo.statusId}`}
          </Typography>
          <Typography variant="subtitle1" sx={styles.testName}>
            {`Chế độ chấm: ${testInfo.judgeMode}`}
          </Typography>

          <Box display="flex" alignItems="center" pt={2}>
            <FcClock size={24} />
            <Typography component="span" sx={styles.time}>
              {`${testInfo.duration} phút`}
            </Typography>

            <FcCalendar size={24} style={{ marginLeft: 40 }} />
            <Typography component="span" sx={styles.time}>
              {`${testInfo.scheduleDateTime}`}
            </Typography>

            <TertiaryButton
              sx={{
                ml: 2,
                fontWeight: (theme) => theme.typography.fontWeightRegular,
              }}
              component={RouterLink}
              to={`/edu/class/quiztest/edit/${testId}`}
            >
              Chỉnh sửa
            </TertiaryButton>
          </Box>
        </CardContent>
      </Card>

      <br />
    </>
  ) : (
    // Loading screen
    <>
      <Typography variant="h5" sx={styles.courseName}>
        <Skeleton width={400} variant="rect" animation="wave" />
      </Typography>
      <Typography variant="subtitle1" sx={styles.testName}>
        <Skeleton width={200} variant="rect" animation="wave" />
      </Typography>

      <Box display="flex" alignItems="center" pt={2}>
        <Skeleton width={24} height={24} variant="circle" animation="wave" />
        <Typography component="span" sx={styles.time}>
          <Skeleton width={80} variant="rect" animation="wave" />
        </Typography>
      </Box>
    </>
  );
}
