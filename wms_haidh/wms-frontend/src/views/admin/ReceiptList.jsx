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
  Progress,
  Select, MenuItem
} from "@nextui-org/react";
import { PlusIcon } from "../../components/icon/PlusIcon";
import { VerticalDotsIcon } from "../../components/icon/VerticalDotsIcon";
import { columns, statusOptions } from "../../config/admin";
import { useNavigate } from 'react-router-dom';
import { useState, useEffect, useMemo, useCallback } from "react";
import { request } from "../../api";

const INITIAL_VISIBLE_COLUMNS = ["productName", "quantity", "warehouseName", "expectedReceiptDate", "completed", "actions"];
const buttonText = "View Receipt Bill";
export default function ReceiptList() {

  const [page, setPage] = useState(1);
  const [rowsPerPage, setRowsPerPage] = useState(5);
  const [pages, setPages] = useState(1);
  const [items, setItems] = useState([]);
  const [totalItems, setTotalItems] = useState(0);
  const [statusFilter, setStatusFilter] = useState("APPROVED");

  useEffect(() => {
    request("get", `/admin/receipt?status=${statusFilter}&page=${page - 1}&size=${rowsPerPage}`, (res) => {
      setItems(res.data.content);
      setTotalItems(res.data.totalElements);
      setPages(res.data.totalPages);
    }).then();
  }, [page, rowsPerPage, statusFilter]);

  const [visibleColumns, setVisibleColumns] = useState(new Set(INITIAL_VISIBLE_COLUMNS));

  const headerColumns = useMemo(() => {
    if (visibleColumns === "all") return columns;

    return columns.filter((column) => Array.from(visibleColumns).includes(column.uid));
  }, [visibleColumns]);

  const renderCell = useCallback((item, columnKey) => {
    const cellValue = item[columnKey];

    switch (columnKey) {
      case "completed":
        return (
          <Progress 
            aria-label="Completed..."
            className="max-w-md min-h-[2.5rem]"
            color={item.completed === 100 ? "success" : "warning"}
            showValueLabel={true}
            size="sm"
            value={item.completed}
          />
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
                <DropdownItem onClick={() => handleUpdate(item.receiptItemRequestId)}>Update request</DropdownItem>
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

  const handleViewBill = () => {
    navigate('/admin/receipts/receipt-bill');
  };

  const handleUpdate = (id) => {
    navigate(`/admin/receipts/${id}`);
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    const options = {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      hour12: false, // Đảm bảo không sử dụng định dạng giờ AM/PM
    };
    return date.toLocaleString('en-GB', options); // Hoặc 'en-US' nếu bạn muốn kiểu định dạng kiểu Mỹ
  };




  const topContent = useMemo(() => {
    return (
      <div className="flex flex-col gap-4">
        <div className="flex justify-between items-center gap-3">
          {/* Status Select */}
          <div className="flex-shrink-0 w-40">
            <Select
              labelId="status-label"
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              defaultSelectedKeys={["APPROVED"]}
              className="w-full"
            >
              {statusOptions.map((cat) => (
                <MenuItem key={cat.uid} value={cat.uid}>
                  {cat.name}
                </MenuItem>
              ))}
            </Select>
          </div>

          {/* Add Button */}
          <div className="flex-shrink-0">
            <Button
              className="bg-foreground text-background"
              endContent={<PlusIcon />}
              size="md"
              onClick={handleViewBill}
            >
              {buttonText}
            </Button>
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
            align={column.uid === "actions" ? "center" : "start"}
            allowsSorting={column.sortable}
          >
            {column.name}
          </TableColumn>
        )}
      </TableHeader>
      <TableBody emptyContent={"Loading ..."} items={items}>
        {(item) => (
          <TableRow key={item.receiptItemRequestId} >
            {(columnKey) => (
              <TableCell>
                {columnKey === "expectedReceiptDate"
                  ? formatDate(item.expectedReceiptDate)
                  : renderCell(item, columnKey)}
              </TableCell>
            )}
          </TableRow>
        )}
      </TableBody>
    </Table>
  );
}
