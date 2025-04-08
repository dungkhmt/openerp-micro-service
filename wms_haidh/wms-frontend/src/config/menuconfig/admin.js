export const admin = {
  id: "MENU_WMSv2_ADMIN",
  icon: "AdminIcon",
  text: "Inventory Management",
  child: [
    {
      id: "MENU_WMSv2_ADMIN.WAREHOUSE",
      path: "/admin/warehouse",
      isPublic: true,
      text: "Warehouses",
      child: [],
    },
    {
      id: "MENU_WMSv2_ADMIN.PRODUCT",
      path: "/admin/product",
      isPublic: true,
      icon: "StarBorder",
      text: "Products",
      child: [],
    },
    {
      id: "MENU_WMSv2_ADMIN.INVENTORY",
      path: "/admin/inventory",
      isPublic: true,
      text: "Inventory",
      child: [],
    },
    {
      id: "MENU_WMSv2_ADMIN.PROCESS_RECEIPT",
      path: "/admin/receipts",
      isPublic: true,
      icon: "StarBorder",
      text: "Purchase Orders",
      child: [],
    },
    {
      id: "MENU_WMSv2_ADMIN.ORDER",
      path: "/admin/orders",
      isPublic: true,
      icon: "StarBorder",
      text: "Sale Orders",
      child: [],
    }
  ],
};
