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
    title: "Quản lý nhân sự",
    icon: "mdi:account-group-outline",
    children: [
      {
        title: "Phòng ban",
        path: "/hr/department",
        icon: "mdi:office-building-outline",
      },
      {
        title: "Danh sách nhân sự",
        path: "/hr/staff",
        icon: "mdi:account-multiple-outline"
      },
      {
        title: "Vị trí công việc",
        path: "/hr/job-position",
        icon: "mdi:briefcase-outline"
      },
      {
        title: "Đánh giá nhân sự",
        icon: "mdi:star-check-outline",
        children: [
          {
            title: "Tiêu chí",
            path: "/hr/checkpoint/configure",
            icon: "mdi:clipboard-text-outline"
          },
          {
            title: "Đánh giá",
            path: "/hr/checkpoint/period",
            icon: "mdi:calendar-check-outline"
          },
        ]
      },
    ]
  },
  {
    sectionTitle: "Meetings",
  },
  {
    title: "Quản lý cuộc họp",
    icon: "mdi:event-multiple-check",
    children: [
      {
        title: "Cuộc họp đã tạo",
        path: "meetings/created-meetings",
      },
      {
        title: "Cuộc họp tham gia",
        path: "meetings/joined-meetings",
      },
    ],
  },
  {
    sectionTitle: "Administration",
  },
  {
    title: "Quản lý nhân viên",
    icon: "mdi:users-group-outline",
    children: [
      {
        title: "Danh sách nhân viên",
        path: "/user-management",
      },
    ],
  },
  {
    title: "Quản lý thuộc tính",
    icon: "mdi:category-outline",
    children: [
      {
        title: "Danh sách thuộc tính",
        path: "/attribute-management"

      },
    ],
  },
];

export default navigation;
