export const order = {
    id: "MENU_CONTAINER_ORDER",
    icon: "OrderIcon",
    text: "Order",
    child: [
      {
        id: "MENU_CONTAINER.ORDER_WAIT",
        path: "/wait-approve/order",
        isPublic: false,
        text: "Order Wait Approve",
        child: [],
      },
      {
        id: "MENU_CONTAINER.ORDER_APPROVED",
        path: "/order",
        isPublic: false,
        text: "Approved Order Management",
        child: [],
      },
    ],
  };