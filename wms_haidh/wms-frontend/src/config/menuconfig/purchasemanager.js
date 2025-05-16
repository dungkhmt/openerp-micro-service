export const purchasemanager = {
  id: "MENU_WMSv2_PURCHASE_MANAGER",
  icon: "PurchaseManagerIcon",
  text: "Purchase Management",
  child: [
    {
      id: "MENU_WMSv2_PURCHASE_MANAGER.SUPPLIERS",
      path: "/purchase-manager/suppliers",
      isPublic: false,
      text: "Suppliers",
      child: [],
    },
    {
      id: "MENU_WMSv2_PURCHASE_MANAGER.PROCESS_RECEIPT",
      path: "/purchase-manager/process-receipts",
      isPublic: false,
      text: "Purchase Orders",
      child: [],
    }
  ],
};
