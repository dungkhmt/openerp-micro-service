export const GeneralManagement = {
    id: "MENU_GENERAL_MANAGEMENT",
    icon: "LibraryBookIcon",
    text: "My Asset",
    child: [
        {
            id: "MENU_GENERAL_MANAGEMENT.ASSET",
            path: "/",
            isPublic: true,
            text: "My Asset",
            child: []
        },
        {
            id: "MENU_GENERAL_MANAGEMENT.REQUEST",
            path: "/my-request",
            isPublic: true,
            text: "My Request",
            child: []
        },
    ]
};
