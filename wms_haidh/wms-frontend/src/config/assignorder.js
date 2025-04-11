const columns = [
  { name: "ORDER DATE", uid: "orderDate" },
  { name: "CUSTOMER NAME", uid: "customerName" },
  { name: "TOTAL ORDER COST", uid: "totalOrderCost" },
  { name: "STATUS", uid: "status" },
  { name: "APPROVED BY", uid: "approvedBy" },
  { name: "ACTIONS", uid: "actions" }
];
  
  const statusOptions = [
    { name: "APPROVED", uid: "APPROVED" },
    { name: "DELIVERING", uid: "DELIVERING" },
    { name: "COMPLETED", uid: "COMPLETED" },
  ];
  export { columns, statusOptions };