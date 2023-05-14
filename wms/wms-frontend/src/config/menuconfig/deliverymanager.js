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
    // {
    //   id: "MENU_WMSv2_DELIVERY_MANAGER.APPROVAL_ORDERS",
    //   path: "/delivery-manager/orders",
    //   isPublic: false,
    //   text: "Danh sách các đơn hàng cần xử lý",
    //   child: [],
    // }
  ],
};
