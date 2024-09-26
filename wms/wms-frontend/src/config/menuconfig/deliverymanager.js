export const deliverymanager = {
  id: "MENU_WMSv2_DELIVERY_MANAGER",
  icon: "DashboardIcon",
  text: "Quản lý giao hàng",
  child: [
    {
      id: "MENU_WMSv2_DELIVERY_MANAGER.DELIVERY_PERSON",
      path: "/delivery-manager/delivery-person",
      isPublic: false,
      text: "Quản lý nhân viên giao hàng",
      child: [],
    },
    {
      id: "MENU_WMSv2_DELIVERY_MANAGER.SHIPMENTS",
      path: "/delivery-manager/shipments",
      isPublic: false,
      text: "Danh sách các đợt giao hàng",
      child: [],
    },
    {
      id: "MENU_WMSv2_DELIVERY_MANAGER.DELIVERY_TRIPS",
      path: "/delivery-manager/delivery-trips",
      isPublic: false,
      text: "Danh sách các chuyến giao hàng",
      child: [],
    },
    {
      id: "MENU_WMSv2_DELIVERY_MANAGER.ITEMS",
      path: "/delivery-manager/items",
      isPublic: false,
      text: "Danh sách các sản phẩm cần xử lý",
      child: [],
    }
  ],
};
