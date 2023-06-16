export const trip = {
    id: "MENU_CONTAINER_TRIP",
    icon: "Shipment",
    text: "Trip Management",
    child: [
      {
        id: "MENU_CONTAINER.TRIP_PENDING",
        path: "/trip/pending",
        isPublic: false,
        text: "Trip Pending",
        child: [],
      },
      {
        id: "MENU_CONTAINER.TRIP_EXECUTED",
        path: "/trip/executed",
        isPublic: false,
        text: "Trip Executed",
        child: [],
      },
    ],
  };