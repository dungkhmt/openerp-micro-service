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
} from "@heroui/react";
import { VerticalDotsIcon } from "../../../components/icon/VerticalDotsIcon";
import { Badge } from "../../../components/button/badge";
import { columns, statusOptions } from "../../../config/assignorder";
import { useNavigate } from 'react-router-dom';
import { useState, useEffect, useMemo, useCallback } from "react";
import { request } from "../../../api";
import { formatDate, formatPrice } from '../../../utils/utils';
const statusColorMap = {
  APPROVED: "secondary",
  IN_PROGRESS: "warning"
};

const INITIAL_VISIBLE_COLUMNS = ["orderDate", "customerName", "totalOrderCost", "status", "approvedBy", "actions"];
export default function SaleOrderList() {

  const [page, setPage] = useState(1);
  const [rowsPerPage, setRowsPerPage] = useState(5);
  const [pages, setPages] = useState(1);
  const [items, setItems] = useState([]);
  const [totalItems, setTotalItems] = useState(0);
  const [statusFilter, setStatusFilter] = useState("APPROVED");

  useEffect(() => {
    request("get", `/orders?status=${statusFilter}&page=${page - 1}&size=${rowsPerPage}`, (res) => {
      setItems(res.data.content);
      setTotalItems(res.data.totalElements);
      setPages(res.data.totalPages);
    }).then();
  }, [page, rowsPerPage, statusFilter]);

  const visibleColumns = useMemo(() => {
    const updatedColumns = new Set(INITIAL_VISIBLE_COLUMNS);
    return updatedColumns;
  }, [statusFilter]);

  const headerColumns = useMemo(() => {
    if (visibleColumns === "all") return columns;

    return columns.filter((column) => Array.from(visibleColumns).includes(column.uid));
  }, [visibleColumns]);

  const renderCell = useCallback((item, columnKey) => {
    const cellValue = item[columnKey];

    switch (columnKey) {
      case "status":
        const status = item.status;
        if (status === "ASSIGNED") {
          return (
            <Badge className="bg-green-100 text-green-800" variant="flat">
              {cellValue.replace(/_/g, ' ')}
            </Badge>
          );
        } else if (status === "PICK_COMPLETE") {
          return (
            <Badge className="bg-green-600 text-white" variant="flat">
              {cellValue.replace(/_/g, ' ')}
            </Badge>
          );
        }

        return (
          <Badge color={statusColorMap[status]}>
            {cellValue.replace(/_/g, ' ')}
          </Badge>
        );
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
                <DropdownItem onPress={() => handleUpdate(item.orderId)}>Update order</DropdownItem>
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


  const navigate = useNavigate();

  const handleUpdate = (id) => {
    navigate(`/warehouse-manager/orders/${id}`);
  };

  const topContent = useMemo(() => {
    return (
      <div className="flex flex-col gap-4">
        <div className="flex justify-between items-center gap-3">
          {/* Status Select */}
          <div className="w-[20%]">
            <Select
              aria-label="Status"
              labelId="status-label"
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              defaultSelectedKeys={["APPROVED"]}
              className="w-full"
            >
              {statusOptions.map((cat) => (
                <MenuItem style={{ outline: "none" }} key={cat.uid} value={cat.uid}>
                  {cat.name}
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
            align={"center"}
            allowsSorting={column.sortable}
          >
            {column.name}
          </TableColumn>
        )}
      </TableHeader>
      <TableBody emptyContent={"Loading ..."} items={items}>
        {(item) => (
          <TableRow key={item.orderId}>
            {(columnKey) => (
              <TableCell>
                {columnKey === "orderDate"
                  ? formatDate(item.orderDate)
                  : (columnKey === "totalOrderCost" ? formatPrice(item.totalOrderCost) : renderCell(item, columnKey))}
              </TableCell>
            )}
          </TableRow>
        )}
      </TableBody>
    </Table>
  );
}
