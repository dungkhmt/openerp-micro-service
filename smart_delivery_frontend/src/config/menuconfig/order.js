export const order = {
    id: "MENU_ORDER",
    icon: "ReceiptIcon",
    text: "Đơn hàng",
    child: [
        {
            id: "MENU_ORDER_LIST",
            path: "/order/orderlist",
            text: "Danh sách đơn hàng",
            child: [],
        },
        {
            id: "MENU_ORDER_ASSIGN",
            path: "/order/assign",
            text: "Phân công",
            child: [],
        },
        {
            id: "MENU_ORDER_ASSIGN_TODAY",
            path: "/order/assign/today/me",
            icon: "StarBorder",
            text: "Phân công hôm nay",
            child: [],
        },
        {
            id: "MENU_ORDER_CREATE",
            path: "/order/createorder",
            icon: "StarBorder",
            text: "Tạo đơn hàng mới",
            child: [],
        },


        // {
        //     id: "MENU_WMSv2_ADMIN.ORDER",
        //     path: "/hubmanager/updatehub",
        //     isPublic: true,
        //     icon: "StarBorder",
        //     text: "Danh sách đơn xuất hàng",
        //     child: [],
        // },

    ]
};
