export const formatVietnameseCurrency = (amount) => {
  const formattedAmount = amount.toLocaleString("vi-VN", {
    style: "currency",
    currency: "VND",
  });
  return formattedAmount;
};
export const convertUserToName = (user) => {
  return user?.firstName + " " + user?.middleName + " " + user?.lastName;
};
