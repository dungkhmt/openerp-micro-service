import { Route, Routes, useLocation } from "react-router";
import AssignmentList from "../component/education/AssignmentList";
import ClassCreate from "../component/education/class/ClassCreate";
import { MainBoard } from "../component/education/whiteboard/MainBoard";
// import ClassesList from "../component/education/class/ClassesList";
import PlanDetail from "../component/education/classteacherassignment/assignmentPlan/PlanDetail";
import PlanList from "../component/education/classteacherassignment/PlanList";
import AddNewCourse from "../component/education/course/AddNewCourse";
import CourseDetail from "../component/education/course/CourseDetail";
import CourseList from "../component/education/course/CourseList";
import CreateChapterMaterialOfCourse from "../component/education/course/CreateChapterMaterialOfCourse";
import CreateChapterOfCourse from "../component/education/course/CreateChapterOfCourse";
import CreateQuizChoiceAnswerOfCourse from "../component/education/course/CreateQuizChoiceAnswerOfCourse";
import CreateQuizOfCourse from "../component/education/course/CreateQuizOfCourse";
import CreateTopicOfCourse from "../component/education/course/CreateTopicOfCourse";
import StudentCourseChapterDetail from "../component/education/course/StudentCourseChapterDetail";
import StudentCourseChapterMaterialDetail from "../component/education/course/StudentCourseChapterMaterialDetail";
import TeacherCourseChapterDetail from "../component/education/course/TeacherCourseChapterDetail";
import TeacherCourseChapterMaterialDetail from "../component/education/course/TeacherCourseChapterMaterialDetail";
import TeacherCourseDetail from "../component/education/course/TeacherCourseDetail";
import TeacherCourseList from "../component/education/course/TeacherCourseList";
import TeacherCourseQuizChoiceAnswerDetail from "../component/education/course/TeacherCourseQuizChoiceAnswerDetail";
import TeacherCourseQuizDetail from "../component/education/course/TeacherCourseQuizDetail";
import TeacherCourseTopicDetail from "../component/education/course/TeacherCourseTopicDetail";
import TeacherViewCourseQuizDetail from "../component/education/course/TeacherViewCourseQuizDetail";
// import CreateSemester from "../component/education/CreateSemester";
import CreateQuizTest from "../component/education/quiztest/CreateQuizTest";
import QuizTestDetail from "../component/education/quiztest/QuizTestDetail";
import QuizTestEdit from "../component/education/quiztest/QuizTestEdit";
import QuizTestList from "../component/education/quiztest/QuizTestList-old-tmp";
import QuizTestListAll from "../component/education/quiztest/QuizTestListAll";
import StudentQuizDetail from "../component/education/quiztest/StudentQuizDetail";
import StudentQuizDetailCheckAndConfirmGroupCode from "../component/education/quiztest/StudentQuizDetailCheckAndConfirmGroupCode";
import StudentQuizList from "../component/education/quiztest/StudentQuizTestList";
import StudentMyQuizTestList from "../component/education/quiztest/StudentMyQuizTestList";
import ResourceDomainList from "../component/education/resourcelink/ResourceDomainList";
import ResourceList from "../component/education/resourcelink/ResourceList";
import AddTeacher from "../component/education/teacher/AddTeacher";
import TeacherDetail from "../component/education/teacher/TeacherDetail";
import TeacherViewQuestionsOfParticipant from "../component/education/quiztest/TeacherViewQuestionsOfParticipant";
import ClassRegistration from "../views/Education/ClassManagement/Student/ClassRegistration";
import SAssignmentDetail from "../views/Education/ClassManagement/Student/SAssignmentDetail";
import SClassDetail from "../views/Education/ClassManagement/Student/SClassDetail";
import StudentViewLearningSessionDetail from "../views/Education/ClassManagement/Student/StudentViewLearningSessionDetail";
import CreateAssignment from "../views/Education/ClassManagement/Teacher/CreateAssignment";
import StudentLearningProgressDetail from "../views/Education/ClassManagement/Teacher/StudentLearningProgressDetail";
import TAssignmentDetail from "../views/Education/ClassManagement/Teacher/TAssignmentDetail";
import TClassDetail from "../views/Education/ClassManagement/Teacher/TClassDetail";
import TClassList from "../views/Education/ClassManagement/Teacher/TClassList";
import TAllClassList from "../views/Education/ClassManagement/Teacher/TAllClassList";
import TeacherViewDetailClass from "../views/Education/ClassManagement/Teacher/TeacherViewDetailClass";
import TeacherViewLearningSessionDetail from "../views/Education/ClassManagement/Teacher/TeacherViewLearningSessionDetail";

import StudentCreateThesis from "../component/education/thesisdefensejury/StudentCreateThesis";
import NotFound from "../views/errors/NotFound";
import ClassList from "../views/Education/ClassManagement/Student/ClassList";

export default function EduRoute() {
  let { pathname } = useLocation();
  return (
    <div>
      <Routes>
        <Route
          component={StudentLearningProgressDetail}
          path={`${pathname}/student/learning/detail/:id`}
          exact
        />

        <Route
          component={TeacherCourseList}
          path={`${pathname}/teacher/course/list`}
        />
        <Route
          component={TeacherCourseDetail}
          path={`${pathname}/course/detail/:id`}
          exact
        />
        <Route
          component={CreateChapterOfCourse}
          path={`${pathname}/course/detail/chapter/create/:courseId`}
          exact
        />
        <Route
          component={CreateQuizOfCourse}
          path={`${pathname}/course/detail/quiz/create/:courseId`}
          exact
        />
        <Route
          component={CreateTopicOfCourse}
          path={`${pathname}/course/detail/topic/create/:courseId`}
          exact
        />

        <Route
          component={TeacherCourseChapterDetail}
          path={`${pathname}/teacher/course/chapter/detail/:chapterId`}
          exact
        />
        <Route
          component={TeacherCourseQuizDetail}
          path={`${pathname}/teacher/course/quiz/detail/:questionId/:courseId`}
          exact
        />
        <Route
          component={TeacherCourseTopicDetail}
          path={`${pathname}/teacher/course/topic/detail/:quizCourseTopicId/:courseId`}
          exact
        />

        <Route
          component={TeacherViewCourseQuizDetail}
          path={`${pathname}/teacher/course/quiz/view/detail/:questionId/:courseId`}
          exact
        />

        <Route
          component={StudentCourseChapterDetail}
          path={`${pathname}/student/course/chapter/detail/:chapterId`}
          exact
        />

        <Route
          component={StudentCourseChapterDetail}
          path={`${pathname}/student/class/:classId/chapter/detail/:chapterId`}
          exact
        />

        <Route
          component={CreateChapterMaterialOfCourse}
          path={`${pathname}/course/detail/chapter/material/create/:chapterId`}
          exact
        />
        <Route
          component={CreateQuizChoiceAnswerOfCourse}
          path={`${pathname}/course/detail/quiz/choiceanswer/create/:questionId/:courseId`}
          exact
        />
        <Route
          component={TeacherCourseQuizChoiceAnswerDetail}
          path={`${pathname}/teacher/course/quiz/choiceanswer/detail/:choiceAnswerId`}
          exact
        />
        <Route
          component={TeacherCourseChapterMaterialDetail}
          path={`${pathname}/teacher/course/chapter/material/detail/:chapterMaterialId`}
          exact
        />

        <Route
          component={TeacherViewLearningSessionDetail}
          path={`${pathname}/teacher/class/session/detail/:sessionId`}
          exact
        />
        <Route
          component={ResourceDomainList}
          path={`${pathname}/teach/resource-links/list`}
        />
        <Route
          component={ResourceList}
          path={`${pathname}/domains/:id/resources`}
        />

        <Route
          component={PlanDetail}
          path={`${pathname}/teaching-assignment/plan/:planId/`}
        />

        <Route
          component={PlanList}
          path={`${pathname}/teaching-assignment/plan`}
        />

        <Route component={ClassCreate} path={`${pathname}/class/add`} />

        <Route
          component={StudentCourseChapterMaterialDetail}
          path={`${pathname}/student/course/chapter/material/detail/:chapterMaterialId`}
          exact
        />

        <Route
          component={CourseList}
          path={`${pathname}/teaching-assignment/courses`}
        />

        <Route component={CourseDetail} path={`${pathname}/course/detail`} />

        <Route component={AddNewCourse} path={`${pathname}/course/create`} />

        {/* <Route component={TeacherList} path={`${path}/teachers/list`} /> */}

        <Route component={TeacherDetail} path={`${pathname}/teacher/detail`} />

        <Route component={AddTeacher} path={`${pathname}/teacher/create`} />

        {/* <Route component={ClassesList} path={`${path}/classes-list`} /> */}

        <Route component={AssignmentList} path={`${pathname}/assignment`} />

        {/* <Route component={CreateSemester} path={`${path}/semester`} /> */}

        {/**
         * route for quiz test
         */}
        {/* Quiztest-001 */}
        <Route
          component={QuizTestList}
          path={`${pathname}/class/quiztest/list`}
        />
        <Route
          component={QuizTestListAll}
          path={`${pathname}/class/quiztest/list-all`}
        />
        {/* Quiztest-002 */}
        <Route
          component={QuizTestDetail}
          path={`${pathname}/class/quiztest/detail/:id`}
          exact
        />
        <Route
          component={TeacherViewQuestionsOfParticipant}
          path={`${pathname}/class/quiztest/teacher-view-questions-of-participant/:participantid/:quiztestgroupid/:testid`}
          exact
        />

        <Route
          component={QuizTestEdit}
          path={`${pathname}/class/quiztest/edit/:id`}
          exact
        />
        <Route
          component={CreateQuizTest}
          path={`${pathname}/class/quiztest/create-quiz-test`}
        />
        <Route
          component={StudentQuizList}
          path={`${pathname}/class/student/quiztest/list`}
        />
        <Route
          component={StudentMyQuizTestList}
          path={`${pathname}/class/student/myquiztest/list`}
        />

        <Route
          component={StudentQuizDetail}
          path={`${pathname}/class/student/quiztest/detail/:testId`}
        />
        <Route
          component={StudentQuizDetailCheckAndConfirmGroupCode}
          path={`${pathname}/class/student/quiztest-detail/check-confirm-code/:testId`}
        />

        {/* Class management. */}
        <Route
          component={ClassRegistration}
          path={`${pathname}/class/register`}
          exact
        />

        <Route
          component={ClassList}
          path={`${pathname}/student/class/list`}
          exact
        />

        <Route
          component={SAssignmentDetail}
          path={`${pathname}/student/class/:classId/assignment/:assignmentId`}
          exact
        />

        <Route
          component={SClassDetail}
          path={`${pathname}/student/class/:id`}
          exact
        />
        <Route
          component={StudentViewLearningSessionDetail}
          path={`${pathname}/student/class/session/detail/:sessionId`}
          exact
        />
        <Route
          component={MainBoard}
          path={`${pathname}/student/class/session/detail/:sessionId/whiteboard/:whiteboardId`}
          exact
        />

        <Route
          component={TClassList}
          path={`${pathname}/teacher/class/list`}
          exact
        />
        <Route
          component={TAllClassList}
          path={`${pathname}/teacher/all-class/list`}
          exact
        />

        <Route
          component={TeacherViewDetailClass}
          path={`${pathname}/teacher/class/detail/:classId`}
          exact
        />
        <Route
          component={MainBoard}
          path={`${pathname}/teacher/class/session/detail/:sessionId/whiteboard/:whiteboardId`}
          exact
        />

        <Route
          component={CreateAssignment}
          path={`${pathname}/teacher/class/:classId/assignment/create`}
          exact
        />

        <Route
          component={CreateAssignment}
          path={`${pathname}/teacher/class/:classId/assignment/:assignmentId/edit`}
          exact
        />

        <Route
          component={TAssignmentDetail}
          path={`${pathname}/teacher/class/:classId/assignment/:assignmentId`}
          exact
        />

        <Route
          component={TClassDetail}
          path={`${pathname}/teacher/class/:id`}
          exact
        />

        <Route
          component={StudentCreateThesis}
          path={`${pathname}/student/thesis/create`}
          exact
        />

        <Route component={NotFound} />
      </Routes>
    </div>
  );
}
