const columns = [
  { name: "ORDER DATE", uid: "orderDate" },
  { name: "TOTAL ORDER COST", uid: "totalOrderCost" },
  { name: "CUSTOMER NAME", uid: "customerName" },
  { name: "STATUS", uid: "status" },
  { name: "APPROVED BY", uid: "approvedBy" },
  { name: "ACTIONS", uid: "actions" }
];
  
  const statusOptions = [
    { name: "APPROVED", uid: "APPROVED" },
    { name: "DELIVERING_A_PART", uid: "DELIVERING_A_PART" },
    { name: "DISTRIBUTED", uid: "DISTRIBUTED" },
    { name: "COMPLETED", uid: "COMPLETED" },
  ];
  export { columns, statusOptions };