import { MenuItem, Select } from "@mui/material"

export const ProductDropDown = ({ productList, setSelectedProduct }) => {
  return <Select onChange={(e) => setSelectedProduct(e.target.value)}>
    {
      productList.length > 0 &&
      productList.map(product => 
        <MenuItem value={product.productId}>{product.name}</MenuItem>)
    }
  </Select>
}

export const WarehouseDropDown = ({ warehouseList, setSelectedWarehouseId, setSelectedWarehouseName }) => {
  return <Select onChange={(e, v) => {
        setSelectedWarehouseId(e.target.value);
        setSelectedWarehouseName(v?.props?.children);
      }}>
    {
      warehouseList.length > 0 &&
      warehouseList.map(warehouse => 
        <MenuItem value={warehouse.id}>{warehouse.name}</MenuItem>)
    }
  </Select>
}

export const BayDropDown = ({ selectedWarehouse, setSelectedBayId, setSelectedBayCode }) => {
  return <Select onChange={(e, v) => {
      setSelectedBayId(e.target.value); 
      setSelectedBayCode(v?.props?.children);
      }}>
    {
      selectedWarehouse?.listShelf?.length > 0 &&
      selectedWarehouse?.listShelf?.map(bay => 
        <MenuItem value={bay.id}>{bay.code}</MenuItem>)
    }
  </Select>
}
