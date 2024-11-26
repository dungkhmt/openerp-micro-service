import { useState } from "react";
import { useParams } from "react-router";
import withScreenSecurity from "../../withScreenSecurity";
import TeacherViewCourseDetail from "./teacher/TeacherViewCourseDetail";
import TeacherCourseChapterList from "./TeacherCourseChapterList";
import TeacherCourseQuizList from "./TeacherCourseQuizList";
import TeacherCourseTopicList from "./TeacherCourseTopicList";
import TeacherViewCourseQuizList from "./TeacherViewCourseQuizList";

function TeacherCourseDetail() {
  const params = useParams();
  const courseId = params.id;

  const [course, setCourse] = useState(null);

  console.log(courseId);

  return (
    <>
      <TeacherViewCourseDetail courseId={courseId} />
      <br />

      {/* <TeacherCourseChapterList courseId={courseId} />
      <TeacherCourseQuizList courseId={courseId} />
      <TeacherCourseTopicList courseId={courseId} />
      <TeacherViewCourseQuizList courseId={courseId} /> */}
    </>
  );
}

const screenName = "SCREEN_EDUCATION_TEACHING_MANAGEMENT_TEACHER";
export default withScreenSecurity(TeacherCourseDetail, screenName, true);
