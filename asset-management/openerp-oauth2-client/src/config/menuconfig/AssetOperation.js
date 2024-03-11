export const AssetOperaion = {
    id: "MENU_ASSET_OPERATION",
    icon: "MonitorIcon",
    text: "Asset Operation",
    child: [
        {
            id: "MENU_ASSET_OPERATION.REQUESTS",
            path: "/requests",
            isPublic: true,
            text: "Requests",
            child: []
        },
        {
            id: "MENU_ASSET_MANAGEMENT.AUDITS",
            path: "/audits",
            isPublic: true,
            text: "Audits",
            child: []
        },
        {
            id: "MENU_ASSET_MANAGEMENT.DEPRECIATION",
            path: "/depreciation",
            isPublic: true,
            text: "Depreciation",
            child: []
        }
    ]
};
