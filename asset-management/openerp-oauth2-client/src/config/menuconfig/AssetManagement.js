export const AssetManagement = {
    id: "MENU_ASSET_MANAGEMENT",
    icon: "LibraryBookIcon",
    text: "Asset Management",
    child: [
        {
            id: "MENU_ASSET_MANAGEMENT.ASSET",
            path: "/assets",
            isPublic: true,
            text: "Assets",
            child: []
        },
        {
            id: "MENU_ASSET_MANAGEMENT.ACCESSORIES",
            path: "/assets/accessories",
            isPublic: true,
            text: "Accessories",
            child: []
        },
        {
            id: "MENU_ASSET_MANAGEMENT.LICENSE",
            path: "/licenses",
            isPublic: true,
            text: "Licenses",
            child: []
        }
    ]
};
