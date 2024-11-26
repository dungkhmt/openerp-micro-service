import React, { useEffect, useState } from "react";
import { useParams, useHistory } from "react-router";
import {
  a11yProps,
  AntTab,
  AntTabs,
  TabPanel,
} from "../../../../component/tab";
import { useTheme } from "@material-ui/core/styles";
import withScreenSecurity from "../../../../component/withScreenSecurity";
import QuizListForAssignment from "component/education/quiztest/QuizListForAssignment";
import TeacherViewInteractiveQuizList from "component/education/quiztest/TeacherViewInteractiveQuizList";
import QuestionInInteractiveQuiz from "./QuestionInInteractiveQuiz";
import TeacherViewInteractiveQuizResult from "./TeacherViewInteractiveQuizResult";
import TeacherViewInteractiveQuizSubmission from "./TeacherViewInteractiveQuizSubmission";

const tabsLabel = [
  "Ngân hàng câu hỏi",
  "Các câu hỏi trong đề",
  "Kết quả",
  "Danh sách nộp",
];

function TeacherViewInteractiveQuiz() {
  const history = useHistory();
  const isCourse = history.location.pathname.includes("course");
  const params = useParams();
  const testId = params.testId;
  //const history = useHistory();
  const [selectedTab, setSelectedTab] = useState(0);
  // const [open, setOpen] = useState(false);
  const theme = useTheme();
  function handleChangeTab(e, newTab) {
    setSelectedTab(newTab);
  }

  return (
    <div>
      <AntTabs
        value={selectedTab}
        onChange={handleChangeTab}
        aria-label="ant example"
        scrollButtons="auto"
        variant="scrollable"
      >
        {tabsLabel.map((label, idx) => (
          <AntTab key={label} label={label} {...a11yProps(idx)} />
        ))}
      </AntTabs>

      <TabPanel value={selectedTab} index={0} dir={theme.direction}>
        <TeacherViewInteractiveQuizList isCourse={isCourse} testId={testId} />
      </TabPanel>
      <TabPanel value={selectedTab} index={1} dir={theme.direction}>
        <QuestionInInteractiveQuiz isCourse={isCourse} testId={testId} />
      </TabPanel>
      <TabPanel value={selectedTab} index={2} dir={theme.direction}>
        {/* <TeacherViewDetailClassStudentRegistered classId={classId} /> */}
        <TeacherViewInteractiveQuizResult interactiveQuizId={testId} />
      </TabPanel>
      <TabPanel value={selectedTab} index={3} dir={theme.direction}>
        <TeacherViewInteractiveQuizSubmission interactiveQuizId={testId} />
      </TabPanel>
    </div>
  );
}

const screenName = "SCREEN_EDUCATION_TEACHING_MANAGEMENT_TEACHER";
export default withScreenSecurity(TeacherViewInteractiveQuiz, screenName, true);
