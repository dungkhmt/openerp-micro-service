export const purchasemanager = {
  id: "MENU_WMSv2_PURCHASE_MANAGER",
  icon: "PurchaseManagerIcon",
  text: "Purchase Management",
  child: [
    {
      id: "MENU_WMSv2_PURCHASE_MANAGER.PROCESS_RECEIPT",
      path: "/purchase-manager/process-receipts",
      isPublic: false,
      text: "Purchase Orders",
      child: [],
    },
    {
      id: "MENU_WMSv2_PURCHASE_MANAGER.RECEIPT_BILL",
      path: "/purchase-manager/receipt-bill",
      isPublic: false,
      text: "Receipt Bills",
      child: [],
    }
  ],
};
