const columns = [
    { name: "PRODUCT NAME", uid: "productName" },
    { name: "LOT ID", uid: "lotId" },
    { name: "QUANTITY ON HAND", uid: "quantityOnHandTotal" },
    { name: "LAST UPDATED", uid: "lastUpdatedStamp" },
  ];
  
  const statusOptions = [
    { name: "APPROVED", uid: "APPROVED" },
    { name: "IN PROGRESS", uid: "IN_PROGRESS" },
    { name: "COMPLETED", uid: "COMPLETED" },
  ];
  export { columns, statusOptions };