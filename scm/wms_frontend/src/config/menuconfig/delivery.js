export const delivery = {
  id: "MENU_WMS_DELIVERY",
  icon: "Delivery",
  text: "Delivery",
  child: [
    {
      id: "MENU_WMS_DELIVERY.SHIPMENT",
      path: "/delivery/shipment",
      isPublic: false,
      text: "Shipment",
      child: [],
    },
    {
      id: "MENU_WMS_DELIVERY.scheduling",
      path: "/delivery/scheduling",
      isPublic: false,
      text: "Scheduling",
      child: [],
    },
  ],
};
