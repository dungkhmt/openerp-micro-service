import React, { useState, useEffect, useMemo, useCallback } from "react";
import {
    Table, TableHeader, TableColumn, TableBody, TableRow, TableCell,
    Input, Button, DropdownTrigger, Dropdown, DropdownMenu, DropdownItem,
    Pagination, Modal, ModalContent, ModalHeader, ModalBody, ModalFooter, Select, MenuItem
} from "@heroui/react";
import { VerticalDotsIcon } from "../../../components/icon/VerticalDotsIcon";
import { SearchIcon } from "../../../components/icon/SearchIcon";
import { columns, statusOptions } from "../../../config/distance";
import { request } from "../../../api";
import { toast, Toaster } from "react-hot-toast";

export default function Distance() {
    const [page, setPage] = useState(1);
    const [rowsPerPage, setRowsPerPage] = useState(5);
    const [fromFilterValue, setFromFilterValue] = useState("");
    const [toFilterValue, setToFilterValue] = useState("");
    const [debouncedFrom, setDebouncedFrom] = useState("");
    const [debouncedTo, setDebouncedTo] = useState("");
    const [pages, setPages] = useState(1);
    const [items, setItems] = useState([]);
    const [totalItems, setTotalItems] = useState(0);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [statusFilter, setStatusFilter] = useState("wc");

    const selectedOption = statusOptions.find(opt => opt.key === statusFilter);
    const fromType = selectedOption?.fromType || "";
    const toType = selectedOption?.toType || "";


    const [distanceInfo, setDistanceInfo] = useState({
        addressDistanceId: "",
        fromLocationName: "",
        toLocationName: "",
        distance: ""
    });

    // Debounce for From Location search
    useEffect(() => {
        const timer = setTimeout(() => setDebouncedFrom(fromFilterValue), 500);
        return () => clearTimeout(timer);
    }, [fromFilterValue]);

    // Debounce for To Location search
    useEffect(() => {
        const timer = setTimeout(() => setDebouncedTo(toFilterValue), 500);
        return () => clearTimeout(timer);
    }, [toFilterValue]);

    // Fetch data when page, rowsPerPage, or search terms change
    useEffect(() => {
        request(
            "get",
            `/address-distances?page=${page - 1}&size=${rowsPerPage}&fromType=${fromType}&toType=${toType}&fromLocation=${debouncedFrom}&toLocation=${debouncedTo}`,
            (res) => {
                setItems(res.data.content);
                setTotalItems(res.data.totalElements);
                setPages(res.data.totalPages);
            }
        );
    }, [page, rowsPerPage, selectedOption, debouncedFrom, debouncedTo]);

    const handleOpenModal = () => setIsModalOpen(true);
    const handleCloseModal = () => setIsModalOpen(false);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setDistanceInfo((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleUpdate = (item) => {
        setDistanceInfo(item);
        handleOpenModal();
    };

    const handleConfirm = () => {
        const payload = {
            addressDistanceId: distanceInfo.addressDistanceId,
            distance: distanceInfo.distance
        }
        request("post", `/address-distances/update`, (res) => {
            if (res.status === 200) {
                toast.success("Distance updated successfully!");
                request("get", `/address-distances?page=${page - 1}&size=${rowsPerPage}&fromLocation=${debouncedFrom}`, (res) => {
                    setItems(res.data.content);
                    setPage(1);
                    setTotalItems(res.data.totalElements);
                    setPages(res.data.totalPages);
                });
            }
        }, {
            onError: (e) => {
                toast.error(e?.response?.data || "Error occured!");
            }
        }, payload);
        handleCloseModal();
    };

    const handleAutoUpdate = () => {
        request("post", "/address-distances/update-all", (res) => {
            if (res.status === 200) {
                toast.success("Distances updated successfully!");
                // Reload data
                request(
                    "get",
                    `/address-distances?page=${page - 1}&size=${rowsPerPage}&fromType=${fromType}&toType=${toType}&fromLocation=${debouncedFrom}&toLocation=${debouncedTo}`,
                    (res) => {
                        setItems(res.data.content);
                        setPage(1);
                        setTotalItems(res.data.totalElements);
                        setPages(res.data.totalPages);
                    }
                );
            }
        }, {
            onError: (e) => {
                toast.error(e?.response?.data || "Error occured!");
            }
        });
    };

    const onRowsPerPageChange = useCallback((e) => {
        setRowsPerPage(Number(e.target.value));
        setPage(1);
    }, []);

    const renderCell = useCallback((item, columnKey) => {
        const cellValue = item[columnKey];
        switch (columnKey) {
            case "fromLocationName":
            case "toLocationName":
                return (
                    <div className="max-w-[300px] whitespace-nowrap overflow-hidden text-ellipsis">
                        {cellValue}
                    </div>
                );
            case "distance":
                return Math.round(cellValue);
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
                                <DropdownItem onPress={() => handleUpdate(item)}>Update</DropdownItem>
                            </DropdownMenu>
                        </Dropdown>
                    </div>
                );
            default:
                return cellValue;
        }
    }, []);

    const topContent = useMemo(() => (
        <div className="flex flex-col gap-4">
            <div className="flex justify-start gap-3 items-end">
                <div className="flex-shrink-0 w-[25%]">
                    <Select
                        aria-label="Select status"
                        labelId="status-label"
                        value={statusFilter}
                        onChange={(e) => setStatusFilter(e.target.value)}
                        defaultSelectedKeys={["whc"]}
                        className="w-full"
                    >
                        {statusOptions.map((cat) => (
                            <MenuItem style={{ outline: "none" }} key={cat.key} value={cat.key}>
                                {cat.name}
                            </MenuItem>
                        ))}
                    </Select>
                </div>
                <Input
                    isClearable
                    className="w-full sm:max-w-[30%]"
                    placeholder="From location..."
                    startContent={<SearchIcon />}
                    value={fromFilterValue}
                    onClear={() => setFromFilterValue("")}
                    onValueChange={(val) => {
                        setFromFilterValue(val || "");
                        setPage(1);
                    }}
                />
                <Input
                    isClearable
                    className="w-full sm:max-w-[30%]"
                    placeholder="To location..."
                    startContent={<SearchIcon />}
                    value={toFilterValue}
                    onClear={() => setToFilterValue("")}
                    onValueChange={(val) => {
                        setToFilterValue(val || "");
                        setPage(1);
                    }}
                />
            </div>
            <div className="flex justify-between items-center">
                <span className="text-default-400 text-small">Total {totalItems} items</span>
                <div className="flex items-center gap-3">
                    <Button
                        size="sm"
                        className="bg-[#019160] text-white hover:bg-[#2fbe8e] active:bg-[#01b075]"
                        onPress={handleAutoUpdate}
                    >
                        Auto Update Distances
                    </Button>

                    <label className="flex items-center text-default-400 text-small">
                        Rows per page:
                        <select
                            className="bg-transparent outline-none text-default-400 text-small ml-1"
                            onChange={onRowsPerPageChange}
                        >
                            <option value="5">5</option>
                            <option value="10">10</option>
                            <option value="15">15</option>
                        </select>
                    </label>
                </div>
            </div>


        </div>
    ), [fromFilterValue, toFilterValue, onRowsPerPageChange, totalItems]);

    const bottomContent = useMemo(() => (
        <div className="py-2 px-2 flex justify-between items-center">
            <Pagination
                isCompact
                showControls
                classNames={{ cursor: "bg-foreground text-background" }}
                color="default"
                page={page}
                total={pages}
                variant="light"
                onChange={setPage}
            />
        </div>
    ), [page, pages]);

    const classNames = useMemo(() => ({
        wrapper: ["max-h-[382px]", "max-w-3xl"],
        th: ["bg-transparent", "text-default-500", "border-b", "border-divider"],
        td: [
            "group-data-[first=true]:first:before:rounded-none",
            "group-data-[first=true]:last:before:rounded-none",
            "group-data-[middle=true]:before:rounded-none",
            "group-data-[last=true]:first:before:rounded-none",
            "group-data-[last=true]:last:before:rounded-none",
        ],
    }), []);

    return (
        <>
            <Toaster />
            <Table
                isCompact
                removeWrapper
                aria-label="Distance table"
                bottomContent={bottomContent}
                bottomContentPlacement="outside"
                classNames={classNames}
                topContent={topContent}
                topContentPlacement="outside"
            >
                <TableHeader columns={columns}>
                    {(column) => (
                        <TableColumn
                            key={column.uid}
                            align={["actions", "distance"].includes(column.uid) ? "center" : "start"}
                        >
                            {column.name}
                        </TableColumn>
                    )}
                </TableHeader>
                <TableBody emptyContent={"Loading ..."} items={items}>
                    {(item) => (
                        <TableRow key={item.addressDistanceId}>
                            {(columnKey) => (
                                <TableCell>{renderCell(item, columnKey)}</TableCell>
                            )}
                        </TableRow>
                    )}
                </TableBody>
            </Table>

            <Modal isOpen={isModalOpen} onClose={handleCloseModal}>
                <ModalContent>
                    <ModalHeader>Update Distance</ModalHeader>
                    <ModalBody>
                        <div className="space-y-4">
                            <div>
                                <label className="block text-sm font-medium">From Location</label>
                                <input
                                    type="text"
                                    name="fromLocation"
                                    value={distanceInfo.fromLocationName}
                                    onChange={handleChange}
                                    className="w-full p-2 border rounded"
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-medium">To Location</label>
                                <input
                                    type="text"
                                    name="toLocation"
                                    value={distanceInfo.toLocationName}
                                    onChange={handleChange}
                                    className="w-full p-2 border rounded"
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-medium">Distance (m)</label>
                                <input
                                    type="number"
                                    name="distance"
                                    value={distanceInfo.distance}
                                    onChange={handleChange}
                                    className="w-full p-2 border rounded"
                                />
                            </div>
                        </div>
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
