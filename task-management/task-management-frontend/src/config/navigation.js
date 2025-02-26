const navigation = [
  {
    sectionTitle: "Dashboard",
  },
  {
    title: "Dashboard",
    icon: "mdi:home-outline",
    badgeContent: "Mới",
    badgeColor: "info",
    children: [
      {
        title: "Dashboard",
        path: "/dashboard",
      },
    ],
  },
  {
    sectionTitle: "My Profile",
  },
  {
    title: "My Profile",
    icon: "mdi:account-outline",
    children: [
      {
        title: "My Profile",
        path: "/my-profile",
      },
    ],
  },
  {
    sectionTitle: "Projects & Tasks",
  },
  {
    title: "Theo dõi dự án",
    icon: "mdi:file-document-outline",
    children: [
      {
        title: "Danh sách dự án",
        path: "/projects",
      },
      {
        title: "Tạo mới dự án",
        path: "/projects/new",
      },
    ],
  },
  {
    title: "Theo dõi công việc",
    icon: "mdi:format-list-checks",
    children: [
      {
        title: "Công việc được giao",
        path: "/tasks/assign-me",
      },
      {
        title: "Công việc đã tạo",
        path: "/tasks/created-by-me",
      },
    ],
  },
  {
    sectionTitle: "User Management",
  },
  {
    title: "Quản lý nhân viên",
    icon: "mdi:account-cog-outline",
    children: [
      {
        title: "Danh sách nhân viên",
        path: "/user-management",
      },
    ],
  },
];

export default navigation;
