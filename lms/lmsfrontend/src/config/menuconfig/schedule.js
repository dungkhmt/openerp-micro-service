export const schedule = {
  id: "MENU_SCHEDULE",
  path: "",
  isPublic: false,
  icon: "DescriptionIcon",
  text: "Thời Khóa Biểu",
  child: [
    {
      id: "MENU_SCHEDULE_VIEW",
      path: "/schedule/view",
      isPublic: true,
      icon: "StarBorder",
      text: "Xem thời khóa biểu",
      child: [],
    },
    {
      id: "MENU_SCHEDULE_UPLOAD",
      path: "/schedule/upload",
      isPublic: true,
      icon: "StarBorder",
      text: "Tải Lên",
      child: [],
    },
  ],
};
