import {useParams, useLocation} from "react-router-dom";
import React, {Fragment, useEffect, useState} from "react";
import {request} from "../../api";
import {API_PATH} from "../apiPaths";
import {errorNoti, successNoti} from "../../utils/notification";
import LoadingScreen from "../../components/common/loading/loading";
import {Box, Button, Checkbox, FormControlLabel, Grid, MenuItem, Modal, TextField, Typography, Dialog, DialogTitle, DialogContent, DialogActions, List, ListItem, ListItemText, Chip} from "@mui/material";
import Maps from "../../components/map/map";
import SearchBox from "../../components/map/searchBox";
import MapIcon from "@mui/icons-material/Map";
import HistoryIcon from "@mui/icons-material/History";
import useStyles from "./CreateOrder.style";
import {useForm} from "react-hook-form";

const UpdateOrder = () =>{
    const { id: orderId } = useParams();
    const location = useLocation();
    const [order, setOrder] = useState();
    const [isLoading, setLoading] = useState(true);
    const [openModal, setOpenModal] = useState(false);
    const [openHistoryDialog, setOpenHistoryDialog] = useState(false);
    const [orderHistory, setOrderHistory] = useState([]);
    const [isLoadingHistory, setIsLoadingHistory] = useState(false);
    const classes = useStyles();
    const [selectedField, setSelectedField] = useState(null);
    const [selectPosition, setSelectPosition] = useState(null);
    const { register, errors, handleSubmit, watch, getValues } = useForm();

    // Check if URL contains 'view' to determine view mode
    const isViewMode = location.pathname.toLowerCase().includes('view');

    const [formData, setFormData] = useState({
        senderName: '',
        senderPhone: '',
        senderEmail: '123@gmail.com',
        senderAddress: '',
        senderLongitude: '',
        senderLatitude: '',
        recipientName: '',
        recipientPhone: '',
        recipientEmail: '1234@gmail.com',
        recipientAddress: '',
        recipientLongitude: '',
        recipientLatitude : ' ',
        items: [{ productId: '', name: '', quantity: 1, weight: 0, price: 0, length:0, width:0, height:0, viewBeforeReceive:false }],
        totalprice: 0,
        orderType: '', // Thêm trường orderType
    });

    useEffect(() => {
        const fetchData = async () => {
            await request(
                "get",
                `${API_PATH.ORDER}/${orderId}`,
                (res) => {
                    setOrder(res.data);
                    setFormData(res.data);
                    setFormData((prevState) => ({
                        ...prevState,
                        senderName: res.data?.senderName,
                        senderPhone: res.data?.senderPhone,
                        senderEmail: res.data?.senderEmail,
                        senderAddress: res.data?.senderAddress,
                        recipientName: res.data?.recipientName,
                        recipientPhone: res.data?.recipienPhone,
                        recipientEmail: res.data?.recipientEmail,
                        recipientAddress: res.data?.recipientAddress,
                    }));

                    console.log("res",res.data);
                }, {
                    401: () => {
                    },
                    503: () => {
                        errorNoti("Có lỗi khi tải dữ liệu của kho")
                    }
                }
            );
            setLoading(false);
        }
        if(orderId){
            fetchData();
        }
        else {
            setLoading(true);
        }
    },[orderId])

    useEffect(() => {
        setLoading(false); // Giả lập quá trình tải dữ liệu
    }, []);

    const handleInputChange = (e, index = null, fieldName = null) => {
        // Prevent changes in view mode
        if (isViewMode) return;

        const { name, value } = e.target;
        if (name.startsWith('items') && index !== null && fieldName !== null) {
            const updatedItems = [...formData.items];
            updatedItems[index][fieldName] = value;
            setFormData({ ...formData, items: updatedItems });
        } else {
            setFormData({ ...formData, [name]: value });
        }
    };

    const handleCheckboxChange = (index) => (event) => {
        // Prevent changes in view mode
        if (isViewMode) return;

        const updatedItems = [...formData.items];
        updatedItems[index].viewBeforeReceive = event.target.checked;
        setFormData({ ...formData, items: updatedItems });
    };

    // Hàm xử lý khi submit form
    const onsubmit = async (e) => {
        // Prevent submission in view mode
        if (isViewMode) return;

        console.log("Submitting data:", formData); // Log dữ liệu để kiểm tra
        request(
            "put",
            `/smdeli/ordermanager/order/update/${orderId}`,
            (res) => {
                successNoti('Cập nhật đơn hàng thành công!');
            },
            {
                401: () => { errorNoti("Có lỗi xảy ra. Vui lòng thử lại sau") },
                400: (e) => { errorNoti("Có lỗi xảy ra. Vui lòng thử lại sau") }
            },
            formData
        );
    };

    // Hàm thêm sản phẩm mới
    const addItem = () => {
        // Prevent changes in view mode
        if (isViewMode) return;

        setFormData({
            ...formData,
            items: [...formData.items, { productId: '', name: '', quantity: 1, weight: 0, price: 0, length:0, width:0, height:0, viewBeforeReceive:false }]
        });
    };

    // Hàm xóa sản phẩm
    const removeItem = (index) => {
        // Prevent changes in view mode
        if (isViewMode) return;

        const updatedItems = formData.items.filter((_, i) => i !== index);
        setFormData({ ...formData, items: updatedItems });
    };

    // Function to fetch order history
    const fetchOrderHistory = async () => {
        setIsLoadingHistory(true);
        try {
            await request(
                "get",
                `${API_PATH.ORDER}/${orderId}/history`,
                (res) => {
                    setOrderHistory(res.data);
                    setOpenHistoryDialog(true);
                },
                {
                    401: () => {
                        errorNoti("Không có quyền truy cập lịch sử đơn hàng");
                    },
                    404: () => {
                        errorNoti("Không tìm thấy lịch sử đơn hàng");
                    },
                    500: () => {
                        errorNoti("Có lỗi khi tải lịch sử đơn hàng");
                    }
                }
            );
        } catch (error) {
            errorNoti("Có lỗi khi tải lịch sử đơn hàng");
        } finally {
            setIsLoadingHistory(false);
        }
    };

    // Function to format status for display
    const formatStatus = (status) => {
        const statusMap = {
            'PENDING': 'Đang xử lý',
            'ASSIGNED': 'Đã phân công lấy hàng',
            'COLLECTED_COLLECTOR': 'Đã lấy hàng',
            'COLLECTED_HUB': 'Đã về hub nguồn',
            'CONFIRMED_IN': 'Đã về hub',
            'CONFIRMED_OUT': 'Đã xác nhận xuất hub nguồn',
            'DELIVERING': 'Đang vận chuyển đến hub đích',
            'DELIVERED': 'Đã giao đến hub đích',
            'CONFIRMED_IN_FINAL_HUB': 'Đã xác nhận nhập hub đích',
            'ASSIGNED_SHIPPER': 'Đã phân công giao hàng',
            'SHIPPING': 'Đang giao hàng',
            'SHIPPED': 'Đã giao hàng',
            'COMPLETED': 'Giao hàng thành công',
            'CANCELLED': 'Đã hủy',
            'DELIVERED_FAILED': 'Giao hàng đến hub thất bại',
            'SHIPPED_FAILED': 'Shipper giao thất bại'
        };
        return statusMap[status] || status;
    };

    // Function to get status color
    const getStatusColor = (status) => {
        switch (status) {
            case 'PENDING':
                return 'default';
            case 'ASSIGNED':
            case 'COLLECTED_COLLECTOR':
            case 'COLLECTED_HUB':
            case 'CONFIRMED_OUT':
                return 'primary';
            case 'DELIVERING':
            case 'DELIVERED':
            case 'CONFIRMED_IN_FINAL_HUB':
            case 'ASSIGNED_SHIPPER':
            case 'SHIPPING':
            case 'SHIPPED':
                return 'info';
            case 'COMPLETED':
                return 'success';
            case 'CANCELLED':
            case 'DELIVERED_FAILED':
            case 'SHIPPED_FAILED':
                return 'error';
            default:
                return 'default';
        }
    };

    useEffect(() => {
        // Tính tổng tiền từ các mặt hàng
        const total = formData.items.reduce((acc, item) => {
            return acc + (item.quantity * item.price);
        }, 0);

        // Cập nhật formData với giá trị tổng tiền mới
        setFormData(prevData => ({
            ...prevData,
            totalprice: total
        }));
    }, [formData.items]); // Mỗi khi items thay đổi, tính lại totalprice

    return (
        isLoading ? (
            <LoadingScreen />
        ) : (
            <Fragment>
                <Modal open={openModal && !isViewMode}
                       onClose={() => setOpenModal(!openModal)}
                       aria-labelledby="modal-modal-title"
                       aria-describedby="modal-modal-description"
                >
                    <Box sx={{
                        position: 'absolute',
                        top: '50%',
                        left: '50%',
                        width: '75%',
                        height: '90%',
                        transform: 'translate(-50%, -50%)',
                        bgcolor: 'background.paper',
                        border: '2px solid #000',
                        boxShadow: 24,
                        p: 4,
                    }}>
                        <Typography variant="h5">
                            Chọn vị trí
                            <Button
                                variant="contained"
                                className={classes.addButton}
                                type="submit"
                                onClick={() => {
                                    setOpenModal(false);
                                    if (selectedField === 'sender') {
                                        setFormData({
                                            ...formData,
                                            senderLongitude: selectPosition?.lon,
                                            senderLatitude: selectPosition?.lat,
                                            senderAddress: selectPosition?.display_name // Cập nhật địa chỉ người gửi
                                        });
                                    } else if (selectedField === 'recipient') {
                                        setFormData({
                                            ...formData,
                                            recipientLongitude: selectPosition?.lon,
                                            recipientLatitude: selectPosition?.lat,
                                            recipientAddress: selectPosition?.display_name // Cập nhật địa chỉ người nhận
                                        });
                                    }
                                }}
                            >
                                Lưu
                            </Button>
                        </Typography>

                        <div style={{
                            display: "flex",
                            flexDirection: "row",
                            width: "100%",
                            height: "100%",
                        }}>
                            <div style={{ width: "50%", height: "90%", marginRight: 10 }}>
                                <Maps selectPosition={selectPosition}
                                      setSelectPosition={setSelectPosition} />
                            </div>
                            <div style={{ width: "50%", height: "90%" }}>
                                <SearchBox selectPosition={selectPosition}
                                           setSelectPosition={setSelectPosition} />
                            </div>
                        </div>
                    </Box>
                </Modal>
                <Box className={classes.warehousePage} >
                    <Grid container justifyContent="space-between"
                          className={classes.headerBox} >
                        <Grid>
                            <Typography variant="h5">
                                {isViewMode ? "Xem chi tiết đơn hàng" : "Cập nhật đơn hàng"}
                            </Typography>
                        </Grid>
                        {!isViewMode && (
                            <Grid className={classes.buttonWrap}>
                                <Button variant="contained" className={classes.addButton}
                                        type="submit" onClick={handleSubmit(onsubmit)} >Lưu</Button>
                            </Grid>
                        )}
                        {/* History button - visible in both view and edit mode */}
                        <Grid className={classes.buttonWrap}>
                            <Button
                                variant="outlined"
                                startIcon={<HistoryIcon />}
                                onClick={fetchOrderHistory}
                                disabled={isLoadingHistory}
                                style={{ marginLeft: 8 }}
                            >
                                {isLoadingHistory ? "Đang tải..." : "Lịch sử đơn hàng"}
                            </Button>
                        </Grid>
                    </Grid>
                </Box>
                <Box className={classes.bodyBox}>
                    <Box className={classes.formWrap}
                         component="form"
                         onSubmit={handleSubmit(onsubmit)}>

                        {/* Thông tin chung kho */}
                        <Box >
                            <Grid container spacing={2}>
                                <Grid item xs={6.5}>
                                    <Box className={classes.boxInfor}>
                                        <Typography className={classes.inforTitle} variant="h6">
                                            1. Thông tin cơ bản
                                        </Typography>
                                        <Grid container spacing={3} className={classes.inforWrap}>
                                            <Grid item xs={6}>
                                                <Box className={classes.inputWrap}>
                                                    <Box className={classes.labelInput}>
                                                        Người gửi
                                                    </Box>
                                                    <TextField
                                                        fullWidth
                                                        variant="outlined"
                                                        size="small"
                                                        inputRef={register({ required: "Vui lòng điền tên người gửi" })}
                                                        name="senderName"
                                                        error={!!errors.senderName}
                                                        helperText={errors.senderName?.message}
                                                        value={formData.senderName}
                                                        onChange={(e) => handleInputChange(e)}
                                                        disabled={isViewMode}
                                                        InputProps={{
                                                            readOnly: isViewMode,
                                                        }}
                                                    />
                                                </Box>
                                            </Grid>
                                            <Grid item xs={6}>
                                                <Box className={classes.inputWrap}>
                                                    <Box className={classes.labelInput}>
                                                        Số điện thoại
                                                    </Box>
                                                    <TextField
                                                        fullWidth
                                                        variant="outlined"
                                                        size="small"
                                                        inputRef={register({ required: "Vui lòng điền số điện thoại người gửi" })}
                                                        name="senderPhone"
                                                        error={!!errors.senderPhone}
                                                        helperText={errors.senderPhone?.message}
                                                        value={formData.senderPhone}
                                                        onChange={(e) => handleInputChange(e)}
                                                        disabled={isViewMode}
                                                        InputProps={{
                                                            readOnly: isViewMode,
                                                        }}
                                                    />
                                                </Box>
                                            </Grid>
                                            <Grid item xs={12}>
                                                <Box className={classes.inputWrap}>
                                                    <Box className={classes.labelInput}>
                                                        Địa chỉ {!isViewMode && <Button style={{ "margin-bottom": 0 }}
                                                                                        onClick={() => {setOpenModal(!openModal);
                                                                                            setSelectedField('sender')}}><MapIcon /></Button>}
                                                    </Box>
                                                    <TextField
                                                        fullWidth
                                                        variant="outlined"
                                                        size="small"
                                                        inputRef={register({ required: "Vui lòng điền địa chỉ người gửi" })}
                                                        name="senderAddress"
                                                        error={!!errors.senderAddress}
                                                        helperText={errors.senderAddress?.message}
                                                        value={formData.senderAddress}
                                                        onClick={!isViewMode ? () => setSelectedField('sender') : undefined}
                                                        onChange={(e) => handleInputChange(e)}
                                                        disabled={isViewMode}
                                                        InputProps={{
                                                            readOnly: isViewMode,
                                                        }}
                                                    />
                                                </Box>
                                            </Grid>
                                        </Grid>
                                        <Grid container spacing={3} className={classes.inforWrap}>
                                            <Grid item xs={6}>
                                                <Box className={classes.inputWrap}>
                                                    <Box className={classes.labelInput}>
                                                        Người nhận
                                                    </Box>
                                                    <TextField
                                                        fullWidth
                                                        variant="outlined"
                                                        size="small"
                                                        inputRef={register({ required: "Vui lòng điền tên người nhận" })}
                                                        name="recipientName"
                                                        error={!!errors.recipientName}
                                                        helperText={errors.recipientName?.message}
                                                        value={formData.recipientName}
                                                        onChange={(e) => handleInputChange(e)}
                                                        disabled={isViewMode}
                                                        InputProps={{
                                                            readOnly: isViewMode,
                                                        }}
                                                    />
                                                </Box>
                                            </Grid>
                                            <Grid item xs={6}>
                                                <Box className={classes.inputWrap}>
                                                    <Box className={classes.labelInput}>
                                                        Số điện thoại
                                                    </Box>
                                                    <TextField
                                                        fullWidth
                                                        variant="outlined"
                                                        size="small"
                                                        inputRef={register({ required: "Vui lòng điền số điện thoại người nhận" })}
                                                        name="recipientPhone"
                                                        error={!!errors.recipientPhone}
                                                        helperText={errors.recipientPhone?.message}
                                                        value={formData.recipientPhone}
                                                        onChange={(e) => handleInputChange(e)}
                                                        disabled={isViewMode}
                                                        InputProps={{
                                                            readOnly: isViewMode,
                                                        }}
                                                    />
                                                </Box>
                                            </Grid>
                                            <Grid item xs={12}>
                                                <Box className={classes.inputWrap}>
                                                    <Box className={classes.labelInput}>
                                                        Địa chỉ {!isViewMode && <Button style={{ "margin-bottom": 0 }}
                                                                                        onClick={() => {setOpenModal(!openModal);
                                                                                            setSelectedField('recipient')}}><MapIcon /></Button>}
                                                    </Box>
                                                    <TextField
                                                        fullWidth
                                                        variant="outlined"
                                                        size="small"
                                                        inputRef={register({ required: "Vui lòng điền địa chỉ người nhận" })}
                                                        name="recipientAddress"
                                                        error={!!errors.recipientAddress}
                                                        helperText={errors.recipientAddress?.message}
                                                        value={formData.recipientAddress}
                                                        onClick={!isViewMode ? () => setSelectedField('recipient') : undefined}
                                                        onChange={(e) => handleInputChange(e)}
                                                        disabled={isViewMode}
                                                        InputProps={{
                                                            readOnly: isViewMode,
                                                        }}
                                                    />
                                                </Box>
                                            </Grid>
                                            <Grid item xs={6}>
                                                <Box className={classes.inputWrap}>
                                                    <Box className={classes.labelInput}>
                                                        Dịch vụ chuyển phát
                                                    </Box>
                                                    <TextField
                                                        select={!isViewMode} // Only select if not in view mode
                                                        fullWidth
                                                        variant="outlined"
                                                        size="small"
                                                        value={formData.orderType}
                                                        onChange={!isViewMode ? (e) => setFormData({ ...formData, orderType: e.target.value }) : undefined}
                                                        disabled={isViewMode}
                                                        InputProps={{
                                                            readOnly: isViewMode,
                                                        }}
                                                    >
                                                        {!isViewMode && [
                                                            <MenuItem key="normal" value="Bình thường">Bình thường</MenuItem>,
                                                            <MenuItem key="fast" value="Nhanh">Nhanh</MenuItem>,
                                                            <MenuItem key="express" value="Hỏa tốc">Hỏa tốc</MenuItem>
                                                        ]}
                                                    </TextField>
                                                </Box>
                                            </Grid>
                                            <Grid item xs={6}>
                                                <Box className={classes.inputWrap}>
                                                    <Box className={classes.labelInput}>
                                                        Tiền thu hộ
                                                    </Box>
                                                    <TextField
                                                        fullWidth
                                                        variant="outlined"
                                                        size="small"
                                                        inputRef={register({ required: "Vui lòng điền" })}
                                                        name="totalprice"
                                                        error={!!errors.totalprice}
                                                        helperText={errors.totalprice?.message}
                                                        value={formData.totalprice}
                                                        onChange={(e) => handleInputChange(e)}
                                                        disabled={isViewMode}
                                                        InputProps={{
                                                            readOnly: isViewMode,
                                                        }}
                                                    />
                                                </Box>
                                            </Grid>
                                        </Grid>
                                    </Box>
                                </Grid>
                                <Grid item xs={5}>
                                    <Box className={classes.boxInfor}>
                                        <Typography className={classes.inforTitle} variant="h6">
                                            2. Hàng hóa
                                        </Typography>
                                        <Grid container spacing={3} className={classes.inforWrap}>
                                            <Grid item xs={12}>
                                                {formData.items.map((item, index) => (
                                                    <Box key={index} className={classes.itemContainer}>
                                                        <Grid container spacing={2}>
                                                            <Grid item xs={6}>
                                                                <Box className={classes.inputWrap}>
                                                                    <Box className={classes.labelInput}>
                                                                        Mã sản phẩm
                                                                    </Box>
                                                                    <TextField
                                                                        fullWidth
                                                                        variant="outlined"
                                                                        size="small"
                                                                        name={`items[${index}].productId`}
                                                                        value={item.productId}
                                                                        onChange={(e) => handleInputChange(e, index, 'productId')}
                                                                        disabled={isViewMode}
                                                                        InputProps={{
                                                                            readOnly: isViewMode,
                                                                        }}
                                                                    />
                                                                </Box>
                                                            </Grid>
                                                            <Grid item xs={6}>
                                                                <Box className={classes.inputWrap}>
                                                                    <Box className={classes.labelInput}>
                                                                        Tên sản phẩm
                                                                    </Box>
                                                                    <TextField
                                                                        fullWidth
                                                                        variant="outlined"
                                                                        size="small"
                                                                        name={`items[${index}].name`}
                                                                        value={item.name}
                                                                        onChange={(e) => handleInputChange(e, index, 'name')}
                                                                        disabled={isViewMode}
                                                                        InputProps={{
                                                                            readOnly: isViewMode,
                                                                        }}
                                                                    />
                                                                </Box>
                                                            </Grid>
                                                            <Grid item xs={12}>
                                                                <Box className={classes.inputWrap}>
                                                                    <FormControlLabel
                                                                        control={
                                                                            <Checkbox
                                                                                checked={item.viewBeforeReceive || false}
                                                                                onChange={handleCheckboxChange(index)}
                                                                                disabled={isViewMode}
                                                                            />
                                                                        }
                                                                        label="Cho khách xem trước khi nhận"
                                                                    />
                                                                </Box>
                                                            </Grid>

                                                            <Grid item xs={6}>
                                                                <Box className={classes.inputWrap}>
                                                                    <Box className={classes.labelInput}>
                                                                        Số lượng
                                                                    </Box>
                                                                    <TextField
                                                                        fullWidth
                                                                        variant="outlined"
                                                                        size="small"
                                                                        type="number"
                                                                        name={`items[${index}].quantity`}
                                                                        value={item.quantity}
                                                                        onChange={(e) => handleInputChange(e, index, 'quantity')}
                                                                        disabled={isViewMode}
                                                                        InputProps={{
                                                                            readOnly: isViewMode,
                                                                        }}
                                                                    />
                                                                </Box>
                                                            </Grid>
                                                            <Grid item xs={6}>
                                                                <Box className={classes.inputWrap}>
                                                                    <Box className={classes.labelInput}>
                                                                        Khối lượng
                                                                    </Box>
                                                                    <TextField
                                                                        fullWidth
                                                                        variant="outlined"
                                                                        size="small"
                                                                        type="number"
                                                                        name={`items[${index}].weight`}
                                                                        value={item.weight}
                                                                        onChange={(e) => handleInputChange(e, index, 'weight')}
                                                                        disabled={isViewMode}
                                                                        InputProps={{
                                                                            readOnly: isViewMode,
                                                                        }}
                                                                    />
                                                                </Box>
                                                            </Grid>
                                                            <Grid item xs={4}>
                                                                <Box className={classes.inputWrap}>
                                                                    <Box className={classes.labelInput}>
                                                                        Dài(cm)
                                                                    </Box>
                                                                    <TextField
                                                                        fullWidth
                                                                        variant="outlined"
                                                                        size="small"
                                                                        type="number"
                                                                        name={`items[${index}].length`}
                                                                        value={item.length}
                                                                        onChange={(e) => handleInputChange(e, index, 'length')}
                                                                        disabled={isViewMode}
                                                                        InputProps={{
                                                                            readOnly: isViewMode,
                                                                        }}
                                                                    />
                                                                </Box>
                                                            </Grid>
                                                            <Grid item xs={4}>
                                                                <Box className={classes.inputWrap}>
                                                                    <Box className={classes.labelInput}>
                                                                        Cao(cm)
                                                                    </Box>
                                                                    <TextField
                                                                        fullWidth
                                                                        variant="outlined"
                                                                        size="small"
                                                                        type="number"
                                                                        name={`items[${index}].height`}
                                                                        value={item.height}
                                                                        onChange={(e) => handleInputChange(e, index, 'height')}
                                                                        disabled={isViewMode}
                                                                        InputProps={{
                                                                            readOnly: isViewMode,
                                                                        }}
                                                                    />
                                                                </Box>
                                                            </Grid>
                                                            <Grid item xs={4}>
                                                                <Box className={classes.inputWrap}>
                                                                    <Box className={classes.labelInput}>
                                                                        Rộng(cm)
                                                                    </Box>
                                                                    <TextField
                                                                        fullWidth
                                                                        variant="outlined"
                                                                        size="small"
                                                                        type="number"
                                                                        name={`items[${index}].width`}
                                                                        value={item.width}
                                                                        onChange={(e) => handleInputChange(e, index, 'width')}
                                                                        disabled={isViewMode}
                                                                        InputProps={{
                                                                            readOnly: isViewMode,
                                                                        }}
                                                                    />
                                                                </Box>
                                                            </Grid>
                                                            <Grid item xs={6}>
                                                                <Box className={classes.inputWrap}>
                                                                    <Box className={classes.labelInput}>
                                                                        Giá
                                                                    </Box>
                                                                    <TextField
                                                                        fullWidth
                                                                        variant="outlined"
                                                                        size="small"
                                                                        type="number"
                                                                        step="0.01"
                                                                        name={`items[${index}].price`}
                                                                        value={item.price}
                                                                        onChange={(e) => handleInputChange(e, index, 'price')}
                                                                        disabled={isViewMode}
                                                                        InputProps={{
                                                                            readOnly: isViewMode,
                                                                        }}
                                                                    />
                                                                </Box>
                                                            </Grid>
                                                        </Grid>
                                                        <br/>
                                                        {!isViewMode && (
                                                            <Button
                                                                variant="contained"
                                                                color="secondary"
                                                                onClick={() => removeItem(index)}
                                                                className={classes.removeButton}
                                                            >
                                                                Xóa sản phẩm
                                                            </Button>
                                                        )}
                                                    </Box>
                                                ))}
                                                {!isViewMode && (
                                                    <Button
                                                        variant="contained"
                                                        color="primary"
                                                        onClick={addItem}
                                                        className={classes.addButton}
                                                    >
                                                        Thêm sản phẩm
                                                    </Button>
                                                )}
                                            </Grid>
                                        </Grid>

                                    </Box>

                                </Grid>

                            </Grid>
                            <Box className={classes.boxInfor} style={{ margin: 0 }}>
                                <Typography className={classes.inforTitle} variant="h6">
                                    Tiền phí & tiền thu hộ
                                    {!isViewMode && (
                                        <input
                                            style={{ display: 'none' }}
                                            id="raised-button-file"
                                            type="file"
                                        />
                                    )}
                                </Typography>
                                {/* Body thông tin chi tiết kho */}
                                <Grid container className={classes.detailWrap} spacing={2}>
                                    <Grid item xs={4}>
                                        <p style={{fontWeight: 'bold'}}>
                                            Tiền thu hộ: {formData.totalprice}đ
                                        </p>
                                    </Grid>
                                    <Grid item xs={4}>
                                        <p style={{fontWeight: 'bold'}}> Tổng cước: 20000đ</p>
                                    </Grid>
                                    <Grid item xs={4}>
                                        <p style={{fontWeight: 'bold'}}> Tiền thu người gửi: {20000}đ </p>
                                    </Grid>
                                </Grid>

                            </Box>
                        </Box>

                    </Box>
                </Box>

                {/* Order History Dialog */}
                <Dialog
                    open={openHistoryDialog}
                    onClose={() => setOpenHistoryDialog(false)}
                    maxWidth="md"
                    fullWidth
                >
                    <DialogTitle>
                        Lịch sử trạng thái đơn hàng #{orderId?.substring(0, 8)}
                    </DialogTitle>
                    <DialogContent>
                        {orderHistory.length === 0 ? (
                            <Typography variant="body2" color="textSecondary">
                                Không có lịch sử trạng thái
                            </Typography>
                        ) : (
                            <List>
                                {orderHistory.map((history, index) => (
                                    <ListItem key={history.id} divider={index < orderHistory.length - 1}>
                                        <ListItemText
                                            primary={
                                                <Box display="flex" alignItems="center" gap={1}>
                                                    <Chip
                                                        label={formatStatus(history.status)}
                                                        color={getStatusColor(history.status)}
                                                        size="small"
                                                    />

                                                </Box>
                                            }
                                            secondary={
                                                <Box>
                                                    <Typography variant="body2">
                                                        <strong>Thời gian:</strong> {new Date(history.createdAt).toLocaleString('vi-VN')}
                                                    </Typography>
                                                    {history.changedBy && (
                                                        <Typography variant="body2">
                                                            <strong>Người thay đổi:</strong> {history.changedBy}
                                                        </Typography>
                                                    )}
                                                    {history.changeReason && (
                                                        <Typography variant="body2">
                                                            <strong>Lý do:</strong> {history.changeReason}
                                                        </Typography>
                                                    )}

                                                </Box>
                                            }
                                        />
                                    </ListItem>
                                ))}
                            </List>
                        )}
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={() => setOpenHistoryDialog(false)}>
                            Đóng
                        </Button>
                    </DialogActions>
                </Dialog>
            </Fragment>
        )
    );
}

export default UpdateOrder;