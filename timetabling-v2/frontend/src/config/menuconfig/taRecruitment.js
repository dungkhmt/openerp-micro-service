const prefix = "/ta-recruitment";
export const taRecruitment = {
  id: "MENU_TA_RECRUITMENT",
  icon: "DashboardIcon",
  text: "Quản lý trợ giảng",
  child: [
    {
      id: "MENU_TA_RECRUITMENT.ASSIGN_CLASS",
      path: `${prefix}/student/classregister-list`,
      isPublic: false,
      text: "Đăng ký lớp trợ giảng",
      child: [],
    },
    {
      id: "MENU_TA_RECRUITMENT.STUDENT_RESULT.VIEW",
      path: `${prefix}/student/result`,
      isPublic: false,
      text: "Kết quả tuyển dụng",
      child: [],
    },
    {
      id: "MENU_TA_RECRUITMENT.CLASS_LIST",
      path: `${prefix}/teacher/dashboard`,
      isPublic: false,
      text: "Thống kê",
      child: [],
    },
    {
      id: "MENU_TA_RECRUITMENT.CLASS_LIST",
      path: `${prefix}/teacher/class-list`,
      isPublic: false,
      text: "Danh sách lớp học",
      child: [],
    },
    {
      id: "MENU_TA_RECRUITMENT.REQUEST_APPLICATION",
      path: `${prefix}/teacher/request-approval`,
      isPublic: false,
      text: "Xác nhận tuyển dụng",
      child: [],
    },
    {
      id: "MENU_TA_RECRUITMENT.ASSIGN",
      path: `${prefix}/teacher/ta-assignment`,
      isPublic: false,
      text: "Phân công trợ giảng",
      child: [],
    },
    {
      id: "MENU_TA_RECRUITMENT.ASSIGN",
      path: `${prefix}/teacher/ta-assist-list`,
      isPublic: false,
      text: "Danh sách trợ giảng",
      child: [],
    },
  ],
};
