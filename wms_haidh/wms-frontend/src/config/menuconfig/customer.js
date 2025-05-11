export const customer = {
  id: "MENU_WMSv2_CUSTOMER",
  icon: "CustomerIcon",
  text: "Order Management",
  child: [
    {
      id: "MENU_WMSv2_CUSTOMER.PRODUCTS",
      path: "/customer/products",
      isPublic: false,
      text: "Products",
      child: [],
    },
    {
      id: "MENU_WMSv2_CUSTOMER.CART",
      path: "/customer/cart",
      isPublic: false,
      text: "Cart",
      child: [],
    },
    {
      id: "MENU_WMSv2_CUSTOMER.HISTORY",
      path: "/customer/order-history",
      isPublic: false,
      text: "My Orders",
      child: [],
    },
  ],
};
