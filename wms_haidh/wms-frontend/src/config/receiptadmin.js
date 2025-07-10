const columns = [
    { name: "RECEIPT NAME", uid: "receiptName" },
    { name: "WAREHOUSE", uid: "warehouseName" },
    { name: "EXPECTED RECEIPT DATE", uid: "expectedReceiptDate" },
    { name: "STATUS", uid: "status" },
    { name: "APPROVED BY", uid: "approvedBy" },
    { name: "ACTIONS", uid: "actions" }
  ];
  
  const statusOptions = [
    { name: "APPROVED", uid: "APPROVED" },
    { name: "IN PROGRESS", uid: "IN_PROGRESS" },
    { name: "ASSIGNED", uid: "ASSIGNED" },
    { name: "PUTAWAY COMPLETE", uid: "PUTAWAY_COMPLETE" },
  ];
  export { columns, statusOptions };