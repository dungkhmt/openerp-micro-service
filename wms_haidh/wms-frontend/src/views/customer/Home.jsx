import React, { useEffect, useState } from "react";
import { TextField, MenuItem, Select, FormControl, InputLabel, InputAdornment } from "@mui/material";
import Button from "@mui/material/Button";
import Divider from "@mui/material/Divider";
import SearchIcon from '@mui/icons-material/Search';
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
                    InputProps={{
                        startAdornment: (
                            <InputAdornment position="start">
                                <SearchIcon />
                            </InputAdornment>
                        ),
                        sx: {
                            height: "48px",
                            paddingLeft: "16px"
                        },
                    }}
                />

                <FormControl variant="outlined" sx={{ minWidth: 200, height: "48px" }}>
                    <InputLabel>Category</InputLabel>
                    <Select
                        value={categoryId}
                        onChange={(e) => setCategoryId(e.target.value)}
                        label="Category"
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
                    variant={sorting === "asc" ? "contained" : "outlined"}
                    onClick={() => setSorting("asc")}
                    sx={{
                        height: "48px",
                        backgroundColor: sorting === "asc" ? "black" : "transparent",
                        color: sorting === "asc" ? "white" : "black",
                        border: "1px solid black",
                        "&:hover": {
                            backgroundColor: sorting === "asc" ? "black" : "rgba(0, 0, 0, 0.1)",
                            opacity: sorting === "asc" ? 0.75 : 1,
                            borderColor: "black",
                        },
                    }}
                >
                    Price Asc
                </Button>

                <Button
                    variant={sorting === "desc" ? "contained" : "outlined"}
                    onClick={() => setSorting("desc")}
                    sx={{
                        height: "48px",
                        backgroundColor: sorting === "desc" ? "black" : "transparent",
                        color: sorting === "desc" ? "white" : "black",
                        border: "1px solid black",
                        "&:hover": {
                            backgroundColor: sorting === "desc" ? "black" : "rgba(0, 0, 0, 0.1)",
                            opacity: sorting === "desc" ? 0.75 : 1,
                            borderColor: "black",
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
                        backgroundColor: 'black',
                        color: 'white',
                        '&:hover': {
                            backgroundColor: 'black',
                            opacity: 0.75,
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
                        backgroundColor: 'black',
                        color: 'white',
                        '&:hover': {
                            backgroundColor: 'black',
                            opacity: 0.75,
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