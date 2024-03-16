export const ReportsManagement = {
    id: "MENU_DASHBOARD_MANAGEMENT",
    icon: "AssessmentIcon",
    text: "Reports",
    child: [
        {
            id: "MENU_DASHBOARD_MANAGEMENT.COMPANY_REPORT",
            path: "/reports/company",
            isPublic: true,
            text: "Company Report",
            child: []
        },
        {
            id: "MENU_DASHBOARD_MANAGEMENT.LOCATION_REPORT",
            path: "/reports/location",
            isPublic: true,
            text: "Location Report",
            child: []
        }
    ]
};
