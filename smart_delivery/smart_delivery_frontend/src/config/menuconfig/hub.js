export const hub = {
    id: "MENU_HUB",
    icon: "WarehouseIcon",
    text: "Hub",
    child: [
        {
            id: "MENU_SCR_SMDELI_HUB_LIST",
            path: "/hubmanager/hublist",
            text: "Danh sách Hub",
            child: [],
        },
        {
            id: "MENU_SCR_SMDELI_HUB_CREATE",
            path: "/hubmanager/createhub",
            icon: "StarBorder",
            text: "Thêm Hub",
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
