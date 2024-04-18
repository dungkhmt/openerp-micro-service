export const student = {
  id: "MENU_STUDENT",
  icon: "LocalLibraryIcon",
  text: "Student",
  child: [
    // {
    //   id: "MENU_STUDENT.MENU_ITEM_1",
    //   path: "/student/class-register",
    //   isPublic: false,
    //   text: "Temp",
    //   child: [],
    // },
    {
      id: "MENU_STUDENT.MENU_ITEM_1",
      path: "/student/classregister-list",
      isPublic: true,
      text: "Đăng ký lớp trợ giảng",
      child: [],
    },
    {
      id: "MENU_STUDENT.MENU_ITEM_2",
      path: "/student/result",
      isPublic: true,
      text: "Kết quả tuyển dụng",
      child: [],
    },
  ],
};
