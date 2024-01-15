export const courseTimeTabling = {
  id: "MENU_COURSE_TIME_TABLING",
  icon: "AnalyticsIcon",
  text: "Quản lý lịch học",
  child: [
    {
      id: "MENU_COURSE_TIME_TABLING.CLASS_LIST_OPENED",
      path: "/course-time-tabling/class-list-opened",
      isPublic: true,
      text: "Tải lên Danh sách lớp",
      child: [],
    },
    {
      id: "MENU_COURSE_TIME_TABLING.SCHEDULE",
      path: "/course-time-tabling/schedule",
      isPublic: true,
      text: "Phân nhóm lớp",
      child: [],
    },
    {
      id: "MENU_COURSE_TIME_TABLING.SCHEDULE",
      path: "/course-time-tabling/make-schedule",
      isPublic: true,
      text: "Xếp thời khóa biểu",
      child: [],
    },
  ],
};
