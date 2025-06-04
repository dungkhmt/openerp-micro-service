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
} from "@heroui/react";

import { VerticalDotsIcon } from "../../../components/icon/VerticalDotsIcon";
import { SearchIcon } from "../../../components/icon/SearchIcon";
import { columns } from "../../../config/warehouse";
import { useNavigate } from 'react-router-dom';
import { useState, useEffect, useMemo, useCallback } from "react";
import { request } from "../../../api";

export default function WarehouseList() {

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
        request("get", `/warehouses/paged?page=${page - 1}&size=${rowsPerPage}&search=${debouncedSearchTerm}`, (res) => {
            setItems(res.data.content);
            setTotalItems(res.data.totalElements);
            setPages(res.data.totalPages);
        }).then();
    }, [page, rowsPerPage, debouncedSearchTerm]);

    const renderCell = useCallback((item, columnKey) => {
        const cellValue = item[columnKey];

        switch (columnKey) {
            case "name":
                return (
                    <div className="min-w-[200px] whitespace-nowrap overflow-hidden text-ellipsis">
                        {cellValue}
                    </div>
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
                                <DropdownItem onPress={() => handleUpdate(item.warehouseId)}>View</DropdownItem>
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
        navigate(`/warehouse-manager/warehouse/${id}`);
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
                        align={["name", "address"].includes(column.uid) ? "start" : "center"}
                    >
                        {column.name}
                    </TableColumn>
                )}
            </TableHeader>

            <TableBody emptyContent={"Loading ..."} items={items}>
                {(item) => (
                    <TableRow key={item.warehouseId}>
                        {(columnKey) => (
                            <TableCell>
                                {renderCell(item, columnKey)}
                            </TableCell>
                        )}
                    </TableRow>
                )}
            </TableBody>
        </Table>
    );
}
