const columns = [
  { name: "NAME", uid: "name", sortable: true },
  { name: "CODE", uid: "code" },
  { name: "DATE UPDATED", uid: "dateUpdated"},
  { name: "ACTIONS", uid: "actions" },
];


const statusOptions = [
  {name: "Active", uid: "active"},
  {name: "Paused", uid: "paused"},
  {name: "Vacation", uid: "vacation"},
];
export { columns, statusOptions};
