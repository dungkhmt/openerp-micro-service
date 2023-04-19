export const ThesisDefenseJuryManagement = {
  id: "MENU_EDUCATION_THESIS_DEFENSE_JURY_MANAGEMENT",
  path: "",
  isPublic: false,
  icon: "Schedule",
  text: "Hội đồng bảo vệ ĐATN",
  child: [
    {
      id: "MENU_EDUCATION_THESIS_DEFENSE_JURY_MANAGEMENT_LIST",
      path: "/thesis/defense_jury",
      isPublic: false,
      icon: "StarBorder",
      text: "DS Hội Đồng ",
      child: [],
    },
    {
      id: "MENU_EDUCATION_THESIS_DEFENSE_JURY_MANAGEMENT_CREATE",
      path: "/thesis/defense_jury/create",
      isPublic: false,
      icon: "StarBorder",
      text: "Tạo mới Hội Đồng ",
      child: [],
    },
    {
      id: "MENU_EDUCATION_THESIS_MANAGEMENT_CREATE",
      path: "/thesis/create",
      isPublic: false,
      icon: "StarBorder",
      text: "Tạo mới Đề tài  tốt nghiệp ",
      child: [],
    },
  ],
};
