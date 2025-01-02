const columns = [
  { name: "RECEIPT NAME", uid: "receiptName" },
  { name: "PRODUCT NAME", uid: "productName" },
  { name: "QUANTITY", uid: "quantity" },
  { name: "WAREHOUSE NAME", uid: "warehouseName" },
  { name: "EXPECTED RECEIPT DATE", uid: "expectedReceiptDate" },
  { name: "COMPLETED", uid: "completed" },
  { name: "ACTIONS", uid: "actions" }
];

const statusOptions = [
  { name: "APPROVED", uid: "APPROVED" },
  { name: "IN PROGRESS", uid: "IN_PROGRESS" },
  { name: "COMPLETED", uid: "COMPLETED" },
];
export { columns, statusOptions };