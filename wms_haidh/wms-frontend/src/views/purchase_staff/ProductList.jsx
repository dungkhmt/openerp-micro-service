import React from "react";
import {
  Table,
  TableHeader,
  TableColumn,
  TableBody,
  TableRow,
  TableCell,
  Input,
  Pagination,
  Select, MenuItem, Checkbox
} from "@heroui/react";
import { SearchIcon } from "../../components/icon/SearchIcon";
import { columns } from "../../config/productpurchase";
import { useState, useEffect, useMemo, useCallback } from "react";
import { request } from "../../api";
import { formatDate } from '../../utils/utils';

export default function ProductList() {

  const [page, setPage] = useState(1);
  const [rowsPerPage, setRowsPerPage] = useState(5);
  const [filterValue, setFilterValue] = useState("");
  const [debouncedSearchTerm, setDebouncedSearchTerm] = useState("");
  const [pages, setPages] = useState(1);
  const [items, setItems] = useState([]);
  const [totalItems, setTotalItems] = useState(0);
  const [warehouseId, setWarehouseId] = useState("");
  const [warehouses, setWarehouses] = useState([]);
  const [isSelected, setIsSelected] = useState(false);
  useEffect(() => {
    request("get", "/warehouses", (res) => {
      const warehouseList = res.data;
      setWarehouses(warehouseList);
      if (warehouseList.length > 0) {
        setWarehouseId(warehouseList[0].warehouseId);
      }
    }).then();
  }, []);

  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearchTerm(filterValue);  // Chỉ cập nhật sau khi người dùng ngừng gõ
    }, 500); // 1000ms = 1 giây

    // Hủy bỏ timeout nếu người dùng tiếp tục gõ
    return () => clearTimeout(timer);
  }, [filterValue]);

  // useEffect để gửi request sau khi giá trị tìm kiếm đã debounce
  useEffect(() => {
    if (warehouseId)
      request("get", `/products/inventory?page=${page - 1}&size=${rowsPerPage}&search=${debouncedSearchTerm}&warehouseId=${warehouseId}&outOfStockOnly=${isSelected}`, (res) => {
        setItems(res.data.content);
        setTotalItems(res.data.totalElements);
        setPages(res.data.totalPages);
      }).then();
  }, [page, rowsPerPage, debouncedSearchTerm, warehouseId, isSelected]);

  const renderCell = useCallback((item, columnKey) => {
    const cellValue = item[columnKey];

    switch (columnKey) {
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

  const topContent = useMemo(() => {
    return (
      <div className="flex flex-col gap-4">
        {warehouseId && (
          <>
            <div className="flex justify-start gap-3 items-end">
              <div className="flex-shrink-0 w-[25%]">

                <Select
                  aria-label="Warehouse"
                  defaultSelectedKeys={[warehouseId]}
                  onSelectionChange={(keys) => {
                    const selected = Array.from(keys)[0];
                    setWarehouseId(selected);
                  }}

                  className="w-full"
                >

                  {warehouses.map((wh) => (
                    <MenuItem style={{ outline: "none" }} key={wh.warehouseId} value={wh.warehouseId}>
                      {wh.name}
                    </MenuItem>
                  ))}
                </Select>
              </div>
              <Input
                isClearable
                className="w-full sm:max-w-[44%]"
                placeholder="Search by name..."
                startContent={<SearchIcon />}
                value={filterValue}
                onClear={() => setDebouncedSearchTerm("")}
                onValueChange={onSearchChange}
              />
            </div>
            <Checkbox isSelected={isSelected} onValueChange={setIsSelected} color="success" classNames={{
              label: "text-md text-default-600 font-medium", // chỉnh font size và màu
            }}>
              Out of stock
            </Checkbox>
          </>
        )
        }

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
    filterValue,
    onSearchChange,
    onRowsPerPageChange,
    totalItems,
    isSelected
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
        "py-3",
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
      <TableHeader columns={columns}>
        {(column) => (
          <TableColumn
            key={column.uid}
            align={column.uid === "totalQuantityOnHand" ? "center" : "start"}
            allowsSorting={column.sortable}
          >
            {column.name}
          </TableColumn>
        )}
      </TableHeader>
      <TableBody emptyContent={"Loading ..."} items={items}>
        {(item) => (
          <TableRow key={item.id}>
            {(columnKey) => (
              <TableCell>
                {columnKey === "dateUpdated"
                  ? formatDate(item.dateUpdated)
                  : renderCell(item, columnKey)}
              </TableCell>
            )}
          </TableRow>
        )}
      </TableBody>
    </Table>
  );
}
