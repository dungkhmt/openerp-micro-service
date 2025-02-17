const columns = [
    { name: "RECEIPT NAME", uid: "receiptName" },
    { name: "CREATED REASON", uid: "createdReason" },
    { name: "DESCRIPTION", uid: "description" },
    { name: "EXPECTED RECEIPT DATE", uid: "expectedReceiptDate" },
    { name: "STATUS", uid: "status" },
    { name: "APPROVED BY", uid: "approvedBy" },
    { name: "ACTIONS", uid: "actions" }
  ];
  
  const statusOptions = [
    { name: "APPROVED", uid: "APPROVED" },
    { name: "IN PROGRESS", uid: "IN_PROGRESS" },
    { name: "COMPLETED", uid: "COMPLETED" },
  ];
  export { columns, statusOptions };