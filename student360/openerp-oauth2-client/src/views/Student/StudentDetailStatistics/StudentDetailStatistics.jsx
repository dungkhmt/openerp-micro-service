import React, { useState } from "react";
import { useParams } from "react-router-dom";
import { useKeycloak } from "@react-keycloak/web";
import Breadcrumb from "./Breadcrumb";
import { a11yProps, AntTab, AntTabs, TabPanel } from "../../../components/tab";
import StudentProfile from "./StudentProfile/StudentProfile";
import StudentStatisticsContest from "./StudentStatisticContest/StudentStatisticContest";
import StudentPerformance from "./StudentPerformance/StudentPerformance";
import StudentCourseRecommend from "./StudentCourseRecommend/StudentCourseRecommend";
import withScreenSecurity from "../../../components/common/withScreenSecurity";

function StudentDetailStatistics() {
  let { id } = useParams();
  const { keycloak } = useKeycloak();

  const [activeTab, setActiveTab] = useState(0);

  function handleChangeTab(event, tabIndex) {
    setActiveTab(tabIndex);
  }

  if (id == null) id = keycloak.tokenParsed.preferred_username;

  const tabsLabel = [
    "Thông tin sinh viên",
    "Thông tin học tập và đánh giá",
    "Phân tích học tập",
    "Khóa học gợi ý",
  ];

  return (
    <div>
      <Breadcrumb />
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
        <StudentProfile studentLoginId={id} />
      </TabPanel>
      <TabPanel value={activeTab} index={1}>
        <StudentPerformance studentLoginId={id} />
      </TabPanel>
      <TabPanel value={activeTab} index={2}>
        <StudentStatisticsContest studentLoginId={id} />
      </TabPanel>
      <TabPanel value={activeTab} index={3}>
        <StudentCourseRecommend studentLoginId={id} />
      </TabPanel>
    </div>
  );
}

const screenName = "MENU_STUDENT_RESULT.STUDENT_RESULT";
export default withScreenSecurity(StudentDetailStatistics, screenName, true);
