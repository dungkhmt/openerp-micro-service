export const backlog = {
  id: "MENU_BACKLOG",
  path: "",
  isPublic: false,
  icon: "AssignmentOutlinedIcon",
  text: "Theo dõi dự án",
  child: [
    {
      id: "MENU_BACKLOG_VIEW_LIST_PROJECT",
      path: "/project/list",
      isPublic: false,
      icon: "StarBorder",
      text: "DS dự án",
      child: [],
    },
    {
      id: "MENU_BACKLOG_CREATE_TASK_PROJECT",
      path: "/project/type/create",
      isPublic: false,
      icon: "StarBorder",
      text: "Tạo mới dự án",
      child: [],
    },
    {
      id: "MENU_BACKLOG_VIEW_MY_TASK",
      path: "/tasks/members/assigned",
      isPublic: false,
      icon: "StarBorder",
      text: "DS công việc được giao",
      child: [],
    }
  ],
};
