import { Box, Typography } from "@material-ui/core/";
import { useTheme } from "@material-ui/core/styles";
import { a11yProps, AntTab, AntTabs, TabPanel } from "component/tab";
import { useState } from "react";
import { useParams } from "react-router";
import withScreenSecurity from "../../../withScreenSecurity";
import ParticipantRolesOfQuizTest from "./ParticipantRolesOfQuizTest";
import QuizListForAssignment from "../QuizListForAssignment";
import QuizQuestionsInQuizTest from "../QuizQuestionsInQuizTest";
import QuizTestResultChart from "../QuizTestResultChart";
import QuizTestGeneralInfo from "./QuizTestGeneralInfo";
import StudentsApprovedToQuizTest from "./StudentsApprovedToQuizTest";
import JoinQuizTestRequestList from "./JoinQuizTestRequestList";
import QuizGroupList from "./QuizGroupList";
import StudentAssignedToQuizGroups from "./StudentAssignedToQuizGroups";
import ResultListOfQuizTest from "./ResultListOfQuizTest";
import ViewDoingQuizTestQuestionLogs from "./ViewDoingQuizTestQuestionLogs";

const tabsLabel = [
  "Thí sinh",
  "Thí sinh đăng ký",
  "Đề",
  "Phân đề cho thí sinh",
  "DS quiz",
  "DS quiz trong Kỳ thi",
  "Kết quả",
  "Kết quả tổng quát",
  "Lịch sử làm quiz",
  "Biểu đồ",
  "User Vai trò",
];

function TeacherViewQuizTestDetail({ courseInfo }) {
  let params = useParams();
  const testId = params.id;
  const [activeTab, setActiveTab] = useState(0);
  const theme = useTheme();

  const handleChangeTab = (event, newValue) => {
    setActiveTab(newValue);
  };

  return (
    <>
      <QuizTestGeneralInfo testId={testId} />

      <AntTabs
        value={activeTab}
        onChange={handleChangeTab}
        aria-label="ant example"
        variant="scrollable"
        scrollButtons="auto"
      >
        {tabsLabel.map((label, idx) => (
          <AntTab key={label} label={label} {...a11yProps(idx)} />
        ))}
      </AntTabs>

      <TabPanel value={activeTab} index={0} dir={theme.direction}>
        <StudentsApprovedToQuizTest testId={testId} />
      </TabPanel>
      <TabPanel value={activeTab} index={1} dir={theme.direction}>
        <JoinQuizTestRequestList testId={testId} />
      </TabPanel>
      <TabPanel value={activeTab} index={2} dir={theme.direction}>
        <QuizGroupList testId={testId} />
      </TabPanel>
      <TabPanel value={activeTab} index={3} dir={theme.direction}>
        <StudentAssignedToQuizGroups testId={testId} />
      </TabPanel>
      <TabPanel value={activeTab} index={4} dir={theme.direction}>
        <QuizListForAssignment courseInfo={courseInfo} testId={testId} />
      </TabPanel>
      <TabPanel value={activeTab} index={5} dir={theme.direction}>
        <QuizQuestionsInQuizTest testId={testId} />
      </TabPanel>
      <TabPanel value={activeTab} index={6} dir={theme.direction}>
        <ResultListOfQuizTest testId={testId} isGeneral={false} />
      </TabPanel>
      <TabPanel value={activeTab} index={7} dir={theme.direction}>
        <ResultListOfQuizTest testId={testId} isGeneral={true} />
      </TabPanel>
      <TabPanel index={8} value={activeTab} dir={theme.direction}>
        <ViewDoingQuizTestQuestionLogs testId={testId} />
      </TabPanel>
      <TabPanel value={activeTab} index={9} dir={theme.direction}>
        <QuizTestResultChart testId={testId} />
      </TabPanel>
      <TabPanel value={activeTab} index={10} dir={theme.direction}>
        <ParticipantRolesOfQuizTest testId={testId} />
      </TabPanel>
    </>
  );
}

const screenName = "SCREEN_VIEW_QUIZ_TEST_TEACHER";
export default withScreenSecurity(TeacherViewQuizTestDetail, screenName, true);
