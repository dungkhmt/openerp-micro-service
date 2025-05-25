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
    { name: "IN PROGRESS", uid: "IN_PROGRESS" },
    { name: "ASSIGNED", uid: "ASSIGNED" },
    { name: "PICK COMPLETE", uid: "PICK_COMPLETE" },
  ];
  export { columns, statusOptions };