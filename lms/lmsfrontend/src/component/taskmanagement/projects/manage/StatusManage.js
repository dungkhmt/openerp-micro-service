import React, {useEffect, useState} from "react";
import {
  Box,
  Button,
  Grid,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
  Typography
} from '@mui/material';
import ControlPointIcon from '@mui/icons-material/ControlPoint';
import {request} from '../../../../api';
import {successNoti} from "utils/notification";

const CategoryManage = () => {
    const [categoryId, setCategoryId] = useState("");
    const [categoryName, setCategoryName] = useState("");
    const [isAddNewCategory, setIsAddNewCategory] = useState(false);
    const [isLoadTableCategory, setIsLoadTableCategory] = useState(false);
    const [categories, setCategories] = useState([]);
    useEffect(() => {
        request('get', '/task-categories',
            res => {
                setCategories(res.data);
            }, err => {
                console.log(err);
            });
    }, [isLoadTableCategory]);

    const handleSubmitCategory = () => {
        const dataForm = {
            categoryId: categoryId,
            categoryName: categoryName
        }
        request(
            'post',
            '/task-categories',
            res => {
                successNoti("Đã thêm mới danh mục thành công!", true);
                setIsLoadTableCategory(!isLoadTableCategory);
                setCategoryId("");
                setCategoryName("");
            },
            err => {
                console.log(err);
            },
            dataForm);
    }

    const handleDeleteCategory = (categoryId) => {
        request(
            'delete',
            `task-category/${categoryId}`,
            res => {
                successNoti("Đã xóa thành công danh mục", true);
                setIsLoadTableCategory(!isLoadTableCategory);
            },
            err => {
                console.log(err);
            });
    }

    return (
        <>
            <Box mb={5}>
                <Typography>
                    QUẢN LÝ TRẠNG THÁI
                </Typography>
            </Box>
            <Box mb={3}>
                <Button variant="contained" onClick={() => setIsAddNewCategory(!isAddNewCategory)}>
                    <ControlPointIcon /> Thêm danh mục
                </Button>
            </Box>
            {isAddNewCategory &&
                <Box mb={3}>
                    <Grid container sx={{ mb: 3 }}>
                        <Grid item={true} xs={6}>
                            <TextField
                                fullWidth={true}
                                label="Id danh mục"
                                variant="outlined"
                                value={categoryId}
                                onChange={(e) => setCategoryId(e.target.value)}
                            />
                        </Grid>
                    </Grid>
                    <Grid container sx={{ mb: 3 }}>
                        <Grid item={true} xs={6}>
                            <TextField
                                fullWidth={true}
                                label="Tên danh mục"
                                variant="outlined"
                                value={categoryName}
                                onChange={(e) => setCategoryName(e.target.value)}
                            />
                        </Grid>
                    </Grid>
                    <Box>
                        <Button color="success" variant="contained" onClick={handleSubmitCategory}>
                            Submit
                        </Button>
                    </Box>
                </Box>
            }
            <Box mb={3}>
                <Typography>
                    DANH SÁCH TRẠNG THÁI
                </Typography>
            </Box>
            <Box>
                <TableContainer component={Paper}>
                    <Table sx={{ minWidth: 650 }} size="small" aria-label="a dense table">
                        <TableHead>
                            <TableRow>
                                <TableCell>Id danh mục</TableCell>
                                <TableCell align="right">Tên danh mục</TableCell>
                                <TableCell align="right">Xóa</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {categories.map((row) => (
                                <TableRow
                                    key={row.categoryId}
                                    sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
                                >
                                    <TableCell component="th" scope="row">
                                        {row.categoryId}
                                    </TableCell>
                                    <TableCell align="right">{row.categoryName}</TableCell>
                                    <TableCell align="right">
                                        <Button onClick={() => handleDeleteCategory(row.categoryId)}>Delete</Button>
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
            </Box>
        </>
    );
}

export default CategoryManage;