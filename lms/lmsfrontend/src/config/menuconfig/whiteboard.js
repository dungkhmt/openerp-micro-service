export const whiteboard = {
  id: "MENU_WHITE_BOARD",
  path: "",
  isPublic: false,
  icon: "AssignmentOutlinedIcon",
  text: "Bảng viết",
  child: [
    {
      id: "MENU_WHITE_BOARD_CREATE",
      path: "/whiteboard/board/list",
      isPublic: false,
      icon: "StarBorder",
      text: "DS bảng viết",
      child: [],
    },
    {
      id: "MENU_WHITE_BOARD_LIST",
      path: "/whiteboard/board",
      isPublic: false,
      icon: "StarBorder",
      text: "Tạo mới bảng viết",
      child: [],
    },
  ],
};
