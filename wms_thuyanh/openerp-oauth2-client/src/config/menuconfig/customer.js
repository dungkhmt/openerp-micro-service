export const customer = {
  id: "MENU_WMS_CUSTOMER",
  icon: "DashboardIcon",
  text: "Thương mại điện tử",
  child: [
    {
      id: "MENU_WMS_CUSTOMER.PRODUCTS",
      path: "/customer/products",
      isPublic: false,
      text: "Mua hàng online",
      child: [],
    },
    {
      id: "MENU_WMS_CUSTOMER.CART",
      path: "/customer/cart",
      isPublic: false,
      text: "Giỏ hàng",
      child: [],
    },
    {
      id: "MENU_WMS_CUSTOMER.HISTORY",
      path: "/customer/order_history",
      isPublic: false,
      text: "Đơn hàng của tôi",
      child: [],
    },
  ],
};
