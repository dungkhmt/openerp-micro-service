export const middleMile = {
    id: "MENU_ROUTE",
    icon: "AirportShuttleIcon",
    text: "Vận chuyển trung gian",
    child: [

        {
            id: "MENU_ROUTE.DRIVER_TIMETABLE",
            path: "/middle-mile/driver/dashboard",
            text: "Dashboard",
            child: [],
        },

        // {
        //     id: "MENU_ROUTE.VEHICLE_ASSIGNMENTS",
        //     path: "/middle-mile/vehicle-assignments",
        //     text: "Phân công phương tiện",
        //     child: [],
        // },
        // {
        //     id: "MENU_ROUTE.SCHEDULE",
        //     path: "/middle-mile/schedule",
        //     text: "Lịch trình",
        //     child: [],
        // },
        {
            id: "MENU_ROUTE.TRIPS_MANAGEMENT",
            path: "/middle-mile/trips",
            text: "Quản lý chuyến",
            child: [],
        },
        {
            id: "MENU_ROUTE.DRIVER_VEHICLE",
            path: "/middle-mile/driver-vehicle/manage",
            text: "Quản lý xe-tài xế",
            child: [],
        },
        {
            id: "MENU_ROUTE.VEHICLE",
            path: "/middle-mile/vehicle/manage",
            text: "Quản lý xe",
            child: [],
        },
        {
            id: "MENU_ROUTE.DRIVER",
            path: "/middle-mile/driver/manage",
            text: "Quản lý tài xế",
            child: [],
        },

    ],
};