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
import { PlusIcon } from "../../components/icon/PlusIcon";
import { VerticalDotsIcon } from "../../components/icon/VerticalDotsIcon";
import { SearchIcon } from "../../components/icon/SearchIcon";
import { columns } from "../../config/supplier";
import { useState, useEffect, useMemo, useCallback } from "react";
import { request } from "../../api";
import { toast, Toaster } from "react-hot-toast";

const buttonText = "Add supplier";
export default function SupplierList() {

  const [page, setPage] = useState(1);
  const [rowsPerPage, setRowsPerPage] = useState(5);
  const [filterValue, setFilterValue] = useState("");
  const [debouncedSearchTerm, setDebouncedSearchTerm] = useState("");
  const [pages, setPages] = useState(1);
  const [items, setItems] = useState([]);
  const [totalItems, setTotalItems] = useState(0);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const [staffInfo, setStaffInfo] = useState({
    supplierId: '',
    name: '',
    address: '',
    email: '',
    phone: ''
  });

  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearchTerm(filterValue);
    }, 500);

    return () => clearTimeout(timer);
  }, [filterValue]);

  useEffect(() => {
    request("get", `/suppliers/paged?page=${page - 1}&size=${rowsPerPage}&search=${debouncedSearchTerm}`, (res) => {
      setItems(res.data.content);
      setTotalItems(res.data.totalElements);
      setPages(res.data.totalPages);
    }).then();
  }, [page, rowsPerPage, debouncedSearchTerm]);

  const [selectedKeys, setSelectedKeys] = useState(new Set([]));

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
                <DropdownItem onPress={() => handleUpdate(item.supplierId)}>Update</DropdownItem>
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

  const handleOpenModal = () => {
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setStaffInfo((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleAdd = () => {
    setStaffInfo({ supplierId: '', name: '', address: '', email: '', phone: '' });
    handleOpenModal();
  };

  const handleUpdate = (id) => {
    handleOpenModal();
    request('get', `/suppliers/${id}`, (res) => {
      setStaffInfo(res.data);
    });
  };

  const validateForm = () => {
    if (!staffInfo.name.trim()) {
      toast.error("Supplier name is required");
      return false;
    }
    if (!staffInfo.address.trim()) {
      toast.error("Address is required");
      return false;
    }
    if (!staffInfo.email.trim()) {
      toast.error("Email is required");
      return false;
    }
    if (!/^[\w-.]+@([\w-]+\.)+[\w-]{2,4}$/.test(staffInfo.email)) {
      toast.error("Invalid email format");
      return false;
    }
    if (!staffInfo.phone.trim()) {
      toast.error("Phone number is required");
      return false;
    }
    if (!/^[0-9]{8,15}$/.test(staffInfo.phone)) {
      toast.error("Phone number must be 8–15 digits");
      return false;
    }
    return true;
  };


  const handleConfirm = () => {

    if (!validateForm()) return;

    const requestUrl = staffInfo.supplierId
      ? "/suppliers/update"
      : "/suppliers";

    const payload = { ...staffInfo };
    if (!staffInfo.supplierId) delete payload.supplierId;

    request("post", requestUrl, (res) => {
      if (res.status === 200) {
        setPage(1);
        request("get", `/suppliers/paged?page=${page - 1}&size=${rowsPerPage}&search=${debouncedSearchTerm}`, (res) => {
          setItems(res.data.content);
          setTotalItems(res.data.totalElements);
          setPages(res.data.totalPages);
        }).then();
        toast.success("Suppier saved successfully!");
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
            placeholder="Search by name..."
            startContent={<SearchIcon />}
            value={filterValue}
            onClear={() => setDebouncedSearchTerm("")}
            onValueChange={onSearchChange}
          />
          <div className="flex gap-3">
            <Button
              className="bg-[#019160] text-white hover:bg-[#2fbe8e] active:bg-[#01b075]"
              startContent={<PlusIcon />}
              size="md"
              onPress={handleAdd}
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
        selectedKeys={selectedKeys}
        topContent={topContent}
        topContentPlacement="outside"
        onSelectionChange={setSelectedKeys}
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
            <TableRow key={item.supplierId}>
              {(columnKey) => (
                <TableCell>
                  {renderCell(item, columnKey)}
                </TableCell>
              )}
            </TableRow>
          )}
        </TableBody>
      </Table>
      <Modal isOpen={isModalOpen} onClose={handleCloseModal}>
        <ModalContent>
          <ModalHeader>Supplier Information</ModalHeader>
          <ModalBody>
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium">Supplier Name</label>
                <input
                  type="text"
                  name="name"
                  value={staffInfo.name}
                  onChange={handleChange}
                  className="w-full p-2 border rounded"
                />
              </div>
              <div>
                <label className="block text-sm font-medium">Address</label>
                <input
                  type="text"
                  name="address"
                  value={staffInfo.address}
                  onChange={handleChange}
                  className="w-full p-2 border rounded"
                />
              </div>
              <div>
                <label className="block text-sm font-medium">Email</label>
                <input
                  type="email"
                  name="email"
                  value={staffInfo.email}
                  onChange={handleChange}
                  className="w-full p-2 border rounded"
                />
              </div>
              <div>
                <label className="block text-sm font-medium">Phone Number</label>
                <input
                  type="text"
                  name="phone"
                  value={staffInfo.phone}
                  onChange={handleChange}
                  className="w-full p-2 border rounded"
                />
              </div>
            </div>
          </ModalBody>
          <ModalFooter>
            <Button onPress={handleCloseModal} variant="light">Cancel</Button>
            <Button className="bg-[#019160] text-white hover:bg-[#2fbe8e] active:bg-[#01b075]" onPress={() => handleConfirm()} color="primary">
              Confirm
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>

    </>
  );
}
