export const general = {
  id: "MENU_GENERAL",
  icon: "DashboardIcon",
  text: "Business Reports",
  child: [
    {
      id: "MENU_GENERAL.REVENUE",
      path: "/director/revenue",
      isPublic: true,
      text: "Revenue & Profit",
      child: [],
    },
    {
      id: "MENU_GENERAL.CATEGORY",
      path: "/director/category",
      isPublic: true,
      text: "Top Categories",
      child: [],
    },
  ],
};
