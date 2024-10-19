export const purchasemanager = {
  id: "MENU_WMSv2_PURCHASE_MANAGER",
  icon: "DashboardIcon",
  text: "Purchase Management",
  child: [
    {
      id: "MENU_WMSv2_PURCHASE_MANAGER.RECEIPTS",
      path: "/purchase-manager/receipts",
      isPublic: true,
      text: "Receipt Request",
      child: [],
    },
    {
      id: "MENU_WMSv2_PURCHASE_MANAGER.CREATE_RECEIPTS",
      path: "/purchase-manager/create-receipt",
      isPublic: true,
      text: "Create Receipt",
      child: [],
    }
  ],
};
