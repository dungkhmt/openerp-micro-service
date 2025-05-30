export const salemanagerPriceConfig = {
  id: "MENU_WMSv2_SALE_MANAGER.PRICE_CONFIG",
  icon: "MoneyIcon",
  text: "Price Configuration",
  child: [
    {
      id: "MENU_WMSv2_SALE_MANAGER.PRICE_CONFIG",
      path: "/sale-manager/price-config",
      isPublic: false,
      text: "Price Configuration",
      child: [],
    }
  ],
};
export const salemanagerOrders = {
  id: "MENU_WMSv2_SALE_MANAGER.ORDERS",
  icon: "SaleOrderIcon",
  text: "Process Sales Orders",
  child: [
    {
      id: "MENU_WMSv2_SALE_MANAGER.ORDERS",
      path: "/sale-manager/sale-order",
      isPublic: false,
      text: "Sales Orders",
      child: []
    },
  ],
};
