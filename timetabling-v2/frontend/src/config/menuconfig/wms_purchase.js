export const wms_purchase = {
    id: "MENU_WMS_PURCHASE",
    icon: "InfoIcon",
    text: "Mua hàng",
    child: [
      {
        id: "MENU_WMS_PURCHASE.PURCHASE_ORDER",
        path: "/wms/purchase/orders",
        isPublic: false,
        text: "Đơn hàng mua",
        child: [],
      },
      {
        id: "MENU_WMS_PURCHASE.SUPPLIERS",
        path: "/wms/purchase/suppliers",
        isPublic: false,
        text: "Nhà cung cấp",
        child: [],
      },
      {
        id: "MENU_WMS_PURCHASE.DASHBOARD",
        path: "/wms/purchase/dashboard",
        isPublic: false,
        text: "Dashboard",
        child: [],
      }
    ],
  };
  