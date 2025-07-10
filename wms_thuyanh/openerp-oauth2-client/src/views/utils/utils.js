const getWarehouseNameByWarehouseId = (id, warehouseGeneralList) => {
  for (var i = 0; i < warehouseGeneralList.length; i++) {
    if (warehouseGeneralList[i].warehouseId == id) {
      return warehouseGeneralList[i].name;
    }
  };
}

const getProductNameFromProductId = (id, productList) => {
  for (var i = 0; i < productList.length; i++) {
    if (productList[i].productId == id) {
      return productList[i].name;
    }
  }
}

const convertTimeStampToDate = ( time ) => {
  if (time == null) {
    return "";
  }
  const date = new Date(time.slice(0, time.indexOf("T")));
  return date.getDate() + "-" + date.getMonth() + "-" + date.getFullYear();
}

const getCurrentDateInString = () => {
  const now = new Date();
  return `${now.getDate()}-${now.getMonth() + 1}-${now.getFullYear()} ${now.getHours()}:${now.getMinutes()}:${now.getSeconds()}`;
}

const getCurrentDateInString2 = () => {
  const now = new Date();
  const day = String(now.getDate()).padStart(2, '0');
  const month = String(now.getMonth() + 1).padStart(2, '0'); // Months are zero-based
  const year = now.getFullYear();
  return `${day}-${month}-${year}`;
}

const convertDateToRequestFormat = ( date ) => {
  return `${date.getFullYear()}`
}

const parseJwt = (token) => {
  if (!token) {
    return; 
  }
  const base64Url = token.split('.')[1];
  const base64 = base64Url.replace('-', '+').replace('_', '/');
  return JSON.parse(window.atob(base64));
}

export function setCanvasSize(size, scale) {
  return (size * scale);
}

const convertToVNDFormat = ( valueInt ) => {
  var result;
  try {
    return result = valueInt.toLocaleString('it-IT', {style : 'currency', currency : 'VND'})
  } catch (e) {
    console.log("Exception in convert to vnd format: ", e);
  }
  return '0';
}


export { getCurrentDateInString,
  convertToVNDFormat, 
  getWarehouseNameByWarehouseId, 
  getProductNameFromProductId, 
  convertTimeStampToDate,
  getCurrentDateInString2 };