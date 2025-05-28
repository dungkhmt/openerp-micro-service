const navigation = [
  {
    sectionTitle: "Dashboard",
  },
  {
    id: "MENU_DASHBOARD_GROUP",
    title: "Dashboard",
    icon: "mdi:home-outline",
    badgeContent: "Mới",
    badgeColor: "info",
    children: [
      {
        id: "DASHBOARD_VIEW",
        title: "Dashboard",
        path: "/dashboard",
      },
    ],
  },
  {
    sectionTitle: "My Profile",
  },
  {
    id: "MENU_PROFILE_GROUP",
    title: "My Profile",
    icon: "mdi:account-outline",
    children: [
      {
        id: "PROFILE_VIEW",
        title: "My Profile",
        path: "/my-profile",
      },
    ],
  },
  {
    sectionTitle: "Projects & Tasks",
  },
  {
    id: "MENU_PROJECTS_GROUP",
    title: "Theo dõi dự án",
    icon: "mdi:file-document-outline",
    children: [
      {
        id: "PROJECTS_LIST_VIEW",
        title: "Danh sách dự án",
        path: "/projects",
      },
      {
        id: "PROJECTS_CREATE_NEW",
        title: "Tạo mới dự án",
        path: "/projects/new",
      },
    ],
  },
  {
    id: "MENU_TASKS_GROUP",
    title: "Theo dõi công việc",
    icon: "mdi:format-list-checks",
    children: [
      {
        id: "TASKS_ASSIGNED_TO_ME",
        title: "Công việc được giao",
        path: "/tasks/assign-me",
      },
      {
        id: "TASKS_CREATED_BY_ME",
        title: "Công việc đã tạo",
        path: "/tasks/created-by-me",
      },
    ],
  },
  {
    sectionTitle: "HR Management",
  },
  {
    id: "MENU_HR_CHECKIN_OUT",
    title: "Chấm công",
    path: "/hr/checkin-out",
    icon: "mdi:clock-check-outline",
  },
  {
    id: "MENU_HR_STAFF_MANAGER",
    title: "Quản lý nhân sự",
    icon: "mdi:account-group-outline",
    children: [
      {
        id: "MENU_HR_STAFF_MANAGER.DEPARTMENT",
        title: "Phòng ban",
        path: "/hr/department",
        icon: "mdi:office-building-outline",
      },
      {
        id: "MENU_HR_STAFF_MANAGER.JOB_POSITION",
        title: "Vị trí công việc",
        path: "/hr/job-position",
        icon: "mdi:briefcase-outline"
      },
      {
        id: "MENU_HR_STAFF_MANAGER.STAFF",
        title: "Danh sách nhân sự",
        path: "/hr/staff",
        icon: "mdi:account-multiple-outline"
      },
      {
        id: "MENU_HR_STAFF_CHECKPOINT",
        title: "Đánh giá nhân sự",
        icon: "mdi:star-check-outline",
        children: [
          {
            id: "MENU_HR_STAFF_CHECKPOINT.CONFIGURE",
            title: "Tiêu chí",
            path: "/hr/checkpoint/configure",
            icon: "mdi:clipboard-text-outline"
          },
          {
            id: "MENU_HR_STAFF_CHECKPOINT.PERIOD",
            title: "Đánh giá",
            path: "/hr/checkpoint/period",
            icon: "mdi:calendar-check-outline"
          },
        ]
      },
    ]
  },
  {
    id: "MENU_HR_ATTENDANCE",
    title: "Quản lý công",
    icon: "mdi:calendar-clock-outline",
    children: [
      {
        id: "MENU_HR_ATTENDANCE.VIEW",
        title: "Bảng công tháng",
        path: "/hr/attendance-list",
        icon: "mdi:calendar-check-outline"
      },
      {
        id: "MENU_HR_ATTENDANCE.ABSENCE_ANNOUNCE",
        title: "Thông báo nghỉ phép",
        path: "/hr/absence/announce",
        icon: "mdi:calendar-alert-outline",
      },
      {
        id: "MENU_HR_ATTENDANCE",
        title: "Nghỉ phép cá nhân",
        path: "/hr/absence/me",
        icon: "mdi:calendar-account-outline",
      },
      {
        id: "MENU_HR_ATTENDANCE.ABSENCE_VIEW",
        title: "Danh sách nghỉ phép",
        path: "/hr/absence/list",
        icon: "mdi:calendar-today-outline",
      },
    ]
  },
  {
    id: "MENU_HR_BENEFITS",
    title: "Quản lý phúc lợi",
    icon: "mdi:account-heart-outline",
    children: [
      {
        id: "MENU_HR_BENEFITS.HOLIDAY",
        title: "Lịch nghỉ lễ",
        path: "/hr/holiday-schedule",
        icon: "mdi:calendar-heart-outline",
      },
      {
        id: "MENU_HR_BENEFITS.LEAVE_POLICY",
        title: "Quản lý nghỉ phép",
        path: "/hr/leave-policy",
        icon: "mdi:calendar-edit",
      },
    ]
  },
  {
    id: "MENU_HR_SALARY",
    title: "Quản lý lương",
    icon: "mdi:account-cash-outline",
    children: [
      {
        id: "MENU_HR_SALARY",
        title: "Bảng lương",
        path: "/hr/salary/list",
        icon: "mdi:file-chart-outline",
      },
      {
        id: "MENU_HR_SALARY",
        title: "Lịch sử tính lương",
        path: "/hr/salary/payrolls",
        icon: "mdi:history",
      },
    ]
  },
  {
    id: "MENU_HR_CONFIG",
    title: "Cấu hình",
    icon: "mdi:cog-outline",
    children: [
      {
        id: "MENU_HR_CONFIG",
        title: "Cấu hình doanh nghiệp",
        path: "/hr/config/company",
        icon: "mdi:domain",
      },
    ]
  },
  {
    id: "MENU_HR_SHIFT_SCHEDULER_VIEW",
    title: "Lịch làm việc",
    icon: "mdi:calendar-account",
    path: "/hr/shift-scheduler",
  },
  {
    id: "MENU_HR_SHIFT_MANAGER",
    title: "Xếp lịch tự động",
    icon: "mdi:calendar",
    path: "/hr/shift-manager",
  },
  {
    sectionTitle: "Meetings",
  },
  {
    id: "MENU_MEETINGS_GROUP",
    title: "Quản lý cuộc họp",
    icon: "mdi:event-multiple-check",
    children: [
      {
        id: "MEETINGS_CREATED_VIEW",
        title: "Cuộc họp đã tạo",
        path: "meetings/created-meetings",
      },
      {
        id: "MEETINGS_JOINED_VIEW",
        title: "Cuộc họp tham gia",
        path: "meetings/joined-meetings",
      },
    ],
  },
  {
    sectionTitle: "Administration",
  },
  {
    id: "MENU_ADMIN_USER_MGMT_GROUP",
    title: "Quản lý nhân viên",
    icon: "mdi:users-group-outline",
    children: [
      {
        id: "ADMIN_USER_LIST_VIEW",
        title: "Danh sách nhân viên",
        path: "/user-management",
      },
    ],
  },
  {
    id: "MENU_ADMIN_ATTR_MGMT_GROUP",
    title: "Quản lý thuộc tính",
    icon: "mdi:category-outline",
    children: [
      {
        id: "ADMIN_ATTR_LIST_VIEW",
        title: "Danh sách thuộc tính",
        path: "/attribute-management"
      },
    ],
  },
];

export default navigation;
