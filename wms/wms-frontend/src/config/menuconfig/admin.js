export const admin = {
  id: "MENU_WMSv2_ADMIN",
  icon: "DashboardIcon",
  text: "Thủ kho",
  child: [
    {
      id: "MENU_WMSv2_ADMIN.WAREHOUSE",
      path: "/admin/warehouse",
      isPublic: true,
      text: "Danh sách kho",
      child: [],
    },
    {
      id: "MENU_WMSv2_ADMIN.PRODUCT",
      path: "/admin/product",
      isPublic: false,
      icon: "StarBorder",
      text: "Danh sách sản phẩm",
      child: [],
    },
    {
      id: "MENU_WMSv2_ADMIN.ORDER",
      path: "/admin/orders",
      isPublic: false,
      icon: "StarBorder",
      text: "Danh sách đơn xuất hàng",
      child: [],
    },
    {
      id: "MENU_WMSv2_ADMIN.PROCESS_RECEIPT",
      path: "/admin/process-receipts",
      isPublic: false,
      icon: "StarBorder",
      text: "Danh sách đơn nhập hàng",
      child: [],
    },
  ],
};
