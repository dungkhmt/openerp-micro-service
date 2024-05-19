export const generalTimeTabling = {
  id: "MENU_GENERAL_TIME_TABLING",
  icon: "AnalyticsIcon",
  text: "Thời khóa biểu",
  child: [
    {
      id: "MENU_GENERAL_TIME_TABLING.UPLOAD_SCREEN",
      path: "/general-time-tabling/plan-class-open",
      isPublic: true,
      text: "Kế hoạch mở lớp",
      child: [],
    },
    {
      id: "MENU_GENERAL_TIME_TABLING.UPLOAD_SCREEN",
      path: "/general-time-tabling/upload-class",
      isPublic: true,
      text: "Danh sách lớp",
      child: [],
    },
    {
      id: "MENU_GENERAL_TIME_TABLING.GROUP_SCREEN",
      path: "/general-time-tabling/group-class",
      isPublic: true,
      text: "Phân nhóm lớp",
      child: [],
    },
    {
      id: "MENU_GENERAL_TIME_TABLING.SCHEDULE_SCREEN",
      path: "/general-time-tabling/schedule-class",
      isPublic: true,
      text: "Xếp thời khóa biểu",
      child: [],
    },
    {
      id: "MENU_GENERAL_TIME_TABLING.ROOM_OCCUPATION",
      path: "/general-time-tabling/room-occupation",
      isPublic: true,
      text: "Xem tình trạng phòng học",
      child: [],
    },
  ],
};
