export const purchasestaff = {
  id: "MENU_WMSv2_PURCHASE_STAFF",
  icon: "PurchaseStaffIcon",
  text: "Purchase Requests",
  child: [
    {
      id: "MENU_WMSv2_PURCHASE_STAFF.PRODUCT",
      path: "/purchase-staff/products",
      isPublic: true,
      text: "Products",
      child: [],
    },
    {
      id: "MENU_WMSv2_PURCHASE_STAFF.RECEIPT",
      path: "/purchase-staff/receipts",
      isPublic: true,
      text: "Purchase Orders",
      child: [],
    }
  ],
};