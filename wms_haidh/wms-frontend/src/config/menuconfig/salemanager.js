export const salemanager = {
  id: "MENU_WMSv2_SALE_MANAGER",
  icon: "SaleManagerIcon",
  text: "Sales Management",
  child: [
    {
      id: "MENU_WMSv2_SALE_MANAGER.PRICE_CONFIG",
      path: "/sale-manager/price-config",
      isPublic: false,
      text: "Price Configuration",
      child: [],
    },
    {
      id: "MENU_WMSv2_SALE_MANAGER.ORDERS",
      path: "/sale-manager/sale-order",
      isPublic: false,
      text: "Sale Orders",
      child: [],
    },
  ],
};
