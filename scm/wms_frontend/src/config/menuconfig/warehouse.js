export const warehouse = {
  id: "MENU_WMS_WAREHOUSE",
  icon: "WarehouseIcon",
  text: "Warehouse",
  child: [
    {
      id: "MENU_WMS_WAREHOUSE.INVENTORY",
      path: "/warehouse/inventory",
      isPublic: false,
      text: "Inventory",
      child: [],
    },
    {
      id: "MENU_WMS_WAREHOUSE.IMPORTING",
      path: "/warehouse/importing",
      isPublic: false,
      text: "Importing",
      child: [],
    },
    {
      id: "MENU_WMS_WAREHOUSE.EXPORTING",
      path: "/warehouse/exporting",
      isPublic: false,
      text: "Exporting",
      child: [],
    },
  ],
};
