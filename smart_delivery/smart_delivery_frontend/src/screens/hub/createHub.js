import SearchBox from 'components/map/searchBox.js';
import Maps from 'components/map/map';
import MapIcon from '@mui/icons-material/Map';
import { Button, Box, Grid, InputAdornment, OutlinedInput, TextField, Typography, Modal } from '@mui/material';
import React, { Fragment, useEffect, useState, useRef } from 'react';
import { useForm } from "react-hook-form";
import { setCanvasSize } from 'screens/utils/utils.js';
import { Stage, Layer, Rect } from "react-konva";
import useStyles from 'screens/styles.js';
import { request } from 'api';
import { errorNoti, successNoti } from 'utils/notification';
import HighlightOffIcon from '@mui/icons-material/HighlightOff';
import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline';
import CachedIcon from '@mui/icons-material/Cached';
import { API_PATH} from '../apiPaths.js';
// import { warehouse } from 'config/menuconfig/warehouse.js'; ==> ?
import { useRouteMatch } from "react-router-dom";
import { useHistory } from "react-router";
import {StandardTable} from "erp-hust/lib/StandardTable";
import withScreenSecurity from 'components/common/withScreenSecurity.js';
import LoadingScreen from 'components/common/loading/loading.js';

const CreateHub = ( props, { screenAuthorization } ) => {
    const classes = useStyles();
    const [scale, setScale] = useState();
    const { register, errors, handleSubmit, watch, getValues } = useForm();
    const [pos, setPos] = useState();
    const stageCanvasRef = useRef();
    const [width, setWidth] = useState();
    const [warehouseHeight, setWarehouseHeight] = useState();
    const [height, setHeight] = useState();
    const [openModal, setOpenModal] = useState(false);
    const [selectPosition, setSelectPosition] = useState(null);
    const warehouseId = props.match?.params?.id;
    const [warehouseInfo, setWarehouseInfo] = useState(null);
    const isCreateForm = warehouseId == null;
    const history = useHistory();
    const { path } = useRouteMatch();
    // Use to determine what value of Address text field
    // if updateAddress = true -> Address text field = new select address from map
    // else -> current warehouse address or null
    const [updateAddress, setUpdateAddress] = useState(false);
    const [isLoading, setLoading] = useState(true);

    useEffect(() => {
        const fetchData = async () => {
            await request(
                "get",
                API_PATH.HUB + "/" + warehouseId,
                (res) => {
                    setWarehouseInfo(res.data);
                },
                {
                    401: () => { },
                    503: () => { errorNoti("Có lỗi khi tải dữ liệu của kho") }
                }
            );
            setLoading(false);
        }

        if (warehouseId != null) {
            fetchData();
        } else {
            setLoading(false);
        }
    }, [warehouseId])

    useEffect(() => {
        if (selectPosition != null) {
            setUpdateAddress(true);
            console.log("Update address is set to true");
        }
    }, [selectPosition])


    const addWareHouse = () => {
    }


    let submitForm = (data) => {

        data.longitude = updateAddress ? selectPosition.lon : warehouseInfo.longitude;
        data.latitude = updateAddress ? selectPosition.lat : warehouseInfo.latitude;
        data.id = isCreateForm ? null : warehouseId;
        console.log("Data in request body -> ", data);
        request(
            "post",
            API_PATH.CREATE_HUB,
            (res) => {
                successNoti(isCreateForm ? "Tạo kho thành công" : "Cập nhật kho thành công");

            },
            {
                401: () => { errorNoti("Có lỗi xảy ra. Vui lòng thử lại sau") },
                400: (e) => { errorNoti("Có lỗi xảy ra. Vui lòng thử lại sau") }
            },
            data
        );
    };




    return (
        isLoading ? <LoadingScreen /> :
            <Fragment>
                <Modal open={openModal}
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
                            Chọn vị trí Hub
                            <Button variant="contained"
                                    className={classes.addButton}
                                    type="submit"
                                    onClick={() => setOpenModal(false)} >
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
                                {isCreateForm ? "Tạo mới Hub" : "Xem thông tin Hub"}
                            </Typography>
                        </Grid>
                        <Grid className={classes.buttonWrap}>
                            <Button variant="contained" className={classes.addButton}
                                    type="submit" onClick={handleSubmit(submitForm)} >Lưu</Button>
                        </Grid>
                    </Grid>
                </Box>
                <Box className={classes.bodyBox}>
                    <Box className={classes.formWrap}
                         component="form"
                         onSubmit={handleSubmit(addWareHouse)}>

                        {/* Thông tin chung kho */}
                        <Box >
                            <Grid container spacing={2}>
                                <Grid item xs={8}>
                                    <Box className={classes.boxInfor}>
                                        <Typography className={classes.inforTitle} variant="h6">
                                            Thông tin cơ bản
                                        </Typography>
                                        <Grid container spacing={3} className={classes.inforWrap}>
                                            <Grid item xs={6}>
                                                <Box className={classes.inputWrap}>
                                                    <Box className={classes.labelInput}>
                                                        Tên kho
                                                    </Box>
                                                    <TextField
                                                        fullWidth
                                                        variant="outlined"
                                                        size="small"
                                                        inputRef={register({ required: "Vui lòng điền tên kho" })}
                                                        name="name"
                                                        error={!!errors.name}
                                                        helperText={errors.name?.message}
                                                        value={warehouseInfo?.name}
                                                        onChange={(e) => {
                                                            setWarehouseInfo({
                                                                ...warehouseInfo,
                                                                name: e.target.value
                                                            });
                                                        }}
                                                    />
                                                </Box>
                                            </Grid>
                                            <Grid item xs={6}>
                                                <Box className={classes.inputWrap}>
                                                    <Box className={classes.labelInput}>
                                                        Mã kho
                                                    </Box>
                                                    <TextField
                                                        fullWidth
                                                        variant="outlined"
                                                        size="small"
                                                        inputRef={register({ required: "Vui lòng điền mã kho" })}
                                                        name="code"
                                                        error={!!errors.code}
                                                        helperText={errors.code?.message}
                                                        value={warehouseInfo?.code}
                                                        onChange={(e) => {
                                                            setWarehouseInfo({
                                                                ...warehouseInfo,
                                                                code: e.target.value
                                                            });
                                                        }}
                                                    />
                                                </Box>
                                            </Grid>
                                            <Grid item xs={12}>
                                                <Box className={classes.inputWrap}>
                                                    <Box className={classes.labelInput}>
                                                        Địa chỉ <Button style={{ "margin-bottom": 0 }}
                                                                        onClick={() => setOpenModal(!openModal)}><MapIcon /></Button>
                                                    </Box>
                                                    <TextField
                                                        fullWidth
                                                        className={classes.inputRight}
                                                        style={{ flexGrow: 1, marginRight: 4 }}
                                                        variant="outlined"
                                                        size="small"
                                                        inputRef={register({ required: "Vui lòng điền địa chỉ kho" })}
                                                        name="address"
                                                        error={!!errors.address}
                                                        helperText={errors.address?.message}
                                                        disabled
                                                        value={
                                                            updateAddress ?
                                                                selectPosition?.display_name :
                                                                warehouseInfo?.address
                                                        }
                                                    />
                                                </Box>
                                            </Grid>
                                        </Grid>
                                    </Box>
                                </Grid>
                                <Grid item xs={4}>
                                    <Box className={classes.boxInfor}>
                                        <Typography className={classes.inforTitle} variant="h6">
                                            Kích thước
                                        </Typography>
                                        <Grid container spacing={3} className={classes.inforWrap}>
                                            <Grid item xs={12}>
                                                <Box className={classes.inputWrap}>
                                                    <Box className={classes.labelInput}>
                                                        Chiều rộng
                                                    </Box>
                                                    <OutlinedInput
                                                        fullWidth
                                                        inputRef={register({ required: false })}
                                                        name="length"
                                                        className={classes.settingInput}
                                                        endAdornment={<InputAdornment position="end">{`(mét)`}</InputAdornment>}
                                                        value={warehouseInfo?.length}
                                                        onChange={(e) => {
                                                            setWarehouseInfo({
                                                                ...warehouseInfo,
                                                                length: e.target.value
                                                            });
                                                        }}
                                                    />
                                                </Box>
                                            </Grid>
                                            <Grid item xs={12}>
                                                <Box className={classes.inputWrap}>
                                                    <Box className={classes.labelInput}>
                                                        Chiều dài
                                                    </Box>
                                                    <OutlinedInput
                                                        fullWidth
                                                        name="width"
                                                        inputRef={register({ required: false })}
                                                        className={classes.settingInput}
                                                        endAdornment={<InputAdornment position="end">{`(mét)`}</InputAdornment>}
                                                        value={warehouseInfo?.width}
                                                        onChange={(e) => {
                                                            setWarehouseInfo({
                                                                ...warehouseInfo,
                                                                width: e.target.value
                                                            });
                                                        }}
                                                    />
                                                </Box>
                                            </Grid>
                                        </Grid>
                                    </Box>
                                </Grid>
                            </Grid>
                        </Box>

                        {/* Thông tin chi tiết kho */}
                        <Box className={classes.boxInfor} style={{ margin: 0 }}>
                            <Typography className={classes.inforTitle} variant="h6">
                                Thông tin chi tiết Hub

                                <label>
                                    <Button
                                        variant="raised"
                                        component="span"
                                        style={{ fontSize: "18px !important", color: "#1976d2", marginLeft: 94, textTransform: "none" }}
                                    >
                                    </Button>
                                </label>

                            </Typography>

                        </Box>

                    </Box>
                </Box>
            </Fragment>
    );
}

const SCR_ID = "SCR_SMDELI_HUB";
export default withScreenSecurity(CreateHub,"SCR_SMDELI_HUB_CREATE", true);