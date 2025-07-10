import React, { useEffect, useState } from "react";
import { TextField, MenuItem, Select, FormControl, InputAdornment, IconButton } from "@mui/material";
import Button from "@mui/material/Button";
import Divider from "@mui/material/Divider";
import SearchIcon from '@mui/icons-material/Search';
import CloseIcon from '@mui/icons-material/Close';
import { Toaster } from "react-hot-toast";
import ChevronLeftIcon from '@mui/icons-material/ChevronLeft';
import ChevronRightIcon from '@mui/icons-material/ChevronRight';
import { useSidebar } from "../../layout/SidebarContext";
import ProductPreview from "./ProductPreview";
import BreadcrumbsCustom from "../../components/BreadcrumbsCustom";
import { request } from '../../api';

export default function Home() {
    const [products, setProducts] = useState([]);
    const [categories, setCategories] = useState([]);
    const [search, setSearch] = useState("");
    const [debouncedSearchTerm, setDebouncedSearchTerm] = useState("");
    const [categoryId, setCategoryId] = useState("all");
    const categoryParam = categoryId === 'all' ? '' : categoryId;
    const [sorting, setSorting] = useState("asc");
    const { open } = useSidebar();
    const [page, setPage] = useState(1);
    const itemsPerPage = open ? 3 : 4;
    const [totalPages, setTotalPages] = useState(1);


    useEffect(() => {
        const timer = setTimeout(() => {
            setDebouncedSearchTerm(search);  // Chỉ cập nhật sau khi người dùng ngừng gõ
        }, 500); // 1000ms = 1 giây

        // Hủy bỏ timeout nếu người dùng tiếp tục gõ
        return () => clearTimeout(timer);
    }, [search]);

    useEffect(() => {
        request("get", "/categories", (res) => {
            setCategories(res.data);
        }).then();
    }, [])

    useEffect(() => {
        setPage(1);
    }, [open, debouncedSearchTerm, categoryId, sorting]);

    useEffect(() => {
        request("get", `/products/public?page=${page - 1}&size=${itemsPerPage}&searchTerm=${debouncedSearchTerm}&categoryId=${categoryParam}&sortDir=${sorting}`, (res) => {
            setProducts(res.data.content);
            setTotalPages(res.data.totalPages);
        }).then();
    }, [page, itemsPerPage, debouncedSearchTerm, categoryParam, sorting]);

    const breadcrumbPaths = [
        { label: "Home", link: "/" },
        { label: "Products", link: "/products" }

    ];

    return (
        <div style={{ padding: "16px" }}>
            <Toaster />
            {/* Breadcrumb */}
            <BreadcrumbsCustom paths={breadcrumbPaths} />

            {/* Thanh tìm kiếm & sắp xếp */}
            <div style={{ display: "flex", gap: "12px", marginBottom: "16px", alignItems: "center", flexWrap: "wrap" }}>
                <TextField
                    variant="outlined"
                    onChange={(e) => setSearch(e.target.value)}
                    sx={{
                        width: "300px",
                        height: "48px"
                    }}
                    placeholder="Search by name ..."
                    value={search}
                    InputProps={{
                        startAdornment: (
                            <InputAdornment position="start">
                                <SearchIcon />
                            </InputAdornment>
                        ),
                        endAdornment: search && (
                            <InputAdornment position="end">
                                <IconButton
                                    size="small"
                                    onClick={() => setSearch('')}
                                >
                                    <CloseIcon fontSize="small" />
                                </IconButton>
                            </InputAdornment>
                        ),
                        sx: {
                            height: "48px",
                            paddingLeft: "16px"
                        },
                    }}
                />

                <FormControl variant="outlined" sx={{ minWidth: 200, height: "48px" }}>
                    <Select
                        value={categoryId}
                        onChange={(e) => setCategoryId(e.target.value)}
                        aria-label="Category"
                        sx={{ height: "48px" }}
                    >
                        <MenuItem value="all">All categories</MenuItem>
                        {categories.map((category) => (
                            <MenuItem key={category.categoryId} value={category.categoryId}>
                                {category.name}
                            </MenuItem>
                        ))}
                    </Select>
                </FormControl>


                <Button
                    variant="contained"
                    onClick={() => setSorting("asc")}
                    sx={{
                        height: "48px",
                        backgroundColor: sorting === "asc" ? "#6fd649" : "transparent",
                        color: sorting === "asc" ? "#fff" : "#6fd649",
                        border: "1px solid #6fd649",
                        '&:hover': {
                            backgroundColor: sorting === "asc" ? "#6fd649" : "transparent",
                        },
                        '&:active': {
                            backgroundColor: sorting === "asc" ? "#3e8e41" : "transparent",
                        },
                    }}
                >
                    Price Asc
                </Button>



                <Button
                    variant="contained"
                    onClick={() => setSorting("desc")}
                    sx={{
                        height: "48px",
                        backgroundColor: sorting === "desc" ? "#6fd649" : "transparent",
                        color: sorting === "desc" ? "#fff" : "#6fd649",
                        border: "1px solid #6fd649",
                        '&:hover': {
                            backgroundColor: sorting === "desc" ? "#6fd649" : "transparent",
                        },
                        '&:active': {
                            backgroundColor: sorting === "desc" ? "#3e8e41" : "transparent",
                        },
                    }}
                >
                    Price Desc
                </Button>

            </div>


            <Divider />


            <div style={{
                display: "grid",
                gridTemplateColumns: "repeat(auto-fill, minmax(250px, 1fr))",
                gap: "16px",
                marginTop: "16px"
            }}>
                {products && products.map(item => (
                    <ProductPreview key={item.productId} item={item} />
                ))}
            </div>
            <div className="flex justify-center space-x-4 mt-4">
                <Button
                    variant="contained"
                    disabled={page === 1}
                    onClick={() => setPage((prev) => prev - 1)}
                    sx={{
                        backgroundColor: '#6fd649',
                        color: '#fff',
                        '&:hover': {
                            backgroundColor: '#4caf50',
                        },
                        '&:active': {
                            backgroundColor: '#3e8e41',
                        },
                        minWidth: '40px'
                    }}
                >
                    <ChevronLeftIcon />
                </Button>


                <span className="self-center text-sm">Page {page} of {totalPages}</span>

                <Button
                    variant="contained"
                    disabled={page === totalPages}
                    onClick={() => setPage((prev) => prev + 1)}
                    sx={{
                        backgroundColor: '#6fd649',
                        color: '#fff',
                        '&:hover': {
                            backgroundColor: '#4caf50', // giữ nguyên khi hover
                        },
                        '&:active': {
                            backgroundColor: '#3e8e41', // hiệu ứng nhấn
                        },
                        minWidth: '40px'
                    }}
                >
                    <ChevronRightIcon />
                </Button>
            </div>

        </div>
    );
}