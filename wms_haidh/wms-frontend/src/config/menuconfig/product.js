export const product = {
  id: "MENU_PRODUCT",
  icon: "Product",
  text: "Product management",
  child: [
    {
      id: "MENU_PRODUCT.CATEGORY",
      path: "/product/category",
      isPublic: true,
      text: "Category",
      child: [],
    },
    {
      id: "MENU_PRODUCT.PRODUCT",
      path: "/product/all",
      isPublic: true,
      text: "Product",
      child: [],
    },
    {
      id: "MENU_PRODUCT.PRICE",
      path: "/product/price",
      isPublic: true,
      text: "Price",
      child: [],
    }
  ],
};
