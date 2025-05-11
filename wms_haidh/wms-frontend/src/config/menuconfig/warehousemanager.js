export const warehousemanager = {
  id: "MENU_WMSv2_ADMIN",
  icon: "AdminIcon",
  text: "Inventory Management",
  child: [
    {
      id: "MENU_WMSv2_ADMIN.WAREHOUSE",
      path: "/admin/warehouse",
      isPublic: false,
      text: "Warehouses",
      child: [],
    },
    {
      id: "MENU_WMSv2_ADMIN.PRODUCT",
      path: "/admin/product",
      isPublic: false,
      icon: "StarBorder",
      text: "Products",
      child: [],
    },
    {
      id: "MENU_WMSv2_ADMIN.INVENTORY",
      path: "/admin/inventory",
      isPublic: false,
      text: "Inventory",
      child: [],
    },
    {
      id: "MENU_WMSv2_ADMIN.PROCESS_RECEIPT",
      path: "/admin/receipts",
      isPublic: false,
      icon: "StarBorder",
      text: "Purchase Orders",
      child: [],
    },
    {
      id: "MENU_WMSv2_ADMIN.ORDER",
      path: "/admin/orders",
      isPublic: false,
      icon: "StarBorder",
      text: "Sale Orders",
      child: [],
    }
  ],
};
