const columns = [
    { name: "DELIVERY TRIP ID", uid: "deliveryTripId" },
    { name: "DELIVERY PERSON", uid: "deliveryPersonName" },
    { name: "DISTANCE", uid: "distance" },
    { name: "TOTAL LOCATIONS", uid: "totalLocations" },
    { name: "STATUS", uid: "status" },
    { name: "DESCRIPTION", uid: "description" },
    { name: "ACTIONS", uid: "actions" }
  ];
  
  const statusOptions = [
    { name: "CREATED", uid: "CREATED" },
    { name: "DELIVERING", uid: "DELIVERING" },
    { name: "DONE", uid: "DONE" },
  ];
  export { columns, statusOptions };