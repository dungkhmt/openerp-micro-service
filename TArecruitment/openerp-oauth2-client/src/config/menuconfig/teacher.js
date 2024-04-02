export const teacher = {
  id: "MENU_TEACHER",
  icon: "GiTeacher",
  text: "Teacher",
  child: [
    {
      id: "MENU_TEACHER.MENU_ITEM_1",
      path: "/teacher/create-class",
      isPublic: true,
      text: "Tạo lớp học",
      child: [],
    },
    {
      id: "MENU_TEACHER.MENU_ITEM_2",
      path: "/teacher/class-list",
      isPublic: true,
      text: "Danh sách lớp học",
      child: [],
    },
    {
      id: "MENU_TEACHER.MENU_ITEM_2",
      path: "/teacher/request-approval",
      isPublic: true,
      text: "Xác nhận tuyển dụng",
      child: [],
    },
    {
      id: "MENU_TEACHER.MENU_ITEM_2",
      path: "/teacher/ta-assignment",
      isPublic: true,
      text: "Phân công trợ giảng",
      child: [],
    },
  ],
};
