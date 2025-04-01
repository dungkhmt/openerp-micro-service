export const wms_sales = {
    id: "MENU_WMS_SALES",
    icon: "InfoIcon",
    text: "Bán hàng",
    child: [
      {
        id: "MENU_WMS_SALES.SALES_ORDER",
        path: "/wms/sales/orders",
        isPublic: false,
        text: "Đơn hàng bán",
        child: [],
      },
      {
        id: "MENU_WMS_SALES.CUSTOMERS",
        path: "/wms/sales/customers",
        isPublic: false,
        text: "Khách hàng",
        child: [],
      },
      {
        id: "MENU_WMS_SALES.DASHBOARD",
        path: "/wms/sales/dashboard",
        isPublic: false,
        text: "Dashboard",
        child: [],
      }
    ],
  };
  