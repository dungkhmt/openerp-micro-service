export const formatVietnameseCurrency = (amount) => {
  const formattedAmount = amount.toLocaleString("vi-VN", {
    style: "currency",
    currency: "VND",
  });
  return formattedAmount;
};
export const convertUserToName = (user) => {
  if (user === null || user === undefined) return "";
  return (
    (user?.firstName || "") +
    " " +
    (user?.middleName || "") +
    " " +
    (user?.lastName || "")
  );
};
