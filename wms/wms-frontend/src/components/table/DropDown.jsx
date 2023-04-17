import { MenuItem, Select } from "@mui/material"

export const ProductDropDown = ({ productList, setSelectedProductId, setSelectedProductName }) => {
  return <Select onChange={(e, v) => {
        setSelectedProductId(e.target.value);
        setSelectedProductName(v?.props.children);
      }} defaultValue={""}>
    {
      productList.length > 0 &&
      productList.map(product => 
        <MenuItem key={product.productId} value={product.productId}>{product.productName}</MenuItem>)
    }
  </Select>
}

export const WarehouseDropDown = ({ warehouseList, setSelectedWarehouseId, setSelectedWarehouseName }) => {
  return <Select onChange={(e, v) => {
        setSelectedWarehouseId(e.target.value);
        setSelectedWarehouseName(v?.props?.children);
      }} defaultValue={""}>
    {
      warehouseList.length > 0 &&
      warehouseList.map(warehouse => 
        <MenuItem key={warehouse.id} value={warehouse.id}>{warehouse.name}</MenuItem>)
    }
  </Select>
}

export const BayDropDown = ({ selectedWarehouse, setSelectedBayId, setSelectedBayCode }) => {
  return <Select onChange={(e, v) => {
      setSelectedBayId(e.target.value); 
      setSelectedBayCode(v?.props?.children);
      }} defaultValue={""}>
    {
      selectedWarehouse?.listShelf?.length > 0 &&
      selectedWarehouse?.listShelf?.map(bay => 
        <MenuItem key={bay.id} value={bay.id}>{bay.code}</MenuItem>)
    }
  </Select>
}
