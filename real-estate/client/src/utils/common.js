import {storage} from "../components/UploadImage/FireBaseConfig";
import {deleteObject, getDownloadURL, ref, uploadBytesResumable} from "firebase/storage";
import {v4} from "uuid";

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
    case "HAVE": return "Đã có sổ đỏ";
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

export const transferPrice = (price) => {
  if (price >= 1000000000) {
    return (price / 1000000000).toFixed(1) + " tỷ";
  }
  if (price >= 1000000) {
    return (price / 1000000).toFixed(1) + " triệu";
  }
  if (price === 0) {
    return "0 VND"
  }
}

export const transferTime = (timestamp) => {
  const now = Date.now();
  const diff = now - timestamp; // khoảng cách thời gian tính bằng mili giây

  const seconds = Math.floor(diff / 1000);
  const minutes = Math.floor(seconds / 60);
  const hours = Math.floor(minutes / 60);
  const days = Math.floor(hours / 24);
  const weeks = Math.floor(days / 7);
  const months = Math.floor(weeks/4)
  if (months > 0) {
    return `Đăng ${months} tháng trước`;
  } else if (weeks > 0) {
    return `Đăng ${weeks} tuần trước`;
  } else if (days > 0) {
    return `Đăng ${days} ngày trước`;
  } else if (hours > 0) {
    return `Đăng ${hours} giờ trước`;
  } else if (minutes > 0) {
    return `Đăng ${minutes} phút trước`;
  } else {
    return `Đăng ${seconds} giây trước`;
  }
}

export const uploadImage = (image) => {
  return new Promise((resolve, reject) => {
    const storageRef = ref(storage, `images/${v4()}`);
    const uploadTask = uploadBytesResumable(storageRef, image);
    uploadTask.on(
        'state_changed',
        (snapshot) => {
          // Xử lý tiến trình tải lên
        },
        (error) => {
          // Xử lý lỗi
          console.log(error);
          reject(error);
        },
        () => {
          // Xử lý khi tải lên thành công
          getDownloadURL(uploadTask.snapshot.ref)
              .then((url) => {
                console.log(url);
                resolve(url);
              })
              .catch(error => reject(error));
        }
    );
  });
}

export const handleDeleteImage = async (image) => {
  try {
    const imageRef = ref(storage, image);
    await deleteObject(imageRef);
    console.log("Ảnh đã được xóa thành công!");
  } catch (error) {
    console.log("Lỗi khi xóa ảnh:", error);
  }
}

export const capitalizeFirstLetterOfEachWord = (string) => {
  return string?.split(' ').map(word => {
    return word.charAt(0).toUpperCase() + word.slice(1);
  }).join(' ');
}

export const transferPostStatus = (status) => {
  switch (status) {
    case "OPENING": return "Đang Mở";
    case "CLOSE": return "Đã Đóng";
    default: return "Đã Chốt";
  }
}

export const transferColorPostStatus = (status) => {
  switch (status) {
    case "OPENING":
      return "rgb(227, 170, 73)";
    case "CLOSE":
      return "rgb(224, 60, 49)";
    default:
      return "rgb(0, 155, 161)";
  }
}

export const transferTimeToDate = (timestamp) => {
  const date = new Date(timestamp);

  const month = String(date.getMonth() + 1).padStart(2, '0'); // Months are zero-based
  const day = String(date.getDate()).padStart(2, '0');

  const formattedDate = `${month}/${day}`;
  return formattedDate;
}