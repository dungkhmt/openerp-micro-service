const prefix = "/lab-time-tabling";
export const computerLabTimeTabling = {
    id: "MENU_SCHEDULER",
    icon: "AnalyticsIcon",
    text: "Quản lý lịch thực hành",
    child: [
      {
        id: "MENU_SCHEDULER",
        path: `${prefix}/class-management`,
        isPublic: true,
        text: "Quản lý lớp học",
        child: [],
      },
      {
        id: "MENU_SCHEDULER",
        path: `${prefix}/room-management`,
        isPublic: true,
        text: "Quản lý phòng học",
        child: [],
      },
      {
        id: "MENU_SCHEDULER",
        path: `${prefix}/room-availibility`,
        isPublic: true,
        text: "Phòng học sẵn sàng",
        child: [],
      },
      {
        id: "MENU_SCHEDULER",
        path: `${prefix}/timetable`,
        isPublic: true,
        text: "Thời khóa biểu",
        child: [],
      },
      {
        id: "MENU_SCHEDULER",
        path: `${prefix}/conflict-checking`,
        isPublic: true,
        text: "Lịch màu",
        child: [],
      },
      {
        id: "MENU_SCHEDULER",
        path: `${prefix}/auto-assign`,
        isPublic: true,
        text: "Xếp lịch tự động",
        child: [],
      },
    ],
  };
