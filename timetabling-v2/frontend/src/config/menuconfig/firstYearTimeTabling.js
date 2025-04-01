export const firstYearTimeTabling = {
    id: "MENU_GENERAL_TIME_TABLING",
    icon: "AnalyticsIcon",
    text: "Quản lý lịch học (Năm 1)",
    child: [
      {
        id: "MENU_GENERAL_TIME_TABLING.UPLOAD_SCREEN",
        path: "/first-year-time-tabling/plan-class-open",
        isPublic: false,
        text: "Kế hoạch mở lớp",
        child: [],
      },
      {
        id: "MENU_GENERAL_TIME_TABLING.UPLOAD_SCREEN",
        path: "/first-year-time-tabling/upload-class",
        isPublic: false,
        text: "Danh sách lớp",
        child: [],
      },
      {
        id: "MENU_GENERAL_TIME_TABLING.GROUP_SCREEN",
        path: "/first-year-time-tabling/group-class",
        isPublic: false,
        text: "Phân nhóm lớp",
        child: [],
      },
      {
        id: "MENU_GENERAL_TIME_TABLING.SCHEDULE_SCREEN",
        path: "/first-year-time-tabling/schedule-class",
        isPublic: false,
        text: "Xếp thời khóa biểu",
        child: [],
      },
      {
        id: "MENU_GENERAL_TIME_TABLING.ROOM_OCCUPATION",
        path: "/first-year-time-tabling/room-occupation",
        isPublic: true,
        text: "Xem tình trạng phòng học",
        child: [],
      },
    ],
  };
  