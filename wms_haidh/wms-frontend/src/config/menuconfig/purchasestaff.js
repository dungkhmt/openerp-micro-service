export const purchasestaff = {
  id: "MENU_WMSv2_PURCHASE_STAFF",
  icon: "PurchaseStaffIcon",
  text: "Purchase Requests",
  child: [
    {
      id: "MENU_WMSv2_PURCHASE_STAFF.PRODUCT",
      path: "/purchase-staff/products",
      isPublic: false,
      text: "Products",
      child: [],
    },
    {
      id: "MENU_WMSv2_PURCHASE_STAFF.RECEIPT",
      path: "/purchase-staff/receipts",
      isPublic: false,
      text: "Purchase Orders",
      child: [],
    }
  ],
};