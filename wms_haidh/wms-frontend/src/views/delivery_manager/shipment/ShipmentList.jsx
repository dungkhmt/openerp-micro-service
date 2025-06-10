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
  Modal, ModalContent, ModalHeader, ModalBody, ModalFooter
} from "@heroui/react";
import { PlusIcon } from "../../../components/icon/PlusIcon";
import { VerticalDotsIcon } from "../../../components/icon/VerticalDotsIcon";
import { SearchIcon } from "../../../components/icon/SearchIcon";
import { columns } from "../../../config/shipment";
import { useNavigate } from 'react-router-dom';
import { useState, useEffect, useMemo, useCallback } from "react";
import { request } from "../../../api";
import { formatDate } from '../../../utils/utils';
import { toast, Toaster } from "react-hot-toast";

const buttonText = "Create shipment";
export default function Shipment() {

  const [page, setPage] = useState(1);
  const [rowsPerPage, setRowsPerPage] = useState(5);
  const [filterValue, setFilterValue] = useState("");
  const [debouncedSearchTerm, setDebouncedSearchTerm] = useState("");
  const [pages, setPages] = useState(1);
  const [items, setItems] = useState([]);
  const [totalItems, setTotalItems] = useState(0);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedDateTime, setSelectedDateTime] = useState("");

  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearchTerm(filterValue);
    }, 500);
    return () => clearTimeout(timer);
  }, [filterValue]);

  useEffect(() => {
    request("get", `/shipments?page=${page - 1}&size=${rowsPerPage}&search=${debouncedSearchTerm}`, (res) => {
      setItems(res.data.content);
      setTotalItems(res.data.totalElements);
      setPages(res.data.totalPages);
    }).then();
  }, [page, rowsPerPage, debouncedSearchTerm]);

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
                <DropdownItem onPress={() => handleUpdate(item.shipmentId)}>View delivery trips</DropdownItem>
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
    navigate(`/delivery-manager/shipments/${id}`);
  };

  const handleOpenModal = () => {
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
  };

  const handleConfirm = () => {
    if (!selectedDateTime) {
      toast.error("Please select a delivery time");
      return;
    }

    const selected = new Date(selectedDateTime);
    const now = new Date();

    if (selected < now) {
      toast.error("Delivery time must be in the future");
      return;
    }

    const formattedDateTime = `${selectedDateTime}:00`;

    const payload = {
      expectedDeliveryStamp: formattedDateTime
    };

    const requestUrl = "/shipments";

    request("post", requestUrl, (res) => {
      if (res.status === 200) {
        setPage(1);
        request("get", `/shipments?page=${page - 1}&size=${rowsPerPage}&search=${debouncedSearchTerm}`, (res) => {
          setItems(res.data.content);
          setTotalItems(res.data.totalElements);
          setPages(res.data.totalPages);
        }).then();
        toast.success("Shipment created successfully!");
      }
    }, {
      onError: (e) => {
        toast.error(e?.response?.data || "Error occured!");
      }
    }, payload);

    handleCloseModal();
  };



  const topContent = useMemo(() => {
    return (
      <div className="flex flex-col gap-4">
        <div className="flex justify-between gap-3 items-end">
          <Input
            isClearable
            className="w-full sm:max-w-[44%]"
            placeholder="Search by shipment ID..."
            startContent={<SearchIcon />}
            value={filterValue}
            onClear={() => setDebouncedSearchTerm("")}
            onValueChange={onSearchChange}
          />
          <div className="flex gap-3">
            <Button className="bg-[#019160] text-white hover:bg-[#2fbe8e] active:bg-[#01b075]"
              startContent={<PlusIcon />} size="md" onPress={handleOpenModal}>
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
    <>
      <Toaster />
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
              align={column.uid === "actions" ? "center" : "start"}
              allowsSorting={column.sortable}
            >
              {column.name}
            </TableColumn>
          )}
        </TableHeader>
        <TableBody emptyContent={"Loading ..."} items={items}>
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
      <Modal isOpen={isModalOpen} onClose={handleCloseModal}>
        <ModalContent>
          <ModalHeader>Select Expected Delivery Time</ModalHeader>
          <ModalBody>
            <input
              type="datetime-local"
              value={selectedDateTime}
              onChange={(e) => setSelectedDateTime(e.target.value)}
              className="w-full p-2 border rounded"
            />
          </ModalBody>
          <ModalFooter>
            <Button onPress={handleCloseModal} variant="light">Cancel</Button>
            <Button className="bg-[#019160] text-white hover:bg-[#2fbe8e] active:bg-[#01b075]" onPress={handleConfirm} color="primary">Confirm</Button>
          </ModalFooter>
        </ModalContent>
      </Modal>
    </>
  );
}
