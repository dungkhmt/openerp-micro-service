export const purchasemanager = {
  id: "MENU_WMSv2_PURCHASE_MANAGER",
  icon: "DashboardIcon",
  text: "Giám đốc mua hàng",
  child: [
    {
      id: "MENU_WMSv2_PURCHASE_MANAGER.RECEIPTS",
      path: "/purchase-manager/receipts",
      isPublic: false,
      text: "Danh sách yêu cầu nhập hàng",
      child: [],
    },
    {
      id: "MENU_WMSv2_PURCHASE_MANAGER.CREATE_RECEIPTS",
      path: "/purchase-manager/create-receipt",
      isPublic: false,
      text: "Tạo đơn nhập hàng",
      child: [],
    }
  ],
};
