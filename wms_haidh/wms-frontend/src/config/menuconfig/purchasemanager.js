export const purchasemanager = {
  id: "MENU_WMSv2_PURCHASE_MANAGER",
  icon: "PurchaseManagerIcon",
  text: "Purchase Management",
  child: [
    {
      id: "MENU_WMSv2_PURCHASE_MANAGER.PROCESS_RECEIPT",
      path: "/purchase-manager/process-receipts",
      isPublic: true,
      text: "Process Receipt",
      child: [],
    },
    {
      id: "MENU_WMSv2_PURCHASE_MANAGER.BILL_RECEIPT",
      path: "/purchase-manager/receipt-bill",
      isPublic: true,
      text: "Receipt bill",
      child: [],
    }
  ],
};
