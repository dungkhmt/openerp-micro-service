export const backlog = {
  id: "MENU_BACKLOG",
  path: "",
  isPublic: false,
  icon: "AssignmentOutlinedIcon",
  text: "Theo dõi dự án",
  child: [
    {
      id: "MENU_BACKLOG_VIEW_LIST_PROJECT",
      path: "/projects",
      isPublic: false,
      icon: "StarBorder",
      text: "Danh sách dự án",
    },
    {
      id: "MENU_BACKLOG_CREATE_TASK_PROJECT",
      path: "/projects/new",
      isPublic: false,
      icon: "StarBorder",
      text: "Tạo mới dự án",
    },
    {
      id: "MENU_BACKLOG_VIEW_MY_TASK",
      path: "/tasks/assign-me",
      isPublic: false,
      icon: "StarBorder",
      text: "Công việc được giao",
    },
  ],
};
