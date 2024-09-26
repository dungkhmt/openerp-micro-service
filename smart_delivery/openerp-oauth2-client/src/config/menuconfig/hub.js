export const hub = {
    id: "MENU_HUB",
    icon: "DashboardIcon",
    text: "Thủ kho",
    child: [
        {
            id: "MENU_WMSv2_ADMIN.PRODUCT",
            path: "/hubmanager/hublist",
            isPublic: true,
            text: "Danh sách kho",
            child: [],
        },
        {
            id: "MENU_WMSv2_ADMIN.PRODUCT",
            path: "/hubmanager/createhub",
            isPublic: true,
            icon: "StarBorder",
            text: "Thêm kho",
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
