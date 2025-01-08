const columns = [
    { name: "RECEIPT BILL ID", uid: "receiptBillId" },
    { name: "DESCRIPTION", uid: "description" },
    { name: "CREATED BY", uid: "createdBy" },
    { name: "TOTAL PRICE", uid: "totalPrice" },
    { name: "RECEIPT NAME", uid: "receiptName" }
  ];
  
  const statusOptions = [
    {name: "CREATED", uid: "CREATED"},
    {name: "CANCELLED", uid: "CANCELLED"},
    {name: "APPROVED", uid: "APPROVED"},
    {name: "IN PROGRESS", uid: "IN_PROGRESS"},
    {name: "COMPLETED", uid: "COMPLETED"},
  ];
  export { columns, statusOptions};