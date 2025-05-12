const columns = [
  { name: "RECEIPT NAME", uid: "receiptName" },
  { name: "WAREHOUSE", uid: "warehouseName" },
  { name: "EXPECTED RECEIPT DATE", uid: "expectedReceiptDate" },
  { name: "STATUS", uid: "status" },
  { name: "CREATED BY", uid: "createdBy" },
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