const columns = [
    { name: "ORDER DATE", uid: "orderDate" },
    { name: "CUSTOMER NAME", uid: "customerName" },
    { name: "PRODUCT NAME", uid: "productName" },
    { name: "QUANTITY", uid: "quantity" },
    { name: "PRICE UNIT", uid: "priceUnit" },
    { name: "COMPLETED", uid: "completed" },
    { name: "ACTIONS", uid: "actions" }
  ];
  
  const statusOptions = [
    { name: "APPROVED", uid: "APPROVED" },
    { name: "DELIVERING_A_PART", uid: "DELIVERING_A_PART" },
    { name: "DISTRIBUTED", uid: "DISTRIBUTED" },
    { name: "COMPLETED", uid: "COMPLETED" },
  ];
  export { columns, statusOptions };