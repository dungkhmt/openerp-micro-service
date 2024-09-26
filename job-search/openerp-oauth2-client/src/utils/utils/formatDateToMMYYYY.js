export const formatDateToMMYYYY = (dateString: string) => {
  return new Date(dateString).toLocaleString("en-US", {
    month: "2-digit",
    year: "numeric",
  });
};
