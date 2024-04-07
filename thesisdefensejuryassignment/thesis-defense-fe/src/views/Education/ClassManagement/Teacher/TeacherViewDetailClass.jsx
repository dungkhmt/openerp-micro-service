import React, { useState } from "react";
import { useParams } from "react-router";
import {
  a11yProps,
  AntTab,
  AntTabs,
  TabPanel,
} from "../../../../component/tab";
/*
import {
  Avatar,
  Card,
  CardContent,
  CardHeader,
  Paper,
  Typography,
  IconButton,
} from "@material-ui/core";
*/
//import EditIcon from "@material-ui/icons/Edit";
import { useTheme } from "@material-ui/core/styles";
import TeacherViewDetailClassStudentList from "./TeacherViewDetailClassStudentList";
import TeacherViewDetailClassStudentRegistered from "./TeacherViewDetailClassStudentRegistered";
//import TClassUpdatePopup from "./TClassUpdatePopup";
import TeacherViewDetailClassExercises from "./TeacherViewDetailClassExercises";
import TeacherViewDetailClassExerciseSubmission from "./TeacherViewDetailClassExerciseSubmission";
import TeacherViewDetailClassGeneralInfo from "./TeacherViewDetailClassGeneralInfo";
import TeacherViewLogUserQuizList from "../../../../component/education/course/TeacherViewLogUserQuizList";
//import TeacherViewQuizDetail from "../../../../component/education/course/TeacherViewQuizDetail";
import TeacherClassViewLearningSessionList from "./TeacherClassViewLearningSessionList";
import withScreenSecurity from "../../../../component/withScreenSecurity";
import TeacherViewAnalyzeDoQuizInClass from "../../../../component/education/course/TeacherViewAnalyzeDoQuizInClass";
import TeacherViewClassMaterialList from "component/education/course/teacher/TeacherViewClassMaterialList";

/*
const useStyles = makeStyles((theme) => ({
  btn: {
    // width: 180,
    marginLeft: theme.spacing(1),
  },
  courseName: { fontWeight: theme.typography.fontWeightMedium },
  // editBtn: {
  //   margin: theme.spacing(2),
  //   width: 100,
  //   fontWeight: theme.typography.fontWeightRegular,
  // },
  testName: { fontSize: "1.25rem", paddingTop: theme.spacing(1) },
  time: {
    paddingLeft: 6,
    color: teal[800],
    fontWeight: theme.typography.fontWeightMedium,
    fontSize: "1rem",
  },
}));
*/
const tabsLabel = [
  "Thông tin chung",
  "DS Sinh viên",
  "SV đăng ký",
  "Bài tập",
  "DS nộp bài tập",
  "Lịch sử học",
  "Buổi học",
  "Tài liệu lớp học",
];

function TeacherViewDetailClass() {
  const params = useParams();
  const classId = params.classId;
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
        <TeacherViewDetailClassGeneralInfo classId={classId} />
      </TabPanel>
      <TabPanel value={selectedTab} index={1} dir={theme.direction}>
        <TeacherViewDetailClassStudentList classId={classId} />
      </TabPanel>
      <TabPanel value={selectedTab} index={2} dir={theme.direction}>
        <TeacherViewDetailClassStudentRegistered classId={classId} />
      </TabPanel>
      <TabPanel value={selectedTab} index={3} dir={theme.direction}>
        <TeacherViewDetailClassExercises classId={classId} />
      </TabPanel>

      <TabPanel value={selectedTab} index={4} dir={theme.direction}>
        <TeacherViewDetailClassExerciseSubmission classId={classId} />
      </TabPanel>
      <TabPanel value={selectedTab} index={5} dir={theme.direction}>
        <TeacherViewLogUserQuizList classId={classId} />
        <TeacherViewAnalyzeDoQuizInClass classId={classId} />
      </TabPanel>
      <TabPanel value={selectedTab} index={6} dir={theme.direction}>
        <TeacherClassViewLearningSessionList classId={classId} />
      </TabPanel>
      <TabPanel value={selectedTab} index={7} dir={theme.direction}>
        <TeacherViewClassMaterialList classId={classId} />
      </TabPanel>
    </div>
  );
}

const screenName = "SCREEN_EDUCATION_TEACHING_MANAGEMENT_TEACHER";
export default withScreenSecurity(TeacherViewDetailClass, screenName, true);
