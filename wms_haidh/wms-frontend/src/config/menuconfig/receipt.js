export const receipt = {
    id: "MENU_RECEIPT",
    icon: "AssignmentOutlinedIcon",
    text: "Receipt management",
    child: [
        {
            id: "MENU_RECEIPT.ITEM_REQUEST",
            path: "/receipt/request",
            isPublic: true,
            text: "Request",
            child: [],
        },
        {
            id: "MENU_RECEIPT.LIST",
            path: "/receipt/list",
            isPublic: true,
            text: "Receipt",
            child: [],
        },
        {
            id: "MENU_RECEIPT.BILL",
            path: "/receipt/bill",
            isPublic: true,
            text: "Receipt bill",
            child: [],
        },
    ],
};
