export const ThesisDefensePlanManagement = {
  id: "MENU_EDUCATION_THESIS_DEFENSE_PLAN_MANAGEMENT",
  icon: "Schedule",
  text: "KH bảo vệ ĐATN",
  child: [
    {
      id: "MENU_EDUCATION_THESIS_DEFENSE_PLAN_MANAGEMENT.MENU_VIEW__LIST_THESIS_DEFENSE_PLAN",
      path: "/thesis/thesis_defense_plan",
      icon: "Schedule",
      text: "DS Đợt bảo vệ",
      isPublic: false,
      child: [],
    },
    {
      id: "MENU_EDUCATION_THESIS_DEFENSE_PLAN_MANAGEMENT.MENU_VIEW__LIST_ASSIGNED_THESIS_DEFENSE_PLAN",
      path: "/thesis/thesis_defense_plan/assigned",
      icon: "",
      text: "DS Đợt bảo vệ được phân công",
      isPublic: false,
      child: [],
    },
  ],
};

export const ThesisDefensePlanStudent = {
  id: "MENU_EDUCATION_THESIS_DEFENSE_PLAN_STUDENT",
  icon: "Schedule",
  text: "KH bảo vệ ĐATN",
  child: [
    {
      id: "MENU_EDUCATION_THESIS_DEFENSE_PLAN_STUDENT.MENU_VIEW_LIST_THESIS_DEFENSE_PLAN",
      path: "/thesis/student/thesis_defense_plan/assigned",
      icon: "Schedule",
      text: "DS Đợt bảo vệ được phân công",
      isPublic: false,
      child: [],
    },
    {
      id: "MENU_EDUCATION_THESIS_DEFENSE_PLAN_STUDENT.MENU_CREATE_THESIS",
      path: "/thesis/student/create",
      icon: "",
      text: "Tạo đồ án tốt nghiệp mới",
      isPublic: false,
      child: [],
    },
  ],
};
