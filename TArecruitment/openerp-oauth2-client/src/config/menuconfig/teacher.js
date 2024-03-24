export const teacher = {
  id: "MENU_TEACHER",
  icon: "GiTeacher",
  text: "Teacher",
  child: [
    {
      id: "MENU_TEACHER.MENU_ITEM_1",
      path: "/teacher/create-class",
      isPublic: false,
      text: "Tạo lớp học",
      child: [],
    },
    {
      id: "MENU_TEACHER.MENU_ITEM_2",
      path: "/teacher/class-list",
      isPublic: false,
      text: "Danh sách lớp học",
      child: [],
    },
    {
      id: "MENU_TEACHER.MENU_ITEM_2",
      path: "/teacher/approving-ta",
      isPublic: false,
      text: "Xác nhận tuyển dụng",
      child: [],
    },
    {
      id: "MENU_TEACHER.MENU_ITEM_2",
      path: "/teacher/ta-management",
      isPublic: false,
      text: "Phân công trợ giảng",
      child: [],
    },
  ],
};
