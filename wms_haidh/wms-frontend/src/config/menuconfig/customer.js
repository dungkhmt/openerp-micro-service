export const customerProducts = {
  id: "MENU_WMSv2_CUSTOMER",
  icon: "CustomerProductIcon",
  text: "Orders",
  child: [
    {
      id: "MENU_WMSv2_CUSTOMER.PRODUCTS",
      path: "/customer/products",
      isPublic: false,
      text: "Products",
      child: []
    }
  ],
};
export const customerCart = {
  id: "MENU_WMSv2_CUSTOMER",
  icon: "CartIcon",
  text: "Orders",
  child: [
    {
      id: "MENU_WMSv2_CUSTOMER.CART",
      path: "/customer/cart",
      isPublic: false,
      text: "Cart",
      child: []
    }
  ],
};
export const customerHistory = {
  id: "MENU_WMSv2_CUSTOMER",
  icon: "HistoryIcon",
  text: "Orders",
  child: [
    {
      id: "MENU_WMSv2_CUSTOMER.HISTORY",
      path: "/customer/order-history",
      isPublic: false,
      text: "History",
      child: []
    },
  ],
};
