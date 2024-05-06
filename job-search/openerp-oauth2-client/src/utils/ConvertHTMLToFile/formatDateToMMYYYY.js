export const formatDateToMMYYYY = (dateString) => {
  return new Date(dateString).toLocaleString("en-US", {
    month: "2-digit",
    year: "numeric",
  });
};
