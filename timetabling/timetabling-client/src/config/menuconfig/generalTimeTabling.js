export const generalTimeTabling = {
    id: "MENU_GENERAL_TIME_TABLING",
    icon: "AnalyticsIcon",
    text: "Quản lý lịch học (Đại trà)",
    child: [
      {
        id: "MENU_GENERAL_TIME_TABLING.UPLOAD_SCREEN",
        path: "/general-time-tabling/upload-class",
        isPublic: true,
        text: "Tải lên Danh sách lớp",
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
        text: "Xem sơ đồ phòng học",
        child: [],
      },
    ],
  };
  