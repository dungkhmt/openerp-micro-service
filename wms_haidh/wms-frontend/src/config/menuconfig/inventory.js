export const inventory = {
  id: "MENU_INVENTORY",
  icon: "Inventory",
  text: "Inventory management",
  child: [
    {
      id: "MENU_INVENTORY.INVENTORY",
      path: "/inventory/item",
      isPublic: true,
      text: "Inventory item",
      child: [],
    },
    {
      id: "MENU_INVENTORY.WAREHOUSE",
      path: "/inventory/warehouse",
      isPublic: true,
      text: "Warehouse management",
      child: [],
    },
    {
      id: "MENU_INVENTORY.BAY",
      path: "/inventory/bay",
      isPublic: true,
      text: "Bay management",
      child: [],
    }
  ],
};
