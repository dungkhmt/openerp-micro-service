export const getMenuStyles = (menuOpened) => {
  if (document.documentElement.clientWidth <= 800) {
    return { right: !menuOpened && "-100%" };
  }
};

export const sliderSettings = {
  slidesPerView: 1,
  spaceBetween: 50,
  breakpoints: {
    480: {
      slidesPerView: 1,
    },
    600: {
      slidesPerView: 2,
    },
    750: {
      slidesPerView: 3,
    },
    1100: {
      slidesPerView: 4,
    },
  },
};

export const updateFavourites = (id, favourites) => {
  if (favourites.includes(id)) {
    return favourites.filter((resId) => resId !== id);
  } else {
    return [...favourites, id];
  }
};

export const checkFavourites = (id, favourites) => {
  return favourites?.includes(id) ? "#fa3e5f" : "white";
};

export const validateString = (value) => {
  return value?.length < 3 || value === null
    ? "Must have atleast 3 characters"
    : null;
};

export const transferLegalDocument = (value) => {
  switch (value) {
    case "Have": return "Đã có sổ đỏ";
    case "WAIT": return "Đang chờ sổ đỏ";
    default: return "Không có sổ đỏ"
  }
}

export const transferLegalDocuments = (listValue) => {
  let result = [];
  for (const value in listValue) {
    result = [...result, transferLegalDocument(value)];
  }
  return result;
}

export const transferDirection = (value) => {
  switch (value) {
    case "NORTH":
      return "Bắc";
    case "SOUTH":
      return "Nam";
    case "WEST":
      return "Tây";
    case "EAST":
      return "Đông";
    case "EAST_NORTH":
      return "Đông Bắc";
    case "EAST_SOUTH":
      return "Đông Nam";
    case "WEST_SOUTH":
      return "Tây Nam";
    case "WEST_NORTH":
      return "Tây Bắc";
    default:
      return "Hướng không xác định";
  }
}


export const transferDirections = (listValue) => {
  let result = [];
  for (const value in listValue) {
    result = [...result, transferDirection(value)];
  }
  return result;
}

export const transferTypeProperty = (value) => {
  switch (value) {
    case "LAND": return "Đất";
    case "HOUSE": return "Nhà ở";
    default: return "Chung cư";
  }
}

export const transferTypeProperties = (listValue) => {
  let result = [];
  for (const value in listValue) {
    result = [...result, transferTypeProperty(value)];
  }
  return result;
}