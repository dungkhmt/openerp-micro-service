export const admin = {
  id: "MENU_WMS_ADMIN",
  icon: "DashboardIcon",
  text: "Thủ kho",
  child: [
    {
      id: "MENU_WMS_ADMIN.WAREHOUSE",
      path: "/admin/warehouse",
      isPublic: true,
      text: "Danh sách kho",
      child: [],
    },
    {
      id: "MENU_WMS_ADMIN.PRODUCT",
      path: "/admin/product",
      isPublic: true,
      icon: "StarBorder",
      text: "Danh sách sản phẩm",
      child: [],
    },
    {
      id: "MENU_WMS_ADMIN.ORDER",
      path: "/admin/orders",
      isPublic: true,
      icon: "StarBorder",
      text: "Danh sách đơn xuất hàng",
      child: [],
    },
    {
      id: "MENU_WMS_ADMIN.PROCESS_RECEIPT",
      path: "/admin/process-receipts",
      isPublic: true,
      icon: "StarBorder",
      text: "Danh sách đơn nhập hàng",
      child: [],
    },
  ],
};
