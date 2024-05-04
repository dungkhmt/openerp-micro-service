export const validateImageFile = (fileType) => {
  const fileStart = fileType.split("/")[0];

  if (fileStart === "image") return true;

  return false;
};
