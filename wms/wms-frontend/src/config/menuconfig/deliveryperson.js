export const deliveryperson = {
  id: "MENU_WMSv2_DELIVERY_PERSON",
  icon: "DashboardIcon",
  text: "Nhân viên giao hàng",
  child: [
    {
      id: "MENU_WMSv2_DELIVERY_PERSON.DELIVERY_TRIP",
      path: "/delivery-person/trip",
      isPublic: false,
      text: "Danh sách chuyến giao hàng hôm nay",
      child: [],
    },
    // TODO: Danh sách các chuyến giao hàng trong lịch sử
  ],
};
