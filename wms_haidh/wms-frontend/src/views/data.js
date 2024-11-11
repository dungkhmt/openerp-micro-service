const columns = [
  { name: "NAME", uid: "name", sortable: true },
  { name: "CODE", uid: "code", sortable: true },
  { name: "TOTAL QUANTITY ON HAND", uid: "totalQuantityOnHand", sortable: true },
  { name: "ACTIONS", uid: "actions" },
];

const statusOptions = [
  {name: "Active", uid: "active"},
  {name: "Paused", uid: "paused"},
  {name: "Vacation", uid: "vacation"},
];

// const items =
//   [
//     { id: "1a2b3c4d-5678-1234-5678-abcdef123456", code: "ABC123", name: "Product A", inventoryQuantity: 25 },
//     { id: "2b3c4d5e-6789-2345-6789-bcdef2345678", code: "DEF456", name: "Product B", inventoryQuantity: 40 },
//     { id: "3c4d5e6f-7890-3456-7890-cdef34567890", code: "GHI789", name: "Product C", inventoryQuantity: 15 },
//     { id: "4d5e6f7g-8901-4567-8901-def456789012", code: "JKL012", name: "Product D", inventoryQuantity: 60 },
//     { id: "5e6f7g8h-9012-5678-9012-ef5678901234", code: "MNO345", name: "Product E", inventoryQuantity: 30 },
//     { id: "6f7g8h9i-0123-6789-0123-f67890123456", code: "PQR678", name: "Product F", inventoryQuantity: 20 },
//     { id: "7g8h9i0j-1234-7890-1234-g78901234567", code: "STU901", name: "Product G", inventoryQuantity: 50 },
//     { id: "8h9i0j1k-2345-8901-2345-h89012345678", code: "VWX234", name: "Product H", inventoryQuantity: 35 },
//     { id: "9i0j1k2l-3456-9012-3456-i90123456789", code: "YZA567", name: "Product I", inventoryQuantity: 22 },
//     { id: "0j1k2l3m-4567-0123-4567-j01234567890", code: "BCD890", name: "Product J", inventoryQuantity: 45 },
//     { id: "1k2l3m4n-5678-1234-5678-k12345678901", code: "EFG123", name: "Product K", inventoryQuantity: 18 },
//     { id: "2l3m4n5o-6789-2345-6789-l23456789012", code: "HIJ456", name: "Product L", inventoryQuantity: 10 },
//     { id: "3m4n5o6p-7890-3456-7890-m34567890123", code: "KLM789", name: "Product M", inventoryQuantity: 75 },
//     { id: "4n5o6p7q-8901-4567-8901-n45678901234", code: "NOP012", name: "Product N", inventoryQuantity: 5 },
//     { id: "5o6p7q8r-9012-5678-9012-o56789012345", code: "QRS345", name: "Product O", inventoryQuantity: 90 },
//     { id: "6p7q8r9s-0123-6789-0123-p67890123456", code: "TUV678", name: "Product P", inventoryQuantity: 42 },
//     { id: "7q8r9s0t-1234-7890-1234-q78901234567", code: "WXY901", name: "Product Q", inventoryQuantity: 28 },
//     { id: "8r9s0t1u-2345-8901-2345-r89012345678", code: "ZAB234", name: "Product R", inventoryQuantity: 33 },
//     { id: "9s0t1u2v-3456-9012-3456-s90123456789", code: "CDE567", name: "Product S", inventoryQuantity: 70 },
//     { id: "0t1u2v3w-4567-0123-4567-t01234567890", code: "FGH890", name: "Product T", inventoryQuantity: 55 }
//   ]


export { columns, statusOptions };