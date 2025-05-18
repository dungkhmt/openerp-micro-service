export const purchasestaffProduct = {
  id: "MENU_WMSv2_PURCHASE_STAFF",
  icon: "ProductInventoryIcon",
  text: "Purchase Requests",
  child: [
    {
      id: "MENU_WMSv2_PURCHASE_STAFF.PRODUCT",
      path: "/purchase-staff/products",
      isPublic: false,
      text: "Products",
      child: [],
    }
  ],
};
export const purchasestaffReceipt = {
  id: "MENU_WMSv2_PURCHASE_STAFF",
  icon: "ReceiptIcon",
  text: "Purchase Requests",
  child: [
    {
      id: "MENU_WMSv2_PURCHASE_STAFF.RECEIPT",
      path: "/purchase-staff/receipts",
      isPublic: false,
      text: "Purchase Orders",
      child: [],
    }
  ],
};