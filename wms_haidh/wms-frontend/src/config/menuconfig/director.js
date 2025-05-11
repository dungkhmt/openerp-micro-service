export const director = {
  id: "MENU_GENERAL",
  icon: "DashboardIcon",
  text: "Business Reports",
  child: [
    {
      id: "MENU_GENERAL.REVENUE",
      path: "/director/revenue",
      isPublic: false,
      text: "Revenue & Profit",
      child: [],
    },
    {
      id: "MENU_GENERAL.CATEGORY",
      path: "/director/category",
      isPublic: false,
      text: "Top Categories",
      child: [],
    },
  ],
};
