export const deliverymanager = {
  id: "MENU_WMSv2_DELIVERY_MANAGER",
  icon: "DeliveryManagerIcon",
  text: "Delivery Management",
  child: [
    {
      id: "MENU_WMSv2_DELIVERY_MANAGER.DELIVERY_PERSON",
      path: "/delivery-manager/delivery-person",
      isPublic: true,
      text: "Manage Delivery Staff",
      child: [],
    },
    {
      id: "MENU_WMSv2_DELIVERY_MANAGER.SHIPMENTS",
      path: "/delivery-manager/shipments",
      isPublic: true,
      text: "Shipment",
      child: [],
    },
    {
      id: "MENU_WMSv2_DELIVERY_MANAGER.DELIVERY_TRIPS",
      path: "/delivery-manager/delivery-trip",
      isPublic: true,
      text: "Delivery Trips",
      child: [],
    },
    {
      id: "MENU_WMSv2_DELIVERY_MANAGER.ITEMS",
      path: "/delivery-manager/items",
      isPublic: true,
      text: "Items to Process",
      child: [],
    }
  ],
};
