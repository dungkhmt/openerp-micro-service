import React from "react";
import {
  Table,
  TableHeader,
  TableColumn,
  TableBody,
  TableRow,
  TableCell,
  Input,
  Button,
  DropdownTrigger,
  Dropdown,
  DropdownMenu,
  DropdownItem,
  Pagination,
} from "@nextui-org/react";
import { PlusIcon } from "../../components/icon/PlusIcon";
import { VerticalDotsIcon } from "../../components/icon/VerticalDotsIcon";
import { SearchIcon } from "../../components/icon/SearchIcon";
import { columns, statusOptions } from "../../config/shipment";
import { useNavigate } from 'react-router-dom';
import { useState, useEffect, useMemo, useCallback } from "react";
import { request } from "../../api";

const INITIAL_VISIBLE_COLUMNS = ["shipmentId","expectedDeliveryStamp","createdBy", "actions"];
const buttonText = "Add Shipment";
export default function Shipment() {

  const [page, setPage] = useState(1);
  const [rowsPerPage, setRowsPerPage] = useState(5);
  const [filterValue, setFilterValue] = useState("");
  const [debouncedSearchTerm, setDebouncedSearchTerm] = useState("");
  const [pages, setPages] = useState(1);
  const [items, setItems] = useState([]);
  const [totalItems, setTotalItems] = useState(0);

  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearchTerm(filterValue);  // Chỉ cập nhật sau khi người dùng ngừng gõ
    }, 500); // 1000ms = 1 giây

    // Hủy bỏ timeout nếu người dùng tiếp tục gõ
    return () => clearTimeout(timer);
  }, [filterValue]);

  // useEffect để gửi request sau khi giá trị tìm kiếm đã debounce
  useEffect(() => {
    request("get", `/delivery-manager/shipments?page=${page - 1}&size=${rowsPerPage}&search=${debouncedSearchTerm}`, (res) => {
      setItems(res.data.content);
      setTotalItems(res.data.totalElements);
      setPages(res.data.totalPages);
    }).then();
  }, [page, rowsPerPage, debouncedSearchTerm]);

  const [selectedKeys, setSelectedKeys] = useState(new Set([]));
  const [visibleColumns, setVisibleColumns] = useState(new Set(INITIAL_VISIBLE_COLUMNS));
  const [statusFilter, setStatusFilter] = useState("all");

  const headerColumns = useMemo(() => {
    if (visibleColumns === "all") return columns;

    return columns.filter((column) => Array.from(visibleColumns).includes(column.uid));
  }, [visibleColumns]);

  const filteredItems = useMemo(() => {
    let filteredItems = [...items];
    if (statusFilter !== "all" && Array.from(statusFilter).length !== statusOptions.length) {
      filteredItems = filteredItems.filter((item) =>
        Array.from(statusFilter).includes(item.status),
      );
    }

    return filteredItems;
  }, [items, statusFilter]);

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
                <DropdownItem onClick={() => handleUpdate(item.id)}>Update</DropdownItem>
                <DropdownItem onClick={() => handleDelete(item.id)}>Delete</DropdownItem>
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

  const handleAddShipment = () => {
    navigate('/delivery-manager/delivery-person/add-shipment');
  };

  const handleUpdate = (id) => {
    navigate(`/delivery-manager/delivery-person/${id}`);
  };

  const handleDelete = (id) => {
    // request(
    //   "post", // HTTP method
    //   "/admin/product/delete-product", // Endpoint for deleting product
    //   (res) => {
    //     if (res.status === 200) {
    //       if (items.length === 1 && page > 1) setPage(page - 1);
    //       request("get", `/admin/product?page=${page - 1}&size=${rowsPerPage}&search=${debouncedSearchTerm}`, (res) => {
    //         setItems(res.data.content);
    //         setTotalItems(res.data.totalElements);
    //         setPages(res.data.totalPages);
    //       }).then();

    //     }
    //   },
    //   {
    //     onError: (e) => console.error("Error deleting product:", e),
    //   },
    //   { id }
    // );
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
        <div className="flex justify-between gap-3 items-end">
          <Input
            isClearable
            className="w-full sm:max-w-[44%]"
            placeholder="Search by name..."
            startContent={<SearchIcon />}
            value={filterValue}
            onClear={() => setDebouncedSearchTerm("")}
            onValueChange={onSearchChange}
          />
          <div className="flex gap-3">            
            <Button
              className="bg-foreground text-background"
              endContent={<PlusIcon />}
              size="md"
              onClick={handleAddShipment}
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
    filterValue,
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
        {/* <span className="text-small text-default-400">
          {selectedKeys === "all"
            ? "All items selected"
            : `${selectedKeys.size} of ${filteredItems.length} selected`}
        </span> */}
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
      selectedKeys={selectedKeys}
      topContent={topContent}
      topContentPlacement="outside"
      onSelectionChange={setSelectedKeys}
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
      <TableBody emptyContent={"Loading ..."} items={filteredItems}>
        {(item) => (
          <TableRow key={item.shipmentId}>
            {(columnKey) => (
              <TableCell>
                {columnKey === "expectedDeliveryStamp"
                  ? formatDate(item.expectedDeliveryStamp)
                  : renderCell(item, columnKey)}
              </TableCell>
            )}
          </TableRow>
        )}
      </TableBody>
    </Table>
  );
}
