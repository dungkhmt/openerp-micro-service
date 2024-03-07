import {Card, CardContent} from "@material-ui/core";
import {Box, CircularProgress, Typography} from "@material-ui/core/";
import {teal} from "@material-ui/core/colors";
import {useTheme} from "@material-ui/core/styles";
import {Skeleton} from "@material-ui/lab";
import {request} from "api";
import PrimaryButton from "component/button/PrimaryButton";
import TertiaryButton from "component/button/TertiaryButton";
import {a11yProps, AntTab, AntTabs, TabPanel} from "component/tab";
import {useEffect, useState} from "react";
import {FcCalendar, FcClock} from "react-icons/fc";
import {useParams} from "react-router";
import {Link as RouterLink} from "react-router-dom";
import {addZeroBefore} from "utils/dateutils";
import withScreenSecurity from "../../withScreenSecurity";
import TeacherViewQuizTestDetail from "./detail/TeacherViewQuizTestDetail";
import ParticipantRolesOfQuizTest from "./ParticipantRolesOfQuizTest";
import QuizListForAssignment from "./QuizListForAssignment";
import QuizQuestionsInQuizTest from "./QuizQuestionsInQuizTest";
import QuizTestGroupList from "./QuizTestGroupList";
import QuizTestGroupParticipants from "./QuizTestGroupParticipants";
import QuizTestJoinRequestList from "./QuizTestJoinRequestList";
import QuizTestResultChart from "./QuizTestResultChart";
import QuizTestStudentListResult from "./QuizTestResultList";
import QuizTestStudentList from "./QuizTestStudentList";

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

const tabsLabel = [
  "Thí sinh",
  "Thí sinh đăng ký",
  "Đề",
  "Phân đề cho thí sinh",
  "DS quiz",
  "DS quiz trong Kỳ thi",
  "Kết quả",
  "Kết quả tổng quát",
  "Biểu đồ",
  "User Vai trò",
];

const weekDay = [
  "Chủ nhật",
  "Thứ hai",
  "Thứ ba",
  "Thứ tư",
  "Thứ năm",
  "Thứ sáu",
  "Thứ bảy",
];

function QuizTestDetail() {
  let param = useParams();
  let testId = param.id;

  const [testInfo, setTestInfo] = useState([]);
  const [courseInfo, setCourseInfo] = useState();
  const [isProcessing, setIsProcessing] = useState(false);

  const [selectedTab, setSelectedTab] = useState(0);
  const theme = useTheme();

  //
  const handleChangeTab = (event, newValue) => {
    setSelectedTab(newValue);
  };

  // function handleEditQuizTes() {
  //   history.push("/edu/class/quiztest/edit/" + testId);
  // }

  function handleAssignStudents2QuizGroup() {
    setIsProcessing(true);
    let data = { quizTestId: testId };

    request(
      "post",
      "auto-assign-participants-2-quiz-test-group",
      (res) => {
        console.log("assign students to groups ", res);
        alert("assign students to groups " + res.data);
        setIsProcessing(false);
      },
      { 401: () => {} },
      data
    );

    // console.log(datasend);
  }

  function handleSummarizeResult() {
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
  function handleAssignQuestions2QuizGroup() {
    setIsProcessing(true);
    let data = { quizTestId: testId, numberQuestions: 10 };

    request(
      "post",
      "auto-assign-question-2-quiz-group",
      (res) => {
        console.log("assign questions to groups ", res);
        alert("assign questions to groups " + res.data);
        setIsProcessing(false);
      },
      { 401: () => {} },
      data
    );

    // console.log(datasend);
  }

  async function getQuizTestDetail() {
    //do something to get test info from param.id
    request("get", "/get-quiz-test?testId=" + param.id, (res) => {
      res = res.data;

      // Format scheduleDateTime.
      const date = new Date(res.scheduleDatetime);
      const currentTime = new Date();
      const year =
        currentTime.getFullYear() === date.getFullYear()
          ? ""
          : ` ${date.getFullYear()},`;

      const scheduleDateTime = `${
        weekDay[date.getDay()]
      }, ${date.getDate()} Tháng ${
        date.getMonth() + 1
      }, ${year} lúc ${addZeroBefore(date.getHours(), 2)}:${addZeroBefore(
        date.getMinutes(),
        2
      )}`;

      setTestInfo({
        testId: res.testId,
        classId: res.classId,
        courseId: res.courseId,
        duration: res.duration,
        scheduleDateTime: scheduleDateTime,
        testName: res.testName,
        statusId: res.statusId,
      });

      //do something to get course info from testInfo.courseId
      /* request(
      // token, history, 
      "get", `/edu/class/${re.classId}`, (res) => {
            tempCourseInfo.id = res.data.courseId;
            tempCourseInfo.courseName = res.data.name;
        }); */

      request("get", `/edu/class/${res.classId}`, (res2) => {
        res2 = res2.data;
        setCourseInfo({ id: res2.courseId, courseName: res2.name });
      });
    });
  }

  useEffect(() => {
    getQuizTestDetail();
  }, []);

  return courseInfo ? (
    <>
      <TeacherViewQuizTestDetail />
      <br />

      <Card>
        <CardContent>
          <Typography
            variant="h5"
            sx={styles.courseName}
          >{`${courseInfo.courseName} (${courseInfo.id})`}</Typography>
          <Typography
            variant="subtitle1"
            sx={styles.testName}
          >{`Kỳ thi: ${testInfo.testName}`}</Typography>
          <Typography
            variant="subtitle1"
            sx={styles.testName}
          >{`Mã kỳ thi: ${testInfo.testId}`}</Typography>
          <Typography
            variant="subtitle1"
            sx={styles.testName}
          >{`Trạng thái: ${testInfo.statusId}`}</Typography>

          <Box display="flex" alignItems="center" pt={2}>
            <FcClock size={24} />
            <Typography
              component="span"
              sx={styles.time}
            >{`${testInfo.duration} phút`}</Typography>

            <FcCalendar size={24} style={{ marginLeft: 40 }} />
            <Typography
              component="span"
              sx={styles.time}
            >{`${testInfo.scheduleDateTime}`}</Typography>

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
      <br />

      <Box display="flex" justifyContent="flex-end">
        {isProcessing ? <CircularProgress /> : ""}
        <PrimaryButton
          sx={styles.btn}
          onClick={(e) => {
            handleAssignStudents2QuizGroup(e);
          }}
        >
          Phân đề cho SV
        </PrimaryButton>

        <PrimaryButton
          sx={styles.btn}
          onClick={(e) => {
            handleAssignQuestions2QuizGroup(e);
          }}
        >
          Phân câu hỏi cho đề
        </PrimaryButton>
        <PrimaryButton
          sx={styles.btn}
          onClick={(e) => {
            handleSummarizeResult(e);
          }}
        >
          Tổng hợp kết quả
        </PrimaryButton>
      </Box>

      <br />

      <AntTabs
        value={selectedTab}
        onChange={handleChangeTab}
        aria-label="ant example"
        variant="scrollable"
        scrollButtons="auto"
      >
        {tabsLabel.map((label, idx) => (
          <AntTab key={label} label={label} {...a11yProps(idx)} />
        ))}
      </AntTabs>

      <TabPanel value={selectedTab} index={0} dir={theme.direction}>
        <QuizTestStudentList testId={param.id} />
      </TabPanel>
      <TabPanel value={selectedTab} index={1} dir={theme.direction}>
        <QuizTestJoinRequestList testId={param.id} />
      </TabPanel>
      <TabPanel value={selectedTab} index={2} dir={theme.direction}>
        <QuizTestGroupList testId={param.id} />
      </TabPanel>
      <TabPanel value={selectedTab} index={3} dir={theme.direction}>
        <QuizTestGroupParticipants testId={param.id} />
      </TabPanel>
      <TabPanel value={selectedTab} index={4} dir={theme.direction}>
        <QuizListForAssignment testId={param.id} />
      </TabPanel>
      <TabPanel value={selectedTab} index={5} dir={theme.direction}>
        <QuizQuestionsInQuizTest testId={param.id} />
      </TabPanel>

      <TabPanel value={selectedTab} index={6} dir={theme.direction}>
        <QuizTestStudentListResult testId={param.id} isGeneral={false} />
      </TabPanel>
      <TabPanel value={selectedTab} index={7} dir={theme.direction}>
        <QuizTestStudentListResult testId={param.id} isGeneral={true} />
      </TabPanel>
      <TabPanel value={selectedTab} index={8} dir={theme.direction}>
        <QuizTestResultChart testId={param.id} />
      </TabPanel>
      <TabPanel value={selectedTab} index={9} dir={theme.direction}>
        <ParticipantRolesOfQuizTest testId={param.id} />
      </TabPanel>
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

      {/*  */}
      <Box display="flex" alignItems="center" pt={2}>
        {/*  */}
        <Skeleton width={24} height={24} variant="circle" animation="wave" />
        <Typography component="span" sx={styles.time}>
          <Skeleton width={80} variant="rect" animation="wave" />
        </Typography>
      </Box>
    </>
  );
}

const screenName = "SCREEN_VIEW_QUIZ_TEST_TEACHER";
export default withScreenSecurity(QuizTestDetail, screenName, true);
