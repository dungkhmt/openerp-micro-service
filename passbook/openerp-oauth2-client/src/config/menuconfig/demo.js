export const demo = {
  id: "MENU_DEMO",
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
      path: "/create-pass-book",
      isPublic: true,
      text: "Create Passbook",
      child: [],
    },
    {
      id: "MENU_DEMO.LIST_PASSBOOK",
      path: "/pass-book-list",
      isPublic: true,
      text: "Passbooks",
      child: [],
    },
  ],
};
