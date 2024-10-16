export const order = {
    id: "MENU_ORDER",
    icon: "LocalGroceryStoreIcon",
    text: "Order management",
    child: [
        {
            id: "MENU_ORDER.ORDER",
            path: "/order/sale",
            isPublic: true,
            text: "Sale order",
            child: [],
        },
        {
            id: "MENU_ORDER.ASSIGN",
            path: "/order/assigned",
            isPublic: true,
            text: "Assigned order",
            child: [],
        }
    ],
};