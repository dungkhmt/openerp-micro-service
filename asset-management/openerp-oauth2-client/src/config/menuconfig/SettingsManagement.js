export const SettingsManagement = {
    id: "MENU_SETTINGS_MANAGEMENT",
    icon: "SettingsIcon",
    text: "Settings",
    child: [
        {
            id: "MENU_SETTINGS_MANAGEMENT.LOCATION",
            path: "/locations",
            isPublic: true,
            text: "Location",
            child: []
        },
        {
            id: "MENU_SETTINGS_MANAGEMENT.VENDORS",
            path: "/vendors",
            isPublic: true,
            text: "Vendors",
            child: []
        },
        {
            id: "MENU_SETTINGS_MANAGEMENT.ASSET_TYPES",
            path: "/types",
            isPublic: true,
            text: "Asset Types",
            child: []
        }
    ]
};
