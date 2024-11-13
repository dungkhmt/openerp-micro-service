import DoingPracticeQuizLogsOfStudent from "component/dataadmin/viewlearningprofiles/DoingPracticeQuizLogsOfStudent";
import ProgrammingContestSubmissionsOfStudent from "component/dataadmin/viewlearningprofiles/ProgrammingContestSubmissionsOfStudent";
import StudentDetail from "component/dataadmin/viewlearningprofiles/StudentDetail";
import ViewClassMaterialLogsOfStudent from "component/dataadmin/viewlearningprofiles/ViewClassMaterialLogsOfStudent";
import { a11yProps, AntTab, AntTabs, TabPanel } from "component/tab";
import withScreenSecurity from "component/withScreenSecurity";
import { useState } from "react";
import { useParams } from "react-router";

function StudentLearningProfiles(props) {
  const params = useParams();
  const studentLoginId = params.studentLoginId;
  const [activeTab, setActiveTab] = useState(0);

  function handleChangeTab(event, tabIndex) {
    setActiveTab(tabIndex);
  }

  const tabsLabel = [
    "Thông tin chung SV",
    "Xem học liệu",
    "Quiz Test",
    "Programming Contest",
  ];

  return (
    <div>
      <AntTabs
        value={activeTab}
        onChange={handleChangeTab}
        aria-label="student-view-class-detail-tabs"
        scrollButtons="auto"
        variant="scrollable"
      >
        {tabsLabel.map((label, idx) => (
          <AntTab key={label} label={label} {...a11yProps(idx)} />
        ))}
      </AntTabs>

      <TabPanel value={activeTab} index={0}>
        <StudentDetail studentLoginId={studentLoginId} />
      </TabPanel>
      <TabPanel value={activeTab} index={1}>
        <ViewClassMaterialLogsOfStudent studentLoginId={studentLoginId} />
      </TabPanel>
      <TabPanel value={activeTab} index={2}>
        <DoingPracticeQuizLogsOfStudent studentLoginId={studentLoginId} />
      </TabPanel>
      <TabPanel value={activeTab} index={3}>
        <ProgrammingContestSubmissionsOfStudent
          studentLoginId={studentLoginId}
        />
      </TabPanel>
    </div>
  );
}

const screenName = "SCR_ADMIN_LEARNING_PROFILE";
export default withScreenSecurity(StudentLearningProfiles, screenName, true);
