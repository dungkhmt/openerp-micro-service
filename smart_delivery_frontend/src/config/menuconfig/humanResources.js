export const humanResources = {
    id: "MENU_HUMAN_RESOURCES",
    icon: "GroupIcon",
    text: "Nhân sự",
    child: [
        {
            id: "MENU_EMPLOYEE_COLLECTOR",
            path: "/employee/collector",
            text: "Danh sách nhân viên thu gom",
            child: [],
        },
        {
            id: "MENU_EMPLOYEE_SHIPPER",
            path: "/employee/shipper",
            icon: "StarBorder",
            text: "Danh sách nhân viên giao hàng",
            child: [],
        },
        {
            id: "MENU_EMPLOYEE_DRIVER",
            path: "/employee/driver",
            icon: "StarBorder",
            text: "Danh sách lái xe",
            child: [],
        },
        {
            id: "MENU_EMPLOYEE_ADD",
            path: "/employee/add",
            icon: "StarBorder",
            text: "Thêm mới nhân sự",
            child: [],
        }

    ]
};
