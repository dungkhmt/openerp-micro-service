import React from "react";
import {
  Table,
  TableHeader,
  TableColumn,
  TableBody,
  TableRow,
  TableCell,
  Button,
  DropdownTrigger,
  Dropdown,
  DropdownMenu,
  DropdownItem,
  Pagination,
  Select, MenuItem
} from "@nextui-org/react";
import { VerticalDotsIcon } from "../../components/icon/VerticalDotsIcon";
import { columns } from "../../config/inventory";
import { useNavigate } from 'react-router-dom';
import { useState, useEffect, useMemo, useCallback } from "react";
import { request } from "../../api";
import { formatDate } from '../../utils/utils';

const INITIAL_VISIBLE_COLUMNS = ["productName", "lotId", "bayCode", "quantityOnHandTotal", "lastUpdatedStamp"];
export default function InventoryList() {

  const [page, setPage] = useState(1);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [pages, setPages] = useState(1);
  const [items, setItems] = useState([]);
  const [totalItems, setTotalItems] = useState(0);
  const [warehouseId, setWarehouseId] = useState("");
  const [bayId, setBayId] = useState("");
  const [warehouses, setWarehouses] = useState([]);
  const [bays, setBays] = useState([]);

  useEffect(() => {
    request("get", "/admin/warehouse", (res) => {
      setWarehouses(res.data);
    }).then();
  }, []);  
  
  useEffect(() => {
    if (warehouseId) {
      setBayId("");
      request("get", `/admin/bay?warehouseId=${warehouseId}`, (res) => {
        setBays(res.data);
      }).then();
    }
  }, [warehouseId]); 
  
  useEffect(() => {
    request("get", `/admin/inventory?warehouseId=${warehouseId}&bayId=${bayId}&page=${page - 1}&size=${rowsPerPage}`, (res) => {
      setItems(res.data.content);
      setTotalItems(res.data.totalElements);
      setPages(res.data.totalPages);
    }).then();
  }, [page, rowsPerPage, warehouseId, bayId]); 
  const [visibleColumns, setVisibleColumns] = useState(new Set(INITIAL_VISIBLE_COLUMNS));

  const headerColumns = useMemo(() => {
    if (visibleColumns === "all") return columns;

    return columns.filter((column) => Array.from(visibleColumns).includes(column.uid));
  }, [visibleColumns]);

  const renderCell = useCallback((item, columnKey) => {
    const cellValue = item[columnKey];

    switch (columnKey) {
      case "actions":
        return (
          <div className="relative flex justify-end items-center gap-2">
            <Dropdown className="bg-background border-1 border-default-200">
              <DropdownTrigger>
                <Button isIconOnly radius="full" size="sm" variant="light">
                  <VerticalDotsIcon className="text-default-400" />
                </Button>
              </DropdownTrigger>
              <DropdownMenu>
                <DropdownItem onClick={() => handleUpdate(item.receiptId)}>View receipt</DropdownItem>
              </DropdownMenu>
            </Dropdown>
          </div>
        );
      default:
        return cellValue;
    }
  }, []);

  const onRowsPerPageChange = useCallback((e) => {
    setRowsPerPage(Number(e.target.value));
    setPage(1);
  }, []);

  const onSearchChange = useCallback((value) => {
    if (value) {
      setFilterValue(value);
      setPage(1);
    } else {
      setFilterValue("");
    }
  }, []);

  const navigate = useNavigate();

  const handleUpdate = (id) => {
    navigate(`/sale-manager/receipt/${id}`);
  };

  const topContent = useMemo(() => {
    return (
<div className="flex flex-col gap-4">
  <div className="flex justify-start items-center gap-3">
    {/* Warehouse Select */}
    <div className="flex-shrink-0 w-52">
      <Select
        labelId="warehouse-label"
        value={warehouseId} 
        onChange={(e) => setWarehouseId(e.target.value)}
        defaultSelectedKeys={["warehouse"]} 
        className="w-full"
      >
        <MenuItem key="warehouse" value="" style={{ color: "gray" }}> 
          Select a warehouse
        </MenuItem>
        {warehouses.map((cat) => (
          <MenuItem key={cat.warehouseId} value={cat.warehouseId}>
            {cat.name}
          </MenuItem>
        ))}
      </Select>
    </div>
    
    {/* Bay Select */}
    <div className="flex-shrink-0 w-52">
      <Select
        labelId="bay-label"
        value={bayId} 
        onChange={(e) => setBayId(e.target.value)}
        defaultSelectedKeys={["bay"]} 
        className="w-full"
      >
        <MenuItem key="bay" value="" style={{ color: "gray" }}> 
          Select a bay
        </MenuItem>
        {bays.map((cat) => (
          <MenuItem key={cat.bayId} value={cat.bayId}>
            {cat.code}
          </MenuItem>
        ))}
      </Select>
    </div>
  </div>

  <div className="flex justify-between items-center">
    <span className="text-default-400 text-small">Total {totalItems} items</span>
    <label className="flex items-center text-default-400 text-small">
      Rows per page:
      <select
        className="bg-transparent outline-none text-default-400 text-small"
        onChange={onRowsPerPageChange}
      >
        <option value="5">5</option>
        <option value="10">10</option>
        <option value="15">15</option>
      </select>
    </label>
  </div>
</div>



    );
  }, [
    onSearchChange,
    onRowsPerPageChange,
    totalItems
  ]);

  const bottomContent = useMemo(() => {
    return (
      <div className="py-2 px-2 flex justify-between items-center">
        <Pagination
          isCompact
          showControls
          classNames={{
            cursor: "bg-foreground text-background",
          }}
          color="default"
          page={page}
          total={pages}
          variant="light"
          onChange={setPage}
        />
      </div>
    );
  }, [page, pages]);

  const classNames = useMemo(
    () => ({
      wrapper: ["max-h-[382px]", "max-w-3xl"],
      th: ["bg-transparent", "text-default-500", "border-b", "border-divider"],
      td: [
        // changing the rows border radius
        // first
        "group-data-[first=true]:first:before:rounded-none",
        "group-data-[first=true]:last:before:rounded-none",
        // middle
        "group-data-[middle=true]:before:rounded-none",
        // last
        "group-data-[last=true]:first:before:rounded-none",
        "group-data-[last=true]:last:before:rounded-none",
      ],
    }),
    [],
  );

  return (
    <Table
      isCompact
      removeWrapper
      aria-label="Example table with custom cells, pagination and sorting"
      bottomContent={bottomContent}
      bottomContentPlacement="outside"
      checkboxesProps={{
        classNames: {
          wrapper: "after:bg-foreground after:text-background text-background",
        },
      }}
      classNames={classNames}
      topContent={topContent}
      topContentPlacement="outside"
    >
      <TableHeader columns={headerColumns}>
        {(column) => (
          <TableColumn
            key={column.uid}
            align={column.uid === "productName" ? "start" : "center"}
            allowsSorting={column.sortable}
          >
            {column.name}
          </TableColumn>
        )}
      </TableHeader>
      <TableBody emptyContent={"Loading ..."} items={items}>
        {(item) => (
          <TableRow key={item.inventoryItemId}>
            {(columnKey) => (
              <TableCell>
                {columnKey === "lastUpdatedStamp"
                  ? formatDate(item.lastUpdatedStamp)
                  : renderCell(item, columnKey)}
              </TableCell>
            )}
          </TableRow>
        )}
      </TableBody>
    </Table>
  );
}
