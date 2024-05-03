const prefix = "/ta-recruitment";
export const taRecruitment = {
  id: "MENU_TEACHER",
  icon: "DashboardIcon",
  text: "Quản lý và phân công trợ giảng",
  child: [
    {
      id: "MENU_STUDENT.MENU_ITEM_1",
      path: `${prefix}/student/classregister-list`,
      isPublic: false,
      text: "Đăng ký lớp trợ giảng",
      child: [],
    },
    {
      id: "MENU_STUDENT.MENU_ITEM_2",
      path: `${prefix}/student/result`,
      isPublic: false,
      text: "Kết quả tuyển dụng",
      child: [],
    },
    {
      id: "MENU_TEACHER.MENU_ITEM_1",
      path: `${prefix}/teacher/create-class`,
      isPublic: false,
      text: "Tạo lớp học",
      child: [],
    },
    {
      id: "MENU_TEACHER.MENU_ITEM_2",
      path: `${prefix}/teacher/class-list`,
      isPublic: false,
      text: "Danh sách lớp học",
      child: [],
    },
    {
      id: "MENU_TEACHER.MENU_ITEM_2",
      path: `${prefix}/teacher/request-approval`,
      isPublic: false,
      text: "Xác nhận tuyển dụng",
      child: [],
    },
    {
      id: "MENU_TEACHER.MENU_ITEM_2",
      path: `${prefix}/teacher/ta-assignment`,
      isPublic: false,
      text: "Phân công trợ giảng",
      child: [],
    },
  ],
};
