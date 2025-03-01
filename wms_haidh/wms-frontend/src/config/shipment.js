const columns = [
    { name: "SHIPMENT ID", uid: "shipmentId", sortable: true },
    { name: "EXPECTED DELIVERY TIME", uid: "expectedDeliveryStamp", sortable: true },
    { name: "CREATED BY", uid: "createdBy", sortable: true },
    { name: "ACTIONS", uid: "actions" },
  ];
  
  
  const statusOptions = [
    {name: "Active", uid: "active"},
    {name: "Paused", uid: "paused"},
    {name: "Vacation", uid: "vacation"},
  ];
  export { columns, statusOptions};