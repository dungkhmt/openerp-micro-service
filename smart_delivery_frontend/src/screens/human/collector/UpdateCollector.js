import React, {useState, useEffect, Fragment, useRef} from 'react';
import { request } from 'api';
import { errorNoti, successNoti } from 'utils/notification';
import LoadingScreen from "../../../components/common/loading/loading";
import {cityOptions, districtData, wardData} from "../../../utils/LocationData"
import {
    MenuItem,
    Button,
    Box,
    Grid,
    IconButton,
    InputAdornment,
    OutlinedInput,
    TextField,
    Typography,
    Modal,
    FormControlLabel, Checkbox, Select, FormHelperText
} from '@mui/material';
import { Visibility, VisibilityOff } from '@mui/icons-material';

import SearchBox from "../../../components/map/searchBox";
import MapIcon from "@mui/icons-material/Map";
import HighlightOffIcon from "@mui/icons-material/HighlightOff";
import {Layer, Rect, Stage} from "react-konva";
import {StandardTable} from "erp-hust/lib/StandardTable";
import {useForm} from "react-hook-form";
import {useHistory} from "react-router";
import {useParams, useRouteMatch} from "react-router-dom";
import useStyles from "screens/styles.js";
import {API_PATH} from "../../apiPaths";

const UpdateCollector = () => {

    const {id: collectorId} = useParams();
    const classes = useStyles();
    const [isLoading, setLoading] = useState(true);
    const [districtOptions, setDistrictOptions] = useState([]);
    const [wardOptions, setWardOptions] = useState([]);
    const { register, errors, handleSubmit, watch, getValues } = useForm();
    const warehouseId = null;
    const isCreateForm = warehouseId == null;
    const history = useHistory();
    const { path } = useRouteMatch();
    const [showPassword, setShowPassword] = useState(false); // State để quản lý hiển thị mật khẩu
    // Use to determine what value of Address text field
    // if updateAddress = true -> Address text field = new select address from map
    // else -> current warehouse address or null
    const [selectedField, setSelectedField] = useState(null);
    const [hubs, setHubs] = useState([]);

    // Tạo state cho các trường trong form
    const [formData, setFormData] = useState({
        id: collectorId,
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

    useEffect(() => {
        request("get", "/smdeli/hubmanager/hub", (res) => {
            setHubs(res.data);
        }).then();
    }, []);

    useEffect(() => {
        const fetchData = async () => {
            await request(
                "get",
                `${API_PATH.COLLECTOR}/${collectorId}`,
                (res) => {
                    //setCollector(res.data);
                    setFormData((prevState) => ({
                        ...prevState,
                        name: res.data.name,
                        phone: res.data.phone,
                        email: res.data.email,
                        address: res.data.address,
                        role: res.data.role || "COLLECTOR",
                        username: res.data.username,
                        password: res.data.password,
                        city: res.data.city,
                        district: res.data.district,
                        ward:res.data.ward,
                        hub:res.data.hubId
                    }));

                    setDistrictOptions(districtData[res.data.city] || []);
                    setWardOptions(wardData[res.data.district] || []);
                    console.log("formdsdsd",res.data.hubName);
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
        if(collectorId){
            fetchData();
        }
        else {
            setLoading(true);
        }
    },[collectorId])

    const roleOptions = [
        { value: 'COLLECTOR', label: 'Nhân viên lấy hàng' },
    ]


    // Danh sách Tỉnh/Thành phố


    useEffect(() => {
        setLoading(false); // Giả lập quá trình tải dữ liệu
    }, []);

    const handleClickShowPassword = () => {
        setShowPassword((prev) => !prev); // Đổi trạng thái hiển thị mật khẩu
    };
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
            "put",
            "/smdeli/humanresource/update",
            (res) => {
                successNoti('Cập nhật thành công!');
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
                                {"Thông tin nhân viên giao hàng" }
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
                                                        value={roleOptions[0].value}
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
                                                        inputRef={register({ required: "Vui lòng điền số điện thoại người gửi" })}
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
                                                        value={formData.city || ''}  // Hiển thị giá trị city từ formData
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
                                                        value={formData.district || ''}  // Hiển thị giá trị district từ formData
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
                                                        value={formData.ward || ''}  // Hiển thị giá trị ward từ formData
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
                                            2. Thông tin tài khoản
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
                                                        type={showPassword ? 'text' : 'password'} // Đổi giữa type text và password
                                                        error={!!errors.password}
                                                        helperText={errors.password?.message}
                                                        value={formData.password}
                                                        onChange={(e) => handleInputChange(e)}
                                                        InputProps={{
                                                            endAdornment: (
                                                                <InputAdornment position="end">
                                                                    <IconButton
                                                                        onClick={handleClickShowPassword}
                                                                        onMouseDown={(e) => e.preventDefault()} // Ngăn chặn sự kiện mặc định khi nhấn giữ
                                                                        edge="end"
                                                                    >
                                                                        {showPassword ? <VisibilityOff /> : <Visibility />}
                                                                    </IconButton>
                                                                </InputAdornment>
                                                            ),
                                                        }}
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

export default UpdateCollector;
