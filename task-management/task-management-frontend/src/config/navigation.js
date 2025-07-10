const navigation = [
  {
    sectionTitle: "Dashboard",
  },
  {

    title: "Dashboard",
    icon: "mdi:home-outline",
/*
    badgeContent: "Mới",
*/
    badgeColor: "info",
    children: [
      {
        id: "MENU_HR_DASHBOARD_ADMIN",
        title: "Dashboard",
        path: "/dashboard",
      },
      {
        id: "MENU_HR_DASHBOARD_STAFF",
        title: "Thống kê cá nhân",
        path: "/dashboard/employee",
      },
    ],
  },
  {
    sectionTitle: "My Profile",
  },
  {
    sectionTitle: "HR Management",
  },
  {
    id: "MENU_HR_CHECKIN_OUT",
    title: "Profile",
    path: "/hr/me",
    icon: "mdi:account-card-details-outline",
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
        id: "MENU_HR_ATTENDANCE.ABSENCE_ME",
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
];

export default navigation;
