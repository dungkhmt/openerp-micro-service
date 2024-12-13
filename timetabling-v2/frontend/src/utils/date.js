export const convertToDate = (date_time) => {
  const dateString = date_time;
  const dateObj = new Date(dateString);
  const options = { month: "2-digit", day: "2-digit", year: "numeric" };
  return dateObj.toLocaleDateString("en-US", options);
};
