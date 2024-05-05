import React, { useState } from "react";
import { useParams } from "react-router-dom";
import Breadcrumb from "./Breadcrumb";
import { a11yProps, AntTab, AntTabs, TabPanel } from "../../../components/tab";
import StudentProfile from "./StudentProfile/StudentProfile";
import StudentStatisticsContest from "./StudentStatisticContest/StudentStatisticContest";
import StudentPerformance from "./StudentPerformance/StudentPerformance";
import StudentCourseRecommend from "./StudentCourseRecommend/StudentCourseRecommend";

function StudentDetailStatistics() {
  const { id } = useParams();

  const [activeTab, setActiveTab] = useState(0);

  function handleChangeTab(event, tabIndex) {
    setActiveTab(tabIndex);
  }

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

export default StudentDetailStatistics;
