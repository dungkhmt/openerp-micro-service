export const deliverymanager = {
  id: "MENU_WMSv2_DELIVERY_MANAGER",
  icon: "DeliveryManagerIcon",
  text: "Delivery Management",
  child: [
    {
      id: "MENU_WMSv2_DELIVERY_MANAGER.DELIVERY_PERSON",
      path: "/delivery-manager/delivery-person",
      isPublic: true,
      text: "Delivery Staffs",
      child: [],
    },
    {
      id: "MENU_WMSv2_DELIVERY_MANAGER.SHIPMENTS",
      path: "/delivery-manager/shipments",
      isPublic: true,
      text: "Shipments",
      child: [],
    },
    {
      id: "MENU_WMSv2_DELIVERY_MANAGER.DELIVERY_TRIPS",
      path: "/delivery-manager/delivery-trip",
      isPublic: true,
      text: "Delivery Trips",
      child: [],
    }
  ],
};
