export const customer = {
  id: "MENU_WMSv2_CUSTOMER",
  icon: "CustomerIcon",
  text: "E-commerce",
  child: [
    {
      id: "MENU_WMSv2_CUSTOMER.PRODUCTS",
      path: "/customer/products",
      isPublic: true,
      text: "Online Shopping",
      child: [],
    },
    {
      id: "MENU_WMSv2_CUSTOMER.CART",
      path: "/customer/cart",
      isPublic: true,
      text: "Cart",
      child: [],
    },
    {
      id: "MENU_WMSv2_CUSTOMER.HISTORY",
      path: "/customer/order_history",
      isPublic: true,
      text: "My Orders",
      child: [],
    },
  ],
};
