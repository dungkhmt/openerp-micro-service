export const customer = {
  id: "MENU_WMSv2_CUSTOMER",
  icon: "CustomerIcon",
  text: "Order Management",
  child: [
    {
      id: "MENU_WMSv2_CUSTOMER.PRODUCTS",
      path: "/customer/products",
      isPublic: true,
      text: "Products",
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
      path: "/customer/order-history",
      isPublic: true,
      text: "My Orders",
      child: [],
    },
  ],
};
