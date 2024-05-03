const prefix = "/lab-time-tabling";
export const computerLabTimeTabling = {
  id: "MENU_SCHEDULER",
  icon: "AnalyticsIcon",
  text: "Quản lý lịch thực hành",
  child: [
    {
      id: "MENU_SCHEDULER",
      path: `${prefix}/class-management`,
      isPublic: false,
      text: "Quản lý lớp học",
      child: [],
    },
    {
      id: "MENU_SCHEDULER",
      path: `${prefix}/room-management`,
      isPublic: false,
      text: "Quản lý phòng học",
      child: [],
    },
    {
      id: "MENU_SCHEDULER",
      path: `${prefix}/room-availibility`,
      isPublic: false,
      text: "Phòng học sẵn sàng",
      child: [],
    },
    {
      id: "MENU_SCHEDULER",
      path: `${prefix}/timetable`,
      isPublic: false,
      text: "Thời khóa biểu",
      child: [],
    },
    {
      id: "MENU_SCHEDULER",
      path: `${prefix}/conflict-checking`,
      isPublic: false,
      text: "Lịch màu",
      child: [],
    },
    {
      id: "MENU_SCHEDULER",
      path: "/scheduler",
      isPublic: false,
      text: "Xếp lịch tự động (chưa hoàn thiện)",
      child: [],
    },
  ],
};
