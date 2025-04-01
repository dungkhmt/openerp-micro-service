export const ExamMenu = {
    id: "MENU_EXAM",
    path: "",
    isPublic: true,
    icon: "DataManagementIcon",
    text: "Exam",
    child: [
      {
        id: "MENU_EXAM_QUESTION_BANK",
        path: "/exam/question-bank",
        isPublic: false,
        icon: null,
        text: "Question bank",
        child: [],
      },
      {
        id: "MENU_EXAM_TEST_BANK",
        path: "/exam/test-bank",
        isPublic: false,
        icon: null,
        text: "Test bank",
        child: [],
      },
      {
        id: "MENU_EXAM_MANAGEMENT",
        path: "/exam/management",
        isPublic: false,
        icon: null,
        text: "Manage Exams",
        child: [],
      },
      {
        id: "MENU_EXAMINEE_PARTICIPANT",
        path: "/exam/my-exam",
        isPublic: true,
        icon: null,
        text: "My Exams",
        child: [],
      }
    ],
  };
