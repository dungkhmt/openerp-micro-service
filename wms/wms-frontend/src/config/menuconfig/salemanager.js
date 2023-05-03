export const salemanager = {
  id: "MENU_WMSv2_SALE_MANAGER",
  icon: "DashboardIcon",
  text: "Giám đốc bán hàng",
  child: [
    {
      id: "MENU_WMSv2_SALE_MANAGER.PRICE_CONFIG",
      path: "/sale-manager/price-config",
      isPublic: false,
      text: "Cấu hình giá bán",
      child: [],
    },
    {
      id: "MENU_WMSv2_SALE_MANAGER.RECEIPT_REQUEST",
      path: "/sale-manager/receipt-request",
      isPublic: false,
      text: "Tạo đơn xin nhập hàng",
      child: [],
    },
    {
      id: "MENU_WMSv2_SALE_MANAGER.ORDERS",
      path: "/sale-manager/orders",
      isPublic: false,
      text: "Danh sách đơn mua hàng",
      child: [],
    },
  ],
};
