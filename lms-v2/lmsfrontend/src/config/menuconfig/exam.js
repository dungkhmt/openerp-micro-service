export const ExamMenu = {
    id: "MENU_EXAM",
    path: "",
    isPublic: true,
    icon: "DataManagementIcon",
    text: "Exam",
    child: [
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
        isPublic: false,
        icon: null,
        text: "My Exams",
        child: [],
      }
    ],
  };
  