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

export function setCanvasSize(size, scale) {
  return (size * scale);
}

const convertToVNDFormat = ( valueInt ) => {
  // const formatter = new Intl.NumberFormat('vn-VN', {
  //   style: 'currency',
  //   currency: 'VND',
  // });
  // return formatter.format(valueInt).substring(1);
  var result;
  try {
    return result = valueInt.toLocaleString('it-IT', {style : 'currency', currency : 'VND'})
  } catch (e) {
    console.log("Exception in convert to vnd format: ", e);
  }
  return '0';
}


export { convertToVNDFormat, 
  getWarehouseNameByWarehouseId, 
  getProductNameFromProductId, 
  convertTimeStampToDate };