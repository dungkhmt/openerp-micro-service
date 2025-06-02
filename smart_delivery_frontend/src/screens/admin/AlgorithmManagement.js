import React, { Fragment, useState, useEffect } from 'react';
import { useHistory } from "react-router-dom";
import { Button, Box, Grid, FormControl, InputLabel, Select, MenuItem, Typography, CircularProgress, Card, CardContent } from '@mui/material';
import { request } from 'api';
import { errorNoti, successNoti } from 'utils/notification';
import useStyles from 'screens/styles.js';
import LoadingScreen from 'components/common/loading/loading.js';
import SettingsIcon from '@mui/icons-material/Settings';
import CompareArrowsIcon from '@mui/icons-material/CompareArrows';
import { API_PATH } from '../apiPaths.js';

const AlgorithmManagement = () => {
    const classes = useStyles();
    const [selectedAlgorithm, setSelectedAlgorithm] = useState('');
    const [currentAlgorithm, setCurrentAlgorithm] = useState('');
    const [isLoading, setLoading] = useState(true);
    const [isChanging, setIsChanging] = useState(false);
    const history = useHistory();

    // Algorithm information
    const algorithmInfo = {
        'GreedyStrategy': {
            name: 'Giải thuật Tham lam',
            description: 'Thuật toán đơn giản và nhanh chóng gán từng đơn hàng cho nhân viên gần nhất. Phù hợp với phân phối quy mô nhỏ nhưng có thể không luôn đưa ra giải pháp tối ưu nhất.',
            bestFor: 'Phân công nhanh, dữ liệu nhỏ, phân phối thời gian thực',
            complexity: 'Thấp',
            timeComplexity: 'O(n)'
        },
        'GAStrategy': {
            name: 'Giải thuật Di truyền (GA)',
            description: 'Thuật toán tiến hóa tối ưu hóa phân phối đơn hàng bằng cách mô phỏng quá trình chọn lọc tự nhiên. Tạo ra các tuyến đường tốt hơn qua nhiều thế hệ và có thể tìm thấy các giải pháp tối ưu hơn.',
            bestFor: 'Bài toán phân phối phức tạp, dữ liệu lớn, tối ưu hóa nhiều ràng buộc',
            complexity: 'Cao',
            timeComplexity: 'O(n * g)',
            note: 'Trong đó g là số thế hệ (hiện tại là 50)'
        }
    };

    // Fetch current algorithm on component mount
    useEffect(() => {
        const fetchCurrentAlgorithm = async () => {
            try {
                // In a real application, you would have an endpoint to get the current algorithm
                await request(
                    "get",
                    `${API_PATH.ADMIN}/current-strategy`,
                    (res) => {
                        setCurrentAlgorithm(res.data);
                        setSelectedAlgorithm(res.data);
                    },
                    {
                        401: () => { },
                        503: () => {
                            // If API doesn't exist yet, set default values
                            setCurrentAlgorithm('GreedyStrategy');
                            setSelectedAlgorithm('GreedyStrategy');
                            errorNoti("Có lỗi khi tải thông tin thuật toán")
                        }
                    }
                );
            } catch (error) {
                console.error('Failed to fetch current algorithm:', error);
                // Fallback to a default value
                setCurrentAlgorithm('GreedyStrategy');
                setSelectedAlgorithm('GreedyStrategy');
            } finally {
                setLoading(false);
            }
        };

        fetchCurrentAlgorithm();
    }, []);

    const handleAlgorithmChange = (event) => {
        setSelectedAlgorithm(event.target.value);
    };

    const handleApplyAlgorithm = async () => {
        if (!selectedAlgorithm || selectedAlgorithm === currentAlgorithm) return;

        setIsChanging(true);
        try {
            await request(
                "post",
                `${API_PATH.ADMIN}/change-strategy`,
                (res) => {
                    setCurrentAlgorithm(selectedAlgorithm);
                    successNoti(`Đã thay đổi thuật toán thành ${algorithmInfo[selectedAlgorithm]?.name || selectedAlgorithm}`);
                },
                {
                    401: () => { errorNoti("Không có quyền thay đổi thuật toán") },
                    400: (e) => { errorNoti("Có lỗi xảy ra. Vui lòng thử lại sau") }
                },
                null,
                { params: { strategy: selectedAlgorithm } }
            );
        } catch (error) {
            console.error('Failed to change algorithm:', error);
            errorNoti(`Lỗi khi thay đổi thuật toán: ${error.message}`);
        } finally {
            setIsChanging(false);
        }
    };

    const renderHeader = () => {
        return (
            <Grid container justifyContent="space-between" className={classes.headerBox}>
                <Grid>
                    <Typography variant="h5">
                        <SettingsIcon style={{ marginRight: '8px', verticalAlign: 'middle' }}/>
                        Quản lý Thuật toán Phân phối
                    </Typography>
                </Grid>

            </Grid>
        );
    };

    return (
        isLoading ? <LoadingScreen /> :
            <Fragment>
                <Box className={classes.warehousePage || ''}>
                    {renderHeader()}
                    <Box className={classes.bodyBox || ''}>
                        <Grid container spacing={3}>
                            {/* Current settings panel */}
                            <Grid item xs={12} md={6}>
                                <Card elevation={3}>
                                    <CardContent>
                                        <Typography variant="h6" gutterBottom>
                                            Cấu hình hiện tại
                                        </Typography>
                                        <Typography variant="subtitle1" gutterBottom>
                                            Thuật toán đang hoạt động: {algorithmInfo[currentAlgorithm]?.name || currentAlgorithm}
                                        </Typography>

                                        <Box sx={{ mt: 3, mb: 2 }}>
                                            <FormControl fullWidth variant="outlined">
                                                <InputLabel id="algorithm-select-label">Chọn thuật toán</InputLabel>
                                                <Select
                                                    labelId="algorithm-select-label"
                                                    id="algorithm-select"
                                                    value={selectedAlgorithm}
                                                    label="Chọn thuật toán"
                                                    onChange={handleAlgorithmChange}
                                                >
                                                    <MenuItem value="GreedyStrategy">Giải thuật Tham lam</MenuItem>
                                                    <MenuItem value="GAStrategy">Giải thuật Di truyền (GA)</MenuItem>
                                                </Select>
                                            </FormControl>
                                        </Box>

                                        <Box sx={{ mt: 2, display: 'flex', justifyContent: 'center' }}>
                                            <Button
                                                variant="contained"
                                                color="primary"
                                                onClick={handleApplyAlgorithm}
                                                disabled={isChanging || selectedAlgorithm === currentAlgorithm}
                                                startIcon={isChanging ? <CircularProgress size={24} color="inherit" /> : <CompareArrowsIcon />}
                                                sx={{ minWidth: 150 }}
                                            >
                                                {isChanging ? 'Đang thay đổi...' : 'Áp dụng thuật toán'}
                                            </Button>
                                        </Box>
                                    </CardContent>
                                </Card>
                            </Grid>

                            {/* Algorithm info panel */}
                            <Grid item xs={12} md={6}>
                                <Card elevation={3}>
                                    <CardContent>
                                        <Typography variant="h6" gutterBottom>
                                            Thông tin thuật toán
                                        </Typography>

                                        {selectedAlgorithm && (
                                            <Box>
                                                <Typography variant="h6" gutterBottom>
                                                    {algorithmInfo[selectedAlgorithm]?.name || selectedAlgorithm}
                                                </Typography>
                                                <Typography variant="body1" paragraph>
                                                    {algorithmInfo[selectedAlgorithm]?.description || 'Không có mô tả.'}
                                                </Typography>

                                                <Box sx={{ p: 2, bgcolor: '#f5f5f5', borderRadius: 1, mb: 2 }}>
                                                    <Typography variant="subtitle2" color="primary" gutterBottom>
                                                        Sử dụng tốt nhất cho:
                                                    </Typography>
                                                    <Typography variant="body2" paragraph>
                                                        {algorithmInfo[selectedAlgorithm]?.bestFor || 'Mục đích chung'}
                                                    </Typography>

                                                    <Typography variant="subtitle2" color="primary" gutterBottom>
                                                        Độ phức tạp:
                                                    </Typography>
                                                    <Typography variant="body2">
                                                        {algorithmInfo[selectedAlgorithm]?.complexity || 'Không xác định'}
                                                        {algorithmInfo[selectedAlgorithm]?.timeComplexity &&
                                                            ` (${algorithmInfo[selectedAlgorithm].timeComplexity})`}
                                                    </Typography>

                                                    {algorithmInfo[selectedAlgorithm]?.note && (
                                                        <Typography variant="body2" sx={{ mt: 1, fontStyle: 'italic' }}>
                                                            Ghi chú: {algorithmInfo[selectedAlgorithm].note}
                                                        </Typography>
                                                    )}
                                                </Box>
                                            </Box>
                                        )}
                                    </CardContent>
                                </Card>
                            </Grid>


                        </Grid>
                    </Box>
                </Box>
            </Fragment>
    );
};

export default AlgorithmManagement;