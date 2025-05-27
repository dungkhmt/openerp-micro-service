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
    sectionTitle: "HR Management",
  },
  {
    title: "Chấm công",
    path: "/hr/checkin-out",
    icon: "mdi:clock-check-outline",
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
        title: "Vị trí công việc",
        path: "/hr/job-position",
        icon: "mdi:briefcase-outline"
      },
      {
        title: "Danh sách nhân sự",
        path: "/hr/staff",
        icon: "mdi:account-multiple-outline"
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
    title: "Quản lý công",
    icon: "mdi:calendar-clock-outline",
    children: [
      {
        title: "Bảng công tháng",
        path: "/hr/attendance-list",
        icon: "mdi:calendar-check-outline"
      },
      {
        title: "Thông báo nghỉ phép",
        path: "/hr/absence/announce",
        icon: "mdi:calendar-alert-outline",
      },
      {
        title: "Nghỉ phép cá nhân",
        path: "/hr/absence/me",
        icon: "mdi:calendar-account-outline",
      },
      {
        title: "Danh sách nghỉ phép",
        path: "/hr/absence/list",
        icon: "mdi:calendar-today-outline",
      },
    ]
  },
  {
    title: "Quản lý phúc lợi",
    icon: "mdi:account-heart-outline",
    children: [
      {
        title: "Lịch nghỉ lễ",
        path: "/hr/holiday-schedule",
        icon: "mdi:calendar-heart-outline",
      },
      {
        title: "Quản lý nghỉ phép",
        path: "/hr/leave-policy",
        icon: "mdi:calendar-edit",
      },
    ]
  },
  {
    title: "Quản lý lương",
    icon: "mdi:account-cash-outline",
    children: [
      {
        title: "Bảng lương",
        path: "/hr/salary/list",
        icon: "mdi:file-chart-outline",
      },
      {
        title: "Lịch sử tính lương",
        path: "/hr/salary/payrolls",
        icon: "mdi:history",
      },
    ]
  },
  {
    title: "Cấu hình",
    icon: "mdi:cog-outline",
    children: [
      {
        title: "Cấu hình doanh nghiệp",
        path: "/hr/config/company",
        icon: "mdi:domain",
      },
/*      {
        title: "Cơ chế tính lương",
        path: "/hr/config/salary",
        icon: "mdi:cash-multiple",
      },*/
    ]
  },
  {
    title: "Lịch làm việc",
    icon: "mdi:calendar-account",
    path: "/hr/shift-scheduler",
  },
  {
    title: "Xếp lịch tự động",
    icon: "mdi:calendar",
    path: "/hr/shift-manager",
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
