export const ReportsManagement = {
    id: "MENU_DASHBOARD_MANAGEMENT",
    icon: "AssessmentIcon",
    text: "Reports",
    child: [
        {
            id: "MENU_DASHBOARD_MANAGEMENT.ASSET_REPORT",
            path: "/reports/asset",
            isPublic: true,
            text: "Asset Report",
            child: []
        },
        {
            id: "MENU_DASHBOARD_MANAGEMENT.REQUEST_REPORT",
            path: "/reports/request",
            isPublic: true,
            text: "Request Report",
            child: []
        }
    ]
};
