const columns = [
    { name: "DELIVERY TRIP ID", uid: "deliveryTripId" },
    { name: "DEPARTURE WAREHOUSE", uid: "warehouseName" },
    { name: "DISTANCE (M)", uid: "distance" },
    { name: "TOTAL LOCATIONS", uid: "totalLocations" },
    { name: "STATUS", uid: "status" },
    { name: "ACTIONS", uid: "actions" }
  ];
  
  const statusOptions = [
    { name: "CREATED", uid: "CREATED" },
    { name: "STARTED", uid: "STARTED" },
    { name: "DONE", uid: "DONE" },
  ];
  export { columns, statusOptions };