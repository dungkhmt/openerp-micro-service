export const customer = {
  id: "MENU_WMSv2_CUSTOMER",
  icon: "DashboardIcon",
  text: "Thương mại điện tử",
  child: [
    {
      id: "MENU_WMSv2_CUSTOMER.PRODUCTS",
      path: "/customer/products",
      isPublic: true,
      text: "Mua hàng online",
      child: [],
    },
    {
      id: "MENU_WMSv2_CUSTOMER.CART",
      path: "/customer/cart",
      isPublic: true,
      text: "Giỏ hàng",
      child: [],
    },
  ],
};
