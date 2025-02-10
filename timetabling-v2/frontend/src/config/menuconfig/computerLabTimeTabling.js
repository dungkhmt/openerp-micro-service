const prefix = "/lab-time-tabling";
export const computerLabTimeTabling = {
    id: "MENU_LAB_TIMETABLING",
    icon: "AnalyticsIcon",
    text: "Quản lý lịch thực hành",
    child: [
      {
        id: "MENU_LAB_TIMETABLING_CLASS_LIST",
        path: `${prefix}/class-management`,
        isPublic: false,
        text: "Quản lý lớp học",
        child: [],
      },
      {
        id: "MENU_LAB_TIMETABLING_ROOM_LIST",
        path: `${prefix}/room-management`,
        isPublic: false,
        text: "Quản lý phòng học",
        child: [],
      },
      {
        id: "MENU_LAB_TIMETABLING_AVAILABLE_ROOM_LIST",
        path: `${prefix}/room-availibility`,
        isPublic: false,
        text: "Phòng học sẵn sàng",
        child: [],
      },
      {
        id: "MENU_LAB_TIMETABLING_TIMETABLE",
        path: `${prefix}/timetable`,
        isPublic: false,
        text: "Thời khóa biểu",
        child: [],
      },
      // {
      //   id: "MENU_SCHEDULER",
      //   path: `${prefix}/conflict-checking`,
      //   isPublic: true,
      //   text: "Lịch màu",
      //   child: [],
      // },
      {
        id: "MENU_LAB_TIMETABLING_TIMETABLE_MANUAL",
        path: `${prefix}/manual-assign`,
        isPublic: false,
        text: "Xếp lịch thủ công",
        child: [],
      },
      {
        id: "MENU_LAB_TIMETABLING_TIMETABLE_AUTO",
        path: `${prefix}/auto-assign`,
        isPublic: false,
        text: "Xếp lịch tự động",
        child: [],
      },
    ],
  };
