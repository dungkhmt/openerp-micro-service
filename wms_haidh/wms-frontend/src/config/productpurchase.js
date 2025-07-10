const columns = [
    { name: "NAME", uid: "name", sortable: true },
    { name: "CODE", uid: "code" },
    { name: "AVAILABLE QUANTITY", uid: "totalQuantityOnHand", sortable: true },
    { name: "DATE UPDATED", uid: "dateUpdated"}
  ];
  
  
  const statusOptions = [
    {name: "Active", uid: "active"},
    {name: "Paused", uid: "paused"},
    {name: "Vacation", uid: "vacation"},
  ];
  export { columns, statusOptions};