const columns = [
  { name: "RECEIPT NAME", uid: "receiptName" },
  { name: "CREATED REASON", uid: "createdReason" },
  { name: "DESCRIPTION", uid: "description" },
  { name: "EXPECTED RECEIPT DATE", uid: "expectedReceiptDate" },
  { name: "STATUS", uid: "status" },
  { name: "CREATED BY", uid: "createdBy" },
  { name: "ACTIONS", uid: "actions" }
];

const statusOptions = [
  { name: "CREATED", uid: "CREATED" },
  { name: "CANCELLED", uid: "CANCELLED" },
  { name: "APPROVED", uid: "APPROVED" },
  { name: "IN PROGRESS", uid: "IN_PROGRESS" },
  { name: "COMPLETED", uid: "COMPLETED" },
];
export { columns, statusOptions };