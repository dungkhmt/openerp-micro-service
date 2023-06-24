export const delivery = {
  id: "MENU_SCM_DELIVERY",
  icon: "Delivery",
  text: "Delivery",
  child: [
    {
      id: "MENU_SCM_DELIVERY.SHIPMENT",
      path: "/delivery/shipment",
      isPublic: false,
      text: "Shipment",
      child: [],
    },
    {
      id: "MENU_SCM_DELIVERY.SPLIT_ORDER",
      path: "/delivery/split-order",
      isPublic: false,
      text: "Split order",
      child: [],
    },
    {
      id: "MENU_SCM_DELIVERY.VEHICLE.TRUCK",
      path: "/delivery/vehicle/truck",
      isPublic: false,
      text: "Truck",
      child: [],
    },
    {
      id: "MENU_SCM_DELIVERY.VEHICLE.DRONE",
      path: "/delivery/vehicle/drone",
      isPublic: false,
      text: "Drone",
      child: [],
    },
    {
      id: "MENU_SCM_DELIVERY.DELIVERY_STAFF",
      path: "/delivery/delivery-staff",
      isPublic: false,
      text: "Delivery Staff",
      child: [],
    },
  ],
};
