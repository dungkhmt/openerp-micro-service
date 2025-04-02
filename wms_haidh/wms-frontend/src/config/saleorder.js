const columns = [
    { name: "ORDER DATE", uid: "orderDate" },
    { name: "TOTAL ORDER COST", uid: "totalOrderCost" },
    { name: "CUSTOMER NAME", uid: "customerName" },
    { name: "STATUS", uid: "status" },
    { name: "APPROVED BY", uid: "approvedBy" },
    { name: "CANCELLED BY", uid: "cancelledBy" },
    { name: "ACTIONS", uid: "actions" }
  ];
  
  const statusOptions = [
    { name: "CREATED", uid: "CREATED" },
    { name: "CANCELLED", uid: "CANCELLED" },
    { name: "APPROVED", uid: "APPROVED" },
    { name: "DELIVERING", uid: "DELIVERING" },
    { name: "COMPLETED", uid: "COMPLETED" }
  ];
  export { columns, statusOptions };