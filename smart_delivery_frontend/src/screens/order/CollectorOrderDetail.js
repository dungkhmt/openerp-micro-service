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
import {useSelector} from "react-redux";

const CollectorOrderDetail = () => {
    const history = useHistory();
    const { id: orderId } = useParams();
    const [order, setOrder] = useState();
    const [isLoading, setLoading] = useState(true);
    const [openModal, setOpenModal] = useState(false);
    const classes = useStyles();
    const [selectedField, setSelectedField] = useState(null);
    const [selectPosition, setSelectPosition] = useState(null);
    const { register, errors, handleSubmit } = useForm();
    const location = useLocation();
    const assignmentId = location.state?.assignmentId;
    const [assignment, setAssignment] = useState(null);

    // Get role from Redux state
    const role = useSelector((state) => state.auth.user?.role);

    // Determine if user is collector or shipper
    const isCollector = role === 'COLLECTOR';
    const isShipper = role === 'SHIPPER';

    // Role-specific text constants
    const actionText = isCollector ? 'thu gom' : 'giao hàng';
    const actionCapitalized = isCollector ? 'Thu gom' : 'Giao hàng';
    const personTypeText = isCollector ? 'người gửi' : 'người nhận';
    const oppositePersonText = isCollector ? 'người nhận' : 'người gửi';

    // Role-specific API endpoints
    const assignmentEndpoint = isCollector
        ? `${API_PATH.ASSIGN_COLLECTOR}/order/assignment/collector`
        : `${API_PATH.ORDER}/assignment/shipper`;

    // Role-specific storage keys
    const assignmentsStorageKey = isCollector ? 'collector_assignments' : 'shipper_assignments';
    const nextOrderStorageKey = isCollector ? 'collector_nextOrder' : 'shipper_nextOrder';

    // Helper function to check if assignment is completed based on role
    const isAssignmentCompleted = (assignmentData) => {
        if (!assignmentData) return false;

        if (isCollector) {
            return assignmentData.assignmentStatus === "COMPLETED" || assignmentData.assignmentStatus === "FAILED_ONCE";
        } else {
            return assignmentData.assignmentStatus === "COMPLETED" || assignmentData.assignmentStatus === "FAILED_ATTEMPT";
        }
    };

    // Check if action buttons should be disabled
    const isActionDisabled = isAssignmentCompleted(assignment);

    const [formData, setFormData] = useState({
        senderName: '',
        senderPhone: '',
        senderEmail: '',
        senderAddress: '',
        senderLongitude: '',
        senderLatitude: '',
        recipientName: '',
        recipientPhone: '',
        recipientEmail: '',
        recipientAddress: '',
        recipientLongitude: '',
        recipientLatitude: '',
        items: [{ productId: '', name: '', quantity: 1, weight: 0, price: 0, length: 0, width: 0, height: 0, viewBeforeReceive: false }],
        totalprice: 0,
        orderType: '',
    });

    useEffect(() => {
        const fetchData = async () => {
            await request(
                "get",
                `${API_PATH.ORDER}/${orderId}`,
                (res) => {
                    setOrder(res.data);
                    setFormData({
                        ...res.data,
                        senderName: res.data?.senderName,
                        senderPhone: res.data?.senderPhone,
                        senderEmail: res.data?.senderEmail,
                        senderAddress: res.data?.senderAddress,
                        recipientName: res.data?.recipientName,
                        recipientPhone: res.data?.recipientPhone || res.data?.recipienPhone, // Handle both field names
                        recipientEmail: res.data?.recipientEmail,
                        recipientAddress: res.data?.recipientAddress,
                    });
                    console.log("res", res.data);
                }, {
                    401: () => { },
                    503: () => {
                        errorNoti("Có lỗi khi tải dữ liệu của đơn hàng")
                    }
                }
            );
            setLoading(false);
        }
        if (orderId) {
            fetchData();
        } else {
            setLoading(false);
        }
    }, [orderId]);

    // Fetch assignment data to check status
    useEffect(() => {
        if (assignmentId) {
            // Get assignment data from session storage first
            const storedAssignments = JSON.parse(sessionStorage.getItem(assignmentsStorageKey)) || [];
            const currentAssignment = storedAssignments.find(a => a.id === assignmentId);

            if (currentAssignment) {
                setAssignment(currentAssignment);
            } else {
                // If not found in storage, could fetch from API if needed
                console.warn("Assignment not found in session storage");
            }
        }
    }, [assignmentId, assignmentsStorageKey]);

    const handleSuccessfulAction = async () => {
        if (isActionDisabled) {
            errorNoti("Không thể thực hiện thao tác này - đơn hàng đã được xử lý");
            return;
        }

        try {
            await request(
                "put",
                assignmentEndpoint,
                (res) => {
                    const storedAssignments = JSON.parse(sessionStorage.getItem(assignmentsStorageKey)) || [];
                    const currentIndex = storedAssignments.findIndex(a => a.id === assignmentId);
                    const nextOrder = storedAssignments[currentIndex + 1] || null;
                    sessionStorage.setItem(nextOrderStorageKey, JSON.stringify(nextOrder));

                    successNoti(`${actionCapitalized} thành công!`);
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
                        errorNoti(`Có lỗi xảy ra khi xử lý ${actionText}`);
                    }
                },
                {
                    assignmentId: assignmentId,
                    status: 'COMPLETED'
                }
            );
        } catch (error) {
            errorNoti(`Có lỗi xảy ra khi xử lý ${actionText}`);
        }
    };

    const handleFailedAction = async () => {
        if (isActionDisabled) {
            errorNoti("Không thể thực hiện thao tác này - đơn hàng đã được xử lý");
            return;
        }

        try {
            const failedStatus = isCollector ? 'FAILED_ONCE' : 'FAILED_ATTEMPT';

            await request(
                "put",
                assignmentEndpoint,
                (res) => {
                    const storedAssignments = JSON.parse(sessionStorage.getItem(assignmentsStorageKey)) || [];
                    const currentIndex = storedAssignments.findIndex(a => a.id === assignmentId);
                    const nextOrder = storedAssignments[currentIndex + 1] || null;
                    sessionStorage.setItem(nextOrderStorageKey, JSON.stringify(nextOrder));

                    successNoti(`${actionCapitalized} thất bại đã được ghi nhận!`);
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
                        errorNoti(`Có lỗi xảy ra khi xử lý ${actionText}`);
                    }
                },
                {
                    assignmentId: assignmentId,
                    status: failedStatus
                }
            );
        } catch (error) {
            errorNoti(`Có lỗi xảy ra khi xử lý ${actionText}`);
        }
    };

    const handleBack = () => {
        history.goBack();
    };

    // Function to render address section based on role
    const renderAddressSection = () => {
        if (isCollector) {
            return (
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
            );
        } else {
            // For Shipper, focus on recipient info
            return (
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
                                Địa chỉ
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
                </Grid>
            );
        }
    };

    // Function to render second address section (always show the opposite of primary)
    const renderSecondaryAddressSection = () => {
        if (isCollector) {
            // For Collector, secondary is recipient info
            return (
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
                                Địa chỉ <Button style={{ "marginBottom": 0 }}
                                                onClick={() => {
                                                    setOpenModal(!openModal);
                                                    setSelectedField('recipient');
                                                }}><MapIcon /></Button>
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
                </Grid>
            );
        } else {
            // For Shipper, secondary is sender info
            return (
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
                                Địa chỉ <Button style={{ "marginBottom": 0 }}
                                                onClick={() => {
                                                    setOpenModal(!openModal);
                                                    setSelectedField('sender');
                                                }}><MapIcon /></Button>
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
            );
        }
    };

    // Dummy function to prevent form errors
    const onsubmit = () => {};

    return (
        isLoading ? (
            <LoadingScreen />
        ) : (
            <Fragment>
                <Box className={classes.warehousePage}>
                    <Button
                        variant="contained"
                        color="primary"
                        style={{ marginRight: '16px', marginBottom: '1%' }}
                        onClick={handleBack}
                    >
                        Quay lại
                    </Button>
                    <Grid container justifyContent="space-between" className={classes.headerBox}>
                        <Grid>
                            <Typography variant="h5">
                                {"Chi tiết đơn hàng"}
                            </Typography>
                            {isActionDisabled && (
                                <Typography variant="body2" sx={{ color: 'orange', fontStyle: 'italic', marginTop: '8px' }}>
                                    Đơn hàng này đã được xử lý - không thể thực hiện thao tác
                                </Typography>
                            )}
                        </Grid>
                    </Grid>
                </Box>
                <Box className={classes.bodyBox}>
                    <Box className={classes.formWrap} component="form" onSubmit={handleSubmit(onsubmit)}>
                        {/* Thông tin chung kho */}
                        <Box>
                            <Grid container spacing={2}>
                                <Grid item xs={6.5}>
                                    <Box className={classes.boxInfor}>
                                        <Typography className={classes.inforTitle} variant="h6">
                                            1. Thông tin {personTypeText}
                                        </Typography>
                                        {renderAddressSection()}
                                        <Typography className={classes.inforTitle} variant="h6" style={{ marginTop: '20px' }}>
                                            Thông tin {oppositePersonText}
                                        </Typography>
                                        {renderSecondaryAddressSection()}
                                        <Grid container spacing={3} className={classes.inforWrap}>
                                            <Grid item xs={6}>
                                                <Box className={classes.inputWrap}>
                                                    <Box className={classes.labelInput}>
                                                        Dịch vụ chuyển phát
                                                    </Box>
                                                    <TextField
                                                        disabled={true}
                                                        select
                                                        fullWidth
                                                        variant="outlined"
                                                        size="small"
                                                        value={formData.orderType || "Bình thường"}
                                                    >
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
                                                                                disabled={true}
                                                                                checked={item.viewBeforeReceive || false}
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
                                                        <br />
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
                                </Typography>
                                <Grid container className={classes.detailWrap} spacing={2}>
                                    <Grid item xs={4}>
                                        <p style={{ fontWeight: 'bold' }}>
                                            Tiền thu hộ: {formData.totalprice}đ
                                        </p>
                                    </Grid>
                                    <Grid item xs={4}>
                                        <p style={{ fontWeight: 'bold' }}> Tổng cước: 20000đ</p>
                                    </Grid>
                                    <Grid item xs={4}>
                                        <p style={{ fontWeight: 'bold' }}>
                                            {isCollector
                                                ? `Tiền thu người gửi: ${20000}đ`
                                                : `Tiền thu người nhận: ${formData.totalprice + 20000}đ`}
                                        </p>
                                    </Grid>
                                </Grid>
                            </Box>
                        </Box>

                        {/* Action buttons */}
                        <Box className={classes.boxInfor} style={{ marginTop: '20px' }}>
                            <Grid container spacing={2} justifyContent="center">
                                <Grid item>
                                    <Button
                                        variant="contained"
                                        color="primary"
                                        onClick={handleSuccessfulAction}
                                        disabled={isActionDisabled}
                                        style={{
                                            marginRight: '16px',
                                            opacity: isActionDisabled ? 0.6 : 1,
                                            cursor: isActionDisabled ? 'not-allowed' : 'pointer'
                                        }}
                                    >
                                        {actionCapitalized} thành công
                                    </Button>
                                    <Button
                                        variant="contained"
                                        color="error"
                                        onClick={handleFailedAction}
                                        disabled={isActionDisabled}
                                        style={{
                                            opacity: isActionDisabled ? 0.6 : 1,
                                            cursor: isActionDisabled ? 'not-allowed' : 'pointer'
                                        }}
                                    >
                                        {actionCapitalized} thất bại
                                    </Button>
                                </Grid>
                            </Grid>
                        </Box>
                    </Box>
                </Box>

                {/* Map Modal */}
                <Modal
                    open={openModal}
                    onClose={() => setOpenModal(false)}
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
                        <Typography variant="h6" id="modal-modal-title">
                            Vị trí {selectedField === 'sender' ? 'người gửi' : 'người nhận'}
                        </Typography>
                        <Maps
                            selectPosition={selectPosition}
                            setSelectPosition={setSelectPosition}
                        />
                        <div style={{ width: "50%", height: "90%" }}>
                            <SearchBox
                                selectPosition={selectPosition}
                                setSelectPosition={setSelectPosition}
                            />
                        </div>
                        <Button
                            variant="contained"
                            color="primary"
                            onClick={() => setOpenModal(false)}
                            style={{ marginTop: 16 }}
                        >
                            Đóng
                        </Button>
                    </Box>
                </Modal>
            </Fragment>
        )
    );
}

export default CollectorOrderDetail;
