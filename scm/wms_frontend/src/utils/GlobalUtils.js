export const formatVietnameseCurrency = (amount) => {
  const formattedAmount = amount.toLocaleString("vi-VN", {
    style: "currency",
    currency: "VND",
  });
  return formattedAmount;
};
