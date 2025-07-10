import React, {useState, useEffect, Fragment, useRef} from 'react';
import { request } from 'api';
import { errorNoti, successNoti } from 'utils/notification';
import LoadingScreen from "../../components/common/loading/loading";
import {cityOptions, districtData, wardData} from "../../utils/LocationData"
import {
    MenuItem,
    Button,
    Box,
    Grid,
    InputAdornment,
    OutlinedInput,
    TextField,
    Typography,
    Modal,
    FormControlLabel, Checkbox, Select, FormHelperText
} from '@mui/material';
import Maps from "../../components/map/map";
import SearchBox from "../../components/map/searchBox";
import MapIcon from "@mui/icons-material/Map";
import HighlightOffIcon from "@mui/icons-material/HighlightOff";
import {Layer, Rect, Stage} from "react-konva";
import {StandardTable} from "erp-hust/lib/StandardTable";
import {useForm} from "react-hook-form";
import {useHistory} from "react-router";
import {useRouteMatch} from "react-router-dom";
import useStyles from "screens/styles.js";

const CreateEmployee = () => {

    const classes = useStyles();
    const [isLoading, setLoading] = useState(true);
    const [districtOptions, setDistrictOptions] = useState([]);
    const [wardOptions, setWardOptions] = useState([]);
    const [scale, setScale] = useState();
    const { register, errors, handleSubmit, watch, getValues } = useForm();
    const [width, setWidth] = useState();
    const [openModal, setOpenModal] = useState(false);
    const [selectPosition, setSelectPosition] = useState(null);
    const warehouseId = null;
    const isCreateForm = warehouseId == null;
    const history = useHistory();
    const { path } = useRouteMatch();
    // Use to determine what value of Address text field
    // if updateAddress = true -> Address text field = new select address from map
    // else -> current warehouse address or null
    const [selectedField, setSelectedField] = useState(null);
    const [hubs, setHubs] = useState([]);

    // Tạo state cho các trường trong form
    const [formData, setFormData] = useState({
        name: '',
        phone: '',
        email: '',
        address: '',
        role: '',
        username: '',
        password: '',
        city : '',
        district: '',
        ward: '',
        hub:''
    });

    const roleOptions = [
        { value: 'COLLECTOR', label: 'Nhân viên lấy hàng' },
        { value: 'SHIPPER', label: 'Nhân viên giao hàng' },
        { value: 'DRIVER', label: 'Lái xe' },
    ]


    // Danh sách Tỉnh/Thành phố


    useEffect(() => {
        setLoading(false); // Giả lập quá trình tải dữ liệu
    }, []);
    useEffect(() => {
        request("get", "/smdeli/hubmanager/hub", (res) => {
            setHubs(res.data);
        }).then();
    }, []);


    // Hàm cập nhật giá trị của từng trường
    const handleInputChange = (e, index = null, fieldName = null) => {
        const { name, value } = e.target;
            setFormData({ ...formData, [name]: value });
    };

    const handleCityChange = (event) => {
        const selectedCity = event.target.value;
        setFormData({ ...formData, city: selectedCity });
        setDistrictOptions(districtData[selectedCity] || []);  // Luôn cập nhật mảng rỗng nếu không có dữ liệu
        setWardOptions([]);  // Reset phường/xã
    };

    const handleDistrictChange = (event) => {
        const selectedDistrict = event.target.value;
        setFormData({ ...formData, district: selectedDistrict });
        setWardOptions(wardData[selectedDistrict] || []);
    };


    const handleCheckboxChange = (index) => (event) => {
        const updatedItems = [...formData.items];
        updatedItems[index].viewBeforeReceive = event.target.checked;
        setFormData({ ...formData, items: updatedItems });
    };

    // Hàm xử lý khi submit form
    const onsubmit = async (e) => {
        console.log("Submitting data:", formData); // Log dữ liệu để kiểm tra
        request(
            "post",
            "/smdeli/humanresource/add",
            (res) => {
                successNoti('Thêm nhân viên mới thành công!');
            },
            {
                401: () => { errorNoti("Có lỗi xảy ra. Vui lòng thử lại sau") },
                400: (e) => { errorNoti("Có lỗi xảy ra. Vui lòng thử lại sau") },
                500: (e) => { errorNoti("Có lỗi xảy ra. Vui lòng thử lại sau") }

            },
            formData
        );
    };

    useEffect(()=>{
        console.log("Updated formData:", formData); // Kiểm tra giá trị sau khi cập nhật

    },[formData])


    return (
        isLoading ? (
            <LoadingScreen />
        ) : (
            <Fragment>
                <Box className={classes.warehousePage} >
                    <Grid container justifyContent="space-between"
                          className={classes.headerBox} >
                        <Grid>
                            <Typography variant="h5">
                                {"Thêm nhân viên mới" }
                            </Typography>
                        </Grid>
                        <Grid className={classes.buttonWrap}>
                            <Button variant="contained" className={classes.addButton}
                                    type="submit" onClick={handleSubmit(onsubmit)} >Lưu</Button>
                        </Grid>
                    </Grid>
                </Box>
                <Box className={classes.bodyBox}>
                    <Box className={classes.formWrap}
                         component="form"
                         onSubmit={handleSubmit(onsubmit)}>

                        {/* Thông tin chung */}
                        <Box >
                            <Grid container spacing={2}>
                                <Grid item xs={7}>
                                    <Box className={classes.boxInfor}>
                                        <Typography className={classes.inforTitle} variant="h6">
                                            1. Thông tin cơ bản
                                        </Typography>
                                        <Grid container spacing={3} className={classes.inforWrap}>

                                            <Grid item xs={6}>
                                                <Box className={classes.inputWrap}>
                                                    <Box className={classes.labelInput}>
                                                        Họ và tên
                                                    </Box>
                                                    <TextField
                                                        fullWidth
                                                        variant="outlined"
                                                        size="small"
                                                        inputRef={register({ required: "Vui lòng điền họ tên nhân viên" })}
                                                        name="name"
                                                        error={!!errors.name}
                                                        helperText={errors.name?.message}
                                                        value={formData.name}
                                                        onChange={(e) => handleInputChange(e)}
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
                                                        name="phone"
                                                        error={!!errors.phone}
                                                        helperText={errors.phone?.message}
                                                        value={formData.phone}
                                                        onChange={(e) => handleInputChange(e)}
                                                    />
                                                </Box>
                                            </Grid>
                                            <Grid item xs={6}>
                                                <Box className={classes.inputWrap}>
                                                    <Box className={classes.labelInput}>
                                                        Email
                                                    </Box>
                                                    <TextField
                                                        fullWidth
                                                        variant="outlined"
                                                        size="small"
                                                        inputRef={register({ required: "Vui lòng điền email" })}
                                                        name="email"
                                                        error={!!errors.email}
                                                        helperText={errors.email?.message}
                                                        value={formData.email}
                                                        onChange={(e) => handleInputChange(e)}
                                                    />
                                                </Box>
                                            </Grid>
                                            <Grid item xs={6}>
                                                <Box className={classes.inputWrap}>
                                                    <Box className={classes.labelInput}>
                                                        Hub phân công
                                                    </Box>
                                                    <Select
                                                        fullWidth
                                                        variant="outlined"
                                                        size="small"
                                                        name="hub"
                                                        value={formData.hub}
                                                        onChange={handleInputChange}
                                                        inputRef={register({ required: "Vui lòng chọn vai trò" })}
                                                        error={!!errors.hub}
                                                    >
                                                        {hubs.map((hub) => (
                                                            <MenuItem key={hub.id} value={hub.id}>
                                                                {hub.name}
                                                            </MenuItem>
                                                        ))}
                                                    </Select>
                                                </Box>
                                            </Grid>
                                            <Grid item xs={4}>
                                                <Box className={classes.inputWrap}>
                                                    <Box className={classes.labelInput}>
                                                        Vai trò
                                                    </Box>
                                                    <Select
                                                        fullWidth
                                                        variant="outlined"
                                                        size="small"
                                                        name="role"
                                                        value={formData.role}
                                                        onChange={handleInputChange}
                                                        inputRef={register({ required: "Vui lòng chọn vai trò" })}
                                                        error={!!errors.role}
                                                    >
                                                        {roleOptions.map((role) => (
                                                            <MenuItem key={role.value} value={role.value}>
                                                                {role.label}
                                                            </MenuItem>
                                                        ))}
                                                    </Select>
                                                    {errors.ward && <FormHelperText error>{errors.ward.message}</FormHelperText>}
                                                </Box>
                                            </Grid>
                                                {/* Chọn Tỉnh/Thành phố */}
                                            <Grid item xs={12}>
                                                <Box className={classes.inputWrap}>
                                                    <Box className={classes.labelInput}>
                                                       Địa chỉ
                                                    </Box>
                                                    <TextField
                                                        fullWidth
                                                        variant="outlined"
                                                        size="small"
                                                        inputRef={register({ required: "Vui lòng điền địa chỉ" })}
                                                        name="address"
                                                        error={!!errors.address}
                                                        helperText={errors.address?.message}
                                                        value={formData.address}
                                                        onChange={(e) => handleInputChange(e)}
                                                    />
                                                </Box>
                                            </Grid>
                                                <Grid item xs={4}>
                                                    <Box className={classes.inputWrap}>
                                                        <Box className={classes.labelInput}>
                                                            Tỉnh/Thành phố
                                                        </Box>
                                                        <Select
                                                            fullWidth
                                                            variant="outlined"
                                                            size="small"
                                                            name="city"
                                                            value={formData.city}
                                                            onChange={handleCityChange}
                                                            inputRef={register({ required: "Vui lòng chọn tỉnh/thành phố" })}
                                                            error={!!errors.city}
                                                        >
                                                            {cityOptions.map((city) => (
                                                                <MenuItem key={city} value={city}>
                                                                    {city}
                                                                </MenuItem>
                                                            ))}
                                                        </Select>
                                                        {errors.city && <FormHelperText error>{errors.city.message}</FormHelperText>}
                                                    </Box>
                                                </Grid>

                                                {/* Chọn Quận/Huyện */}
                                                <Grid item xs={4}>
                                                    <Box className={classes.inputWrap}>
                                                        <Box className={classes.labelInput}>
                                                            Quận/Huyện
                                                        </Box>
                                                        <Select
                                                            fullWidth
                                                            variant="outlined"
                                                            size="small"
                                                            name="district"
                                                            value={formData.district}
                                                            onChange={handleDistrictChange}
                                                            inputRef={register({ required: "Vui lòng chọn quận/huyện" })}
                                                            error={!!errors.district}
                                                        >
                                                            {districtOptions.map((district) => (
                                                                <MenuItem key={district} value={district}>
                                                                    {district}
                                                                </MenuItem>
                                                            ))}
                                                        </Select>
                                                        {errors.district && <FormHelperText error>{errors.district.message}</FormHelperText>}
                                                    </Box>
                                                </Grid>

                                                {/* Chọn Phường/Xã */}
                                                <Grid item xs={4}>
                                                    <Box className={classes.inputWrap}>
                                                        <Box className={classes.labelInput}>
                                                            Phường/Xã
                                                        </Box>
                                                        <Select
                                                            fullWidth
                                                            variant="outlined"
                                                            size="small"
                                                            name="ward"
                                                            value={formData.ward}
                                                            onChange={handleInputChange}
                                                            inputRef={register({ required: "Vui lòng chọn phường/xã" })}
                                                            error={!!errors.ward}
                                                        >
                                                            {wardOptions.map((ward) => (
                                                                <MenuItem key={ward} value={ward}>
                                                                    {ward}
                                                                </MenuItem>
                                                            ))}
                                                        </Select>
                                                        {errors.ward && <FormHelperText error>{errors.ward.message}</FormHelperText>}
                                                    </Box>
                                                </Grid>

                                        </Grid>

                                    </Box>
                                </Grid>
                                <Grid item xs={4}>
                                    <Box className={classes.boxInfor}>
                                        <Typography className={classes.inforTitle} variant="h6">
                                            2. Thông tin tài khoản mới
                                        </Typography>
                                        <Grid container spacing={3} className={classes.inforWrap}>
                                            <Grid item xs={7}>
                                                <Box className={classes.inputWrap}>
                                                    <Box className={classes.labelInput}>
                                                        Tên đăng nhập
                                                    </Box>
                                                    <TextField
                                                        fullWidth
                                                        variant="outlined"
                                                        size="small"
                                                        inputRef={register({ required: "Vui lòng điền tên đăng nhập" })}
                                                        name="username"
                                                        error={!!errors.username}
                                                        helperText={errors.username?.message}
                                                        value={formData.username}
                                                        onChange={(e) => handleInputChange(e)}
                                                    />
                                                </Box>
                                            </Grid>
                                            <Grid item xs={7}>
                                                <Box className={classes.inputWrap}>
                                                    <Box className={classes.labelInput}>
                                                        Mật khẩu
                                                    </Box>
                                                    <TextField
                                                        fullWidth
                                                        variant="outlined"
                                                        size="small"
                                                        inputRef={register({ required: "Vui lòng điền mật khẩu" })}
                                                        name="password"
                                                        error={!!errors.password}
                                                        helperText={errors.password?.message}
                                                        value={formData.password}
                                                        onChange={(e) => handleInputChange(e)}
                                                    />
                                                </Box>
                                            </Grid>
                                            <Grid item xs={7}>
                                                <Box className={classes.inputWrap}>
                                                    <Box className={classes.labelInput} >
                                                        Xác nhận mật khẩu
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
                                                    />
                                                </Box>
                                            </Grid>
                                        </Grid>

                                    </Box>
                                </Grid>


                            </Grid>

                        </Box>

                    </Box>
                </Box>
            </Fragment>
        )
    );
};

export default CreateEmployee;
