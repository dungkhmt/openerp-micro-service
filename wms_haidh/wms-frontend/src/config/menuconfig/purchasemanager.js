export const purchasemanager = {
  id: "MENU_WMSv2_PURCHASE_MANAGER",
  icon: "PurchaseManagerIcon",
  text: "Purchase Management",
  child: [
    {
      id: "MENU_WMSv2_PURCHASE_MANAGER.RECEIPT",
      path: "/purchase-manager/receipts",
      isPublic: true,
      text: "Receipt",
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
