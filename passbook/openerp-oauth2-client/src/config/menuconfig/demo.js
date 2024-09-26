export const demo = {
  id: "MENU_PASSBOOK",
  icon: "DashboardIcon",
  text: "PassBook",
  child: [
    {
      id: "MENU_DEMO.DEMO",
      path: "/demo",
      isPublic: true,
      text: "Users",
      child: [],
    },
    {
      id: "MENU_DEMO.CREATE_PASSBOOK",
      path: "/create-passbook",
      isPublic: true,
      text: "Create Passbook",
      child: [],
    },
    {
      id: "MENU_PASSBOOK.MENU_LIST_PASSBOOK",
      path: "/passbook-list",
      isPublic: true,
      text: "Passbooks",
      child: [],
    },
    {
      id: "MENU_DEMO.OPTIMIZE_PASSBOOK_FOR_LOAD",
      path: "/optimize-passbook",
      isPublic: true,
      text: "Optimize Passbooks",
      child: [],
    },
  ],
};
