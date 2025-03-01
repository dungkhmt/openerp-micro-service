import {useParams} from "react-router-dom";
import React, {Fragment, useEffect, useState} from "react";
import {request} from "../../api";
import {API_PATH} from "../apiPaths";
import {errorNoti, successNoti} from "../../utils/notification";
import LoadingScreen from "../../components/common/loading/loading";
import {Box, Button, Checkbox, FormControlLabel, Grid, MenuItem, Modal, TextField, Typography} from "@mui/material";
import Maps from "../../components/map/map";
import SearchBox from "../../components/map/searchBox";
import MapIcon from "@mui/icons-material/Map";
import useStyles from "./CreateOrder.style";
import {useForm} from "react-hook-form";
import {useLocation, useHistory} from "react-router-dom";

const CollectorOrderDetail = () =>{
    const history = useHistory();
    const { id: orderId } = useParams();
    const [order, setOrder] = useState();
    const [isLoading, setLoading] = useState(true);
    const [openModal, setOpenModal] = useState(false);
    const classes = useStyles();
    const [selectedField, setSelectedField] = useState(null);
    const [selectPosition, setSelectPosition] = useState(null);
    const { register, errors, handleSubmit, watch, getValues } = useForm();
    const location = useLocation();
    const assignmentId = location.state?.assignmentId;
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

    const handleSuccessfulPickup = async () => {
        try {
            await request(
                "put",
                `${API_PATH.ORDER}/assignment/collector`,
                (res) => {
                    const storedAssignments = JSON.parse(sessionStorage.getItem("assignments")) || [];
                    const currentIndex = storedAssignments.findIndex(a => a.id === assignmentId);
                    const nextOrder = storedAssignments[currentIndex + 1] || null;
                        sessionStorage.setItem("nextOrder", JSON.stringify(nextOrder));

                    successNoti("Lấy hàng thành công!");
                    setTimeout(() => {
                        history.goBack();
                    }, 1500);

                },
                {
                    401: () => {
                        errorNoti("Không có quyền thực hiện thao tác này");
                    },
                    400: () => {
                        errorNoti("Đơn hàng không hợp lệ hoặc đã được xử lý");
                    },
                    503: () => {
                        errorNoti("Có lỗi xảy ra khi xử lý đơn hàng");
                    }
                },
                {
                    assignmentId: assignmentId,
                    status: 'COMPLETED'
                }
            );
        } catch (error) {
            errorNoti("Có lỗi xảy ra khi xử lý đơn hàng");
        }
    };

    const handleFailedPickup = async () => {
        try {
            await request(
                "put",
                `${API_PATH.ORDER}/assignment/collector`,
                (res) => {
                    const storedAssignments = JSON.parse(sessionStorage.getItem("assignments")) || [];
                    const currentIndex = storedAssignments.findIndex(a => a.id === assignmentId);
                    const nextOrder = storedAssignments[currentIndex + 1] || null;
                    sessionStorage.setItem("nextOrder", JSON.stringify(nextOrder));

                    successNoti("Lấy hàng thất bại!");
                    setTimeout(() => {
                        history.goBack();
                    }, 1500);

                },
                {
                    401: () => {
                        errorNoti("Không có quyền thực hiện thao tác này");
                    },
                    400: () => {
                        errorNoti("Đơn hàng không hợp lệ hoặc đã được xử lý");
                    },
                    503: () => {
                        errorNoti("Có lỗi xảy ra khi xử lý đơn hàng");
                    }
                },
                {
                    assignmentId: assignmentId,
                    status: 'FAILED_ONCE'
                }
            );
        } catch (error) {
            errorNoti("Có lỗi xảy ra khi xử lý đơn hàng");
        }
    };
    useEffect(() => {
        setLoading(false); // Giả lập quá trình tải dữ liệu
    }, []);
    const handleBack = () => {
        // If we have a stored "from" path, use it
            history.goBack(); // Fallback to normal back navigation
    };
    return (
        isLoading ? (
            <LoadingScreen />
        ) : (
            <Fragment>
                <Box className={classes.warehousePage} >
                    <Button
                        variant="contained"
                        color="primary"
                        style={{ marginRight: '16px', marginBottom:'1%' }}
                        onClick={handleBack}

                    >
                        Quay lại
                    </Button>
                    <Grid container justifyContent="space-between"
                          className={classes.headerBox} >

                        <Grid>
                            <Typography variant="h5">
                                {"Chi tiết đơn hàng" }
                            </Typography>
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
                                                        disabled={true}
                                                        fullWidth
                                                        variant="outlined"
                                                        size="small"
                                                        inputRef={register({ required: "Vui lòng điền tên người gửi" })}
                                                        name="senderName"
                                                        error={!!errors.senderName}
                                                        helperText={errors.senderName?.message}
                                                        value={formData.senderName}
                                                    />
                                                </Box>
                                            </Grid>
                                            <Grid item xs={6}>
                                                <Box className={classes.inputWrap}>
                                                    <Box className={classes.labelInput}>
                                                        Số điện thoại
                                                    </Box>
                                                    <TextField
                                                        disabled={true}
                                                        fullWidth
                                                        variant="outlined"
                                                        size="small"
                                                        inputRef={register({ required: "Vui lòng điền số điện thoại người gửi" })}
                                                        name="senderPhone"
                                                        error={!!errors.senderPhone}
                                                        helperText={errors.senderPhone?.message}
                                                        value={formData.senderPhone}
                                                    />
                                                </Box>
                                            </Grid>
                                            <Grid item xs={12}>
                                                <Box className={classes.inputWrap}>
                                                    <Box className={classes.labelInput}>
                                                        Địa chỉ 
                                                    </Box>
                                                    <TextField
                                                        disabled={true}
                                                        fullWidth
                                                        variant="outlined"
                                                        size="small"
                                                        inputRef={register({ required: "Vui lòng điền địa chỉ người gửi" })}
                                                        name="senderAddress"
                                                        error={!!errors.senderAddress}
                                                        helperText={errors.senderAddress?.message}
                                                        value={formData.senderAddress}
                                                        onClick={() => setSelectedField('sender')}
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
                                                        disabled={true}
                                                        fullWidth
                                                        variant="outlined"
                                                        size="small"
                                                        inputRef={register({ required: "Vui lòng điền tên người nhận" })}
                                                        name="recipientName"
                                                        error={!!errors.recipientName}
                                                        helperText={errors.recipientName?.message}
                                                        value={formData.recipientName}
                                                    />
                                                </Box>
                                            </Grid>
                                            <Grid item xs={6}>
                                                <Box className={classes.inputWrap}>
                                                    <Box className={classes.labelInput}>
                                                        Số điện thoại
                                                    </Box>
                                                    <TextField
                                                        disabled={true}
                                                        fullWidth
                                                        variant="outlined"
                                                        size="small"
                                                        inputRef={register({ required: "Vui lòng điền số điện thoại người nhận" })}
                                                        name="recipientPhone"
                                                        error={!!errors.recipientPhone}
                                                        helperText={errors.recipientPhone?.message}
                                                        value={formData.recipientPhone}
                                                    />
                                                </Box>
                                            </Grid>
                                            <Grid item xs={12}>
                                                <Box className={classes.inputWrap}>
                                                    <Box className={classes.labelInput}>
                                                        Địa chỉ <Button style={{ "margin-bottom": 0 }}
                                                                        onClick={() => {setOpenModal(!openModal);
                                                                            setSelectedField('recipient')}}><MapIcon /></Button>
                                                    </Box>
                                                    <TextField
                                                        disabled={true}
                                                        fullWidth
                                                        variant="outlined"
                                                        size="small"
                                                        inputRef={register({ required: "Vui lòng điền địa chỉ người nhận" })}
                                                        name="recipientAddress"
                                                        error={!!errors.recipientAddress}
                                                        helperText={errors.recipientAddress?.message}
                                                        value={formData.recipientAddress}
                                                        onClick={() => setSelectedField('recipient')}

                                                    />
                                                </Box>
                                            </Grid>
                                            <Grid item xs={6}>
                                                <Box className={classes.inputWrap}>
                                                    <Box className={classes.labelInput}>
                                                        Dịch vụ chuyển phát
                                                    </Box>
                                                    <TextField
                                                        disabled={true}
                                                        select // Dùng select thay vì input text
                                                        fullWidth
                                                        variant="outlined"
                                                        size="small"
                                                        value={formData.orderType}
                                                    >
                                                        {/* Các tùy chọn */}
                                                        <MenuItem value="Bình thường">Bình thường</MenuItem>
                                                        <MenuItem value="Nhanh">Nhanh</MenuItem>
                                                        <MenuItem value="Hỏa tốc">Hỏa tốc</MenuItem>
                                                    </TextField>
                                                </Box>
                                            </Grid>
                                            <Grid item xs={6}>
                                                <Box className={classes.inputWrap}>
                                                    <Box className={classes.labelInput}>
                                                        Tiền thu hộ
                                                    </Box>
                                                    <TextField
                                                        disabled={true}
                                                        fullWidth
                                                        variant="outlined"
                                                        size="small"
                                                        inputRef={register({ required: "Vui lòng điền" })}
                                                        name="totalprice"
                                                        error={!!errors.totalprice}
                                                        helperText={errors.totalprice?.message}
                                                        value={formData.totalprice}
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
                                                                        disabled={true}
                                                                        fullWidth
                                                                        variant="outlined"
                                                                        size="small"
                                                                        name={`items[${index}].productId`}
                                                                        value={item.productId}
                                                                    />
                                                                </Box>
                                                            </Grid>
                                                            <Grid item xs={6}>
                                                                <Box className={classes.inputWrap}>
                                                                    <Box className={classes.labelInput}>
                                                                        Tên sản phẩm
                                                                    </Box>
                                                                    <TextField
                                                                        disabled={true}
                                                                        fullWidth
                                                                        variant="outlined"
                                                                        size="small"
                                                                        name={`items[${index}].name`}
                                                                        value={item.name}
                                                                    />
                                                                </Box>
                                                            </Grid>
                                                            <Grid item xs={12}>
                                                                <Box className={classes.inputWrap}>
                                                                    <FormControlLabel
                                                                        control={
                                                                            <Checkbox
                                                                                disabled={true}                                                                                checked={item.viewBeforeReceive || false}
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
                                                                        disabled={true}
                                                                        fullWidth
                                                                        variant="outlined"
                                                                        size="small"
                                                                        type="number"
                                                                        name={`items[${index}].quantity`}
                                                                        value={item.quantity}
                                                                    />
                                                                </Box>
                                                            </Grid>
                                                            <Grid item xs={6}>
                                                                <Box className={classes.inputWrap}>
                                                                    <Box className={classes.labelInput}>
                                                                        Khối lượng
                                                                    </Box>
                                                                    <TextField
                                                                        disabled={true}
                                                                        fullWidth
                                                                        variant="outlined"
                                                                        size="small"
                                                                        type="number"
                                                                        name={`items[${index}].weight`}
                                                                        value={item.weight}
                                                                    />
                                                                </Box>
                                                            </Grid>
                                                            <Grid item xs={4}>
                                                                <Box className={classes.inputWrap}>
                                                                    <Box className={classes.labelInput}>
                                                                        Dài(cm)
                                                                    </Box>
                                                                    <TextField
                                                                        disabled={true}
                                                                        fullWidth
                                                                        variant="outlined"
                                                                        size="small"
                                                                        type="number"
                                                                        name={`items[${index}].length`}
                                                                        value={item.length}
                                                                    />
                                                                </Box>
                                                            </Grid>
                                                            <Grid item xs={4}>
                                                                <Box className={classes.inputWrap}>
                                                                    <Box className={classes.labelInput}>
                                                                        Cao(cm)
                                                                    </Box>
                                                                    <TextField
                                                                        disabled={true}
                                                                        fullWidth
                                                                        variant="outlined"
                                                                        size="small"
                                                                        type="number"
                                                                        name={`items[${index}].height`}
                                                                        value={item.height}
                                                                    />
                                                                </Box>
                                                            </Grid>
                                                            <Grid item xs={4}>
                                                                <Box className={classes.inputWrap}>
                                                                    <Box className={classes.labelInput}>
                                                                        Rộng(cm)
                                                                    </Box>
                                                                    <TextField
                                                                        disabled={true}
                                                                        fullWidth
                                                                        variant="outlined"
                                                                        size="small"
                                                                        type="number"
                                                                        name={`items[${index}].width`}
                                                                        value={item.width}
                                                                    />
                                                                </Box>
                                                            </Grid>
                                                            <Grid item xs={6}>
                                                                <Box className={classes.inputWrap}>
                                                                    <Box className={classes.labelInput}>
                                                                        Giá
                                                                    </Box>
                                                                    <TextField
                                                                        disabled={true}
                                                                        fullWidth
                                                                        variant="outlined"
                                                                        size="small"
                                                                        type="number"
                                                                        step="0.01"
                                                                        name={`items[${index}].price`}
                                                                        value={item.price}
                                                                    />
                                                                </Box>
                                                            </Grid>
                                                        </Grid>
                                                        <br/>

                                                    </Box>
                                                ))}

                                            </Grid>
                                        </Grid>

                                    </Box>


                                </Grid>

                            </Grid>
                            <Box className={classes.boxInfor} style={{ margin: 0 }}>
                                <Typography className={classes.inforTitle} variant="h6">
                                    Tiền phí & tiền thu hộ
                                    <input
                                        style={{ display: 'none' }}
                                        id="raised-button-file"
                                        type="file"
                                    />

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
                        <Box className={classes.boxInfor} style={{ marginTop: '20px' }}>
                            <Grid container spacing={2} justifyContent="center">
                                <Grid item>
                                    <Button
                                        variant="contained"
                                        color="primary"
                                        onClick={handleSuccessfulPickup}
                                        style={{ marginRight: '16px' }}
                                    >
                                        Lấy hàng thành công
                                    </Button>
                                    <Button
                                        variant="contained"
                                        color="error"
                                        onClick={handleFailedPickup}
                                    >
                                        Lấy hàng thất bại
                                    </Button>
                                </Grid>
                            </Grid>
                        </Box>
                    </Box>

                </Box>
            </Fragment>
        )
    );
}

export default CollectorOrderDetail;