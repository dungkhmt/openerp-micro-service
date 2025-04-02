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
// Mock data
const mockProducts = [
    { id: 1, title: "Tủ lạnh siêu hiện đại siêu xịn", category: "book", price: 20000, quantity: 50, image: "/demo.jpg" },
    { id: 2, title: "CD B", category: "cd", price: 15000, quantity: 30, image: "/demo.jpg" },
    { id: 3, title: "DVD C", category: "dvd", price: 30000, quantity: 20, image: "/demo.jpg" },
    { id: 4, title: "LP D", category: "lp", price: 40000, quantity: 10, image: "/demo.jpg" },
    { id: 5, title: "Book A1", category: "book", price: 20000, quantity: 50, image: "/demo.jpg" },
    { id: 6, title: "CD B1", category: "cd", price: 15000, quantity: 30, image: "/demo.jpg" },
    { id: 7, title: "DVD C1", category: "dvd", price: 30000, quantity: 20, image: "/demo.jpg" },
    { id: 8, title: "LP D1", category: "lp", price: 40000, quantity: 10, image: "/demo.jpg" }
];



export default function Home() {
    const [products, setProducts] = useState(mockProducts);
    const [sorting, setSorting] = useState("");
    const [search, setSearch] = useState("");
    const [categoryId, setCategoryId] = useState("All");
    const { open } = useSidebar();
    const [currentPage, setCurrentPage] = useState(1);
    const itemsPerPage = open ? 3 : 4;

    useEffect(() => {
        setCurrentPage(1);
    }, [open]);

    const breadcrumbPaths = [
        { label: "Home", link: "/" },
        { label: "Products", link: "/products" }

    ];

    const filteredItems = products
        .filter(item =>
            (categoryId === "All" || item.category === categoryId) &&
            (!search || item.title.toLowerCase().includes(search.toLowerCase()))
        )
        .sort((a, b) => {
            if (sorting === "asc") return a.price - b.price;
            if (sorting === "desc") return b.price - a.price;
            return a.id - b.id;
        });


    const totalPages = Math.ceil(filteredItems.length / itemsPerPage);
    const startIndex = (currentPage - 1) * itemsPerPage;
    const displayedItems = filteredItems.slice(startIndex, startIndex + itemsPerPage);

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
                        <MenuItem value="All">All categories</MenuItem>
                        <MenuItem value="book">Book</MenuItem>
                        <MenuItem value="cd">CD</MenuItem>
                        <MenuItem value="dvd">DVD</MenuItem>
                        <MenuItem value="lp">LP</MenuItem>
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
                {displayedItems.map(item => (
                    <ProductPreview key={item.id} item={item} />
                ))}
            </div>
            <div className="flex justify-center space-x-4 mt-4">
                <Button
                    variant="contained"
                    disabled={currentPage === 1}
                    onClick={() => setCurrentPage((prev) => prev - 1)}
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

                <span className="self-center text-sm">Page {currentPage} of {totalPages}</span>

                <Button
                    variant="contained"
                    disabled={currentPage === totalPages}
                    onClick={() => setCurrentPage((prev) => prev + 1)}
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