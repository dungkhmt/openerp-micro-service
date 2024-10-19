export const salemanager = {
  id: "MENU_WMS_SALE_MANAGER",
  icon: "DashboardIcon",
  text: "Giám đốc kinh doanh",
  child: [
    {
      id: "MENU_WMS_SALE_MANAGER.PRICE_CONFIG",
      path: "/sale-manager/price-config",
      isPublic: false,
      text: "Cấu hình giá bán",
      child: [],
    },
    {
      id: "MENU_WMS_SALE_MANAGER.RECEIPT_REQUEST",
      path: "/sale-manager/receipt-request",
      isPublic: false,
      text: "Tạo yêu cầu nhập hàng",
      child: [],
    },
    {
      id: "MENU_WMS_SALE_MANAGER.ORDERS",
      path: "/sale-manager/orders",
      isPublic: false,
      text: "Danh sách đơn mua hàng",
      child: [],
    },
  ],
};
