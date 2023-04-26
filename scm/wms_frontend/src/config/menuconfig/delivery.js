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
      id: "MENU_WMS_DELIVERY.SPLIT_ORDER",
      path: "/delivery/split-order",
      isPublic: false,
      text: "Split order",
      child: [],
    },
  ],
};
