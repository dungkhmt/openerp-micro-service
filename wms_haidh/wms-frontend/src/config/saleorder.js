const columns = [
    { name: "ORDER DATE", uid: "orderDate" },
    { name: "CUSTOMER NAME", uid: "customerName" },
    { name: "TOTAL ORDER COST", uid: "totalOrderCost" },
    { name: "STATUS", uid: "status" },
    { name: "APPROVED BY", uid: "approvedBy" },
    { name: "CANCELLED BY", uid: "cancelledBy" },
    { name: "ACTIONS", uid: "actions" }
  ];
  
  const statusOptions = [
    { name: "CREATED", uid: "CREATED" },
    { name: "CANCELLED", uid: "CANCELLED" },
    { name: "APPROVED", uid: "APPROVED" }
  ];
  export { columns, statusOptions };