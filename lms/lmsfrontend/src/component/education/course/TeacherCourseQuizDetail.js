import {Card} from "@material-ui/core/";
import {useEffect} from "react";
import {useParams} from "react-router";
import {Link} from "react-router-dom";
import {request} from "../../../api";
import withScreenSecurity from "../../withScreenSecurity";
import QuizUserRole from "./QuizUserRole";
import TeacherCourseQuizChoiceAnswerList from "./TeacherCourseQuizChoiceAnswerList";
import TeacherCourseQuizContent from "./TeacherCourseQuizContent";

function TeacherCourseQuizDetail() {
  const params = useParams();
  const questionId = params.questionId;
  const courseId = params.courseId;

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
