export const courseTimeTabling = {
  id: "MENU_COURSE_TIME_TABLING",
  icon: "AnalyticsIcon",
  text: "Quản lý lịch học (TT)",
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
    {
      id: "MENU_COURSE_TIME_TABLING.CLASS_1_YEAR_STD_LIST_OPENED",
      path: "/course-time-tabling/class-list-opened-first-year-standard",
      isPublic: true,
      text: "Tải lên DS lớp năm 1",
      child: [],
    },
  ],
};
