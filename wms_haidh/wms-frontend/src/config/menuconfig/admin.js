export const admin = {
  id: "MENU_WMSv2_ADMIN",
  icon: "AdminIcon",
  text: "Inventory Management",
  child: [
    {
      id: "MENU_WMSv2_ADMIN.WAREHOUSE",
      path: "/admin/warehouse",
      isPublic: true,
      text: "Warehouse",
      child: [],
    },
    {
      id: "MENU_WMSv2_ADMIN.PRODUCT",
      path: "/admin/product",
      isPublic: true,
      icon: "StarBorder",
      text: "Product",
      child: [],
    },
    {
      id: "MENU_WMSv2_ADMIN.ORDER",
      path: "/admin/orders",
      isPublic: true,
      icon: "StarBorder",
      text: "Delivery Order",
      child: [],
    },
    {
      id: "MENU_WMSv2_ADMIN.PROCESS_RECEIPT",
      path: "/admin/process-receipts",
      isPublic: true,
      icon: "StarBorder",
      text: "Process Receipt",
      child: [],
    },
  ],
};
