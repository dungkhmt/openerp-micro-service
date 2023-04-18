import {Card} from "@material-ui/core/";
import React, {useEffect, useState} from "react";
import {useDispatch, useSelector} from "react-redux";
import {useParams} from "react-router";
import {Link, useHistory} from "react-router-dom";
import {request} from "../../../api";
import TeacherCourseQuizChoiceAnswerList from "./TeacherCourseQuizChoiceAnswerList";
import TeacherCourseQuizContent from "./TeacherCourseQuizContent";
import withScreenSecurity from "../../withScreenSecurity";
import QuizUserRole from "./QuizUserRole";

function TeacherCourseQuizDetail() {
  const params = useParams();
  const questionId = params.questionId;
  const courseId = params.courseId;
  const dispatch = useDispatch();
  const token = useSelector((state) => state.auth.token);
  const history = useHistory();
  const [courseChapter, setCourseChapter] = useState(null);
  const [course, setCourse] = useState(null);

  function getCourseDetailOfQuestion() {
    request(
      "get",
      "get-course-of-quiz-question/" + questionId,
      (res) => {
        console.log(res);
        setCourse(res.data);
      },
      { 401: () => {} }
    );
  }
  useEffect(() => {
    getCourseDetailOfQuestion();
  }, []);

  return (
    <>
      <Card>
        <Link to={"/edu/course/detail/" + courseId}>QUAY VỀ CHI TIẾT MÔN</Link>
        <TeacherCourseQuizContent questionId={questionId} />
        <TeacherCourseQuizChoiceAnswerList
          questionId={questionId}
          courseId={courseId}
        />
      </Card>
      <QuizUserRole questionId={questionId} />
    </>
  );
}

const screenName = "SCREEN_EDUCATION_TEACHING_MANAGEMENT_TEACHER";
export default withScreenSecurity(TeacherCourseQuizDetail, screenName, true);
