const prefix = "/ta-recruitment";
export const taRecruitment = {
  id: "MENU_TA_RECRUITMENT",
  icon: "DashboardIcon",
  text: "Quản lý và phân công trợ giảng",
  child: [
    {
      id: "MENU_STUDENT.MENU_ITEM_1",
      path: `${prefix}/student/classregister-list`,
      isPublic: true,
      text: "Đăng ký lớp trợ giảng",
      child: [],
    },
    {
      id: "MENU_STUDENT.MENU_ITEM_2",
      path: `${prefix}/student/result`,
      isPublic: true,
      text: "Kết quả tuyển dụng",
      child: [],
    },
    {
      id: "MENU_TEACHER.MENU_ITEM_1",
      path: `${prefix}/teacher/create-class`,
      isPublic: true,
      text: "Tạo lớp học",
      child: [],
    },
    {
      id: "MENU_TEACHER.MENU_ITEM_2",
      path: `${prefix}/teacher/class-list`,
      isPublic: true,
      text: "Danh sách lớp học",
      child: [],
    },
    {
      id: "MENU_TEACHER.MENU_ITEM_2",
      path: `${prefix}/teacher/request-approval`,
      isPublic: true,
      text: "Xác nhận tuyển dụng",
      child: [],
    },
    {
      id: "MENU_TEACHER.MENU_ITEM_2",
      path: `${prefix}/teacher/ta-assignment`,
      isPublic: true,
      text: "Phân công trợ giảng",
      child: [],
    },
  ],
};
