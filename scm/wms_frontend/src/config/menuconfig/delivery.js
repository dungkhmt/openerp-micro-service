export const delivery = {
  id: "MENU_WMS_DELIVERY",
  icon: "Delivery",
  text: "Delivery",
  child: [
    {
      id: "MENU_WMS_DELIVERY.SHIPMENT",
      path: "/delivery/shipment",
      isPublic: true,
      text: "Shipment",
      child: [],
    },
    {
      id: "MENU_WMS_DELIVERY.SPLIT_ORDER",
      path: "/delivery/split-order",
      isPublic: true,
      text: "Split order",
      child: [],
    },
    {
      id: "MENU_WMS_DELIVERY.VEHICLE.TRUCK",
      path: "/delivery/vehicle/truck",
      isPublic: true,
      text: "Truck",
      child: [],
    },
    {
      id: "MENU_WMS_DELIVERY.VEHICLE.DRONE",
      path: "/delivery/vehicle/drone",
      isPublic: true,
      text: "Drone",
      child: [],
    },
    // {
    //   id: "MENU_WMS_DELIVERY.VEHICLE",
    //   path: "/delivery/vehicle",
    //   isPublic: true,
    //   text: "Vehicles",
    //   child: [
    //     {
    //       id: "MENU_WMS_DELIVERY.VEHICLE.TRUCK",
    //       path: "/delivery/vehicle/truck",
    //       isPublic: true,
    //       text: "Truck",
    //       child: [],
    //     },
    //     {
    //       id: "MENU_WMS_DELIVERY.VEHICLE.DRONE",
    //       path: "/delivery/vehicle/drone",
    //       isPublic: true,
    //       text: "Drone",
    //       child: [],
    //     },
    //   ],
    // },
  ],
};
