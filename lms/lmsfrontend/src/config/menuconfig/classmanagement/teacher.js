export const eduTeachingManagement = {
  id: "MENU_EDUCATION_TEACHING_MANAGEMENT_TEACHER",
  path: "",
  isPublic: false,
  icon: "GiTeacher",
  text: "Teaching",
  child: [
    {
      id: "MENU_EDUCATION_TEACHING_MANAGEMENT_TEACHER_VIEW_LIST_COURSE",
      path: "/edu/teacher/course/list",
      isPublic: false,
      icon: null,
      text: "Courses",
      child: [],
    },
    {
      id: "MENU_EDUCATION_TEACHING_MANAGEMENT_TEACHER_VIEW_LIST_CLASS",
      path: "/edu/teacher/class/list",
      isPublic: false,
      icon: null,
      text: "Classes",
      child: [],
    },
    {
      id: "MENU_EDUCATION_TEACHING_MANAGEMENT_TEACHER_VIEW_ALL_CLASS",
      path: "/edu/teacher/all-class/list",
      isPublic: false,
      icon: null,
      text: "All Classes",
      child: [],
    },

    {
      id: "MENU_EDUCATION_TEACHING_MANAGEMENT_TEACHER_CREATE_CLASS",
      path: "/edu/class/add",
      isPublic: false,
      icon: null,
      text: "Create Class",
      child: [],
    },
    {
      id: "MENU_EDUCATION_TEACHING_MANAGEMENT_TEACHER_QUIZ_TEST_LIST",
      path: "/edu/class/quiztest/list",
      isPublic: false,
      icon: null,
      //Danh sách kỳ thi trắc nghiệm
      text: "Quiz Test",
      child: [],
    },
    {
      id: "MENU_EDUCATION_TEACHING_MANAGEMENT_ADMIN_ALL_QUIZ_TEST_LIST",
      path: "/edu/class/quiztest/list-all",
      isPublic: false,
      icon: null,
      //Danh sách kỳ thi trắc nghiệm
      text: "All Quiz Test",
      child: [],
    },

    {
      id: "",
      path: "/edu/teach/resource-links/list",
      isPublic: true,
      icon: null,
      text: "Resource Links",
      child: [],
    },
    /*
    {
      id: "MENU_EDUCATION_MANAGEMENT_PROGRAMMING_CONTEST",
      path: "/edu/management/contestprogramming",
      isPublic: true,
      icon: null,
      text: "Programming Contest",
      child: [],
    },
    */
  ],
};
