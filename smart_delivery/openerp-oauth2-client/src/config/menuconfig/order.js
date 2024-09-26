export const order = {
    id: "MENU_HUB",
    icon: "DashboardIcon",
    text: "Đơn hàng",
    child: [
        {
            id: "MENU_WMSv2_ADMIN.WAREHOUSE",
            path: "/order/orderlist",
            isPublic: true,
            text: "Danh sách đơn hàng",
            child: [],
        },
        {
            id: "MENU_WMSv2_ADMIN.PRODUCT",
            path: "/order/createorder",
            isPublic: true,
            icon: "StarBorder",
            text: "Thêm đơn hàng",
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
