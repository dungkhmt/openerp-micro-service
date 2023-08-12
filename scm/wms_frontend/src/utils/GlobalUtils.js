export const formatVietnameseCurrency = (amount) => {
  const formattedAmount = amount?.toLocaleString("vi-VN", {
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
export const generateRandomColor = () => {
  let maxVal = 0xffffff; // 16777215
  let randomNumber = Math.random() * maxVal;
  randomNumber = Math.floor(randomNumber);
  randomNumber = randomNumber.toString(16);
  let randColor = randomNumber.padStart(6, 0);
  return `#${randColor.toUpperCase()}`;
};
