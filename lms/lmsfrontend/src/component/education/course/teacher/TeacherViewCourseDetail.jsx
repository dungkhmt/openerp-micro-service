import React, {useState} from 'react';
import {a11yProps, AntTab, AntTabs, TabPanel} from "../../../tab";
import TeacherViewChapterListOfCourse from "./TeacherViewChapterListOfCourse";
import TeacherViewTopicListOfCourse from "./TeacherViewTopicListOfCourse";
import TeacherViewQuizListOfCourse from "./TeacherViewQuizListOfCourse";
import TeacherViewQuizListDetailOfCourse from "./TeacherViewQuizListDetailOfCourse";
import PropTypes from "prop-types";

const tabsLabel = [
  "Danh sách chương",
  "Danh sách chủ đề",
  "Danh sách quiz",
  "Chi tiết các quiz"
]

export default function TeacherViewCourseDetail(props) {
  const courseId = props.courseId;
  const [activeTab, setActiveTab] = useState(0);

  return (
    <div>
      <AntTabs
        value={activeTab}
        onChange={(_, newTab) => setActiveTab(newTab)}
        aria-label="ant example"
        scrollButtons="auto"
        variant="scrollable"
      >
        {tabsLabel.map((label, idx) => (
          <AntTab key={label} label={label} {...a11yProps(idx)} />
        ))}
      </AntTabs>

      <TabPanel value={activeTab} index={0}>
        <TeacherViewChapterListOfCourse courseId={courseId}/>
      </TabPanel>
      <TabPanel value={activeTab} index={1}>
        <TeacherViewTopicListOfCourse courseId={courseId}/>
      </TabPanel>
      <TabPanel value={activeTab} index={2}>
        <TeacherViewQuizListOfCourse courseId={courseId}/>
      </TabPanel>
      <TabPanel value={activeTab} index={3}>
        <TeacherViewQuizListDetailOfCourse courseId={courseId}/>
      </TabPanel>
    </div>
  );
}

TeacherViewCourseDetail.propTypes = {
  courseId: PropTypes.string.isRequired
}