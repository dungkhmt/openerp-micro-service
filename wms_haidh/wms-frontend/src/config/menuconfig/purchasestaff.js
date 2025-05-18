export const purchasestaffProduct = {
  id: "MENU_WMSv2_PURCHASE_STAFF.PRODUCT",
  icon: "ProductInventoryIcon",
  text: "Product Inventory",
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
  id: "MENU_WMSv2_PURCHASE_STAFF.RECEIPT",
  icon: "ReceiptIcon",
  text: "Create Purchase Orders",
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