const columns = [
    { name: "FROM LOCATION", uid: "fromLocationName" },
    { name: "TO LOCATION", uid: "toLocationName" },
    { name: "DISTANCE (M)", uid: "distance" },
    { name: "ACTIONS", uid: "actions" },
];


const statusOptions = [
    { key: "whc", name: "WAREHOUSE - CUSTOMER", fromType: "WAREHOUSE", toType: "CUSTOMER" },
    { key: "cwh", name: "CUSTOMER - WAREHOUSE", fromType: "CUSTOMER", toType: "WAREHOUSE" },
    { key: "cc", name: "CUSTOMER - CUSTOMER", fromType: "CUSTOMER", toType: "CUSTOMER" }
];

export { columns, statusOptions };