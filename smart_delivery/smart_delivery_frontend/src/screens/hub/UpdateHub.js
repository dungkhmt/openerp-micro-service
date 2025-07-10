import React, { Fragment, useEffect, useState, useRef } from 'react';
import { useForm } from "react-hook-form";
import { useHistory, useParams, useLocation } from "react-router-dom"; // Add useLocation
import { Button, Box, Grid, InputAdornment, OutlinedInput, TextField, Typography, Modal } from '@mui/material';
import MapIcon from '@mui/icons-material/Map';
import { request } from 'api';
import { errorNoti, successNoti } from 'utils/notification';
import useStyles from 'screens/styles.js';
import SearchBox from 'components/map/searchBox.js';
import Maps from 'components/map/map';
import LoadingScreen from 'components/common/loading/loading.js';
import { StandardTable } from "erp-hust/lib/StandardTable";
import { Stage, Layer, Rect } from "react-konva";
import { API_PATH } from '../apiPaths.js';

const UpdateHub = () => {
    const classes = useStyles();
    const [listShelf, setListShelf] = useState([]);
    const [canvanData, setCanvasData] = useState([]);
    const [scale, setScale] = useState();
    const { register, errors, handleSubmit, watch, getValues } = useForm();
    const [pos, setPos] = useState();
    const [shelf, setShelf] = useState();
    const stageCanvasRef = useRef();
    const [width, setWidth] = useState();
    const [warehouseHeight, setWarehouseHeight] = useState();
    const [height, setHeight] = useState();
    const [openModal, setOpenModal] = useState(false);
    const [selectPosition, setSelectPosition] = useState(null);
    const { id: warehouseId } = useParams();
    const [warehouseInfo, setWarehouseInfo] = useState(null);
    const history = useHistory();
    const location = useLocation(); // Get current location
    const [updateAddress, setUpdateAddress] = useState(false);
    const [productTableData, setProductTableData] = useState([]);
    const [isLoading, setLoading] = useState(true);

    // Determine if we're in view mode or update mode
    const isViewMode = location.pathname.includes('/view/');

    useEffect(() => {
        const fetchData = async () => {
            await request(
                "get",
                `${API_PATH.HUB}/${warehouseId}`,
                (res) => {
                    setWarehouseInfo(res.data);
                },
                {
                    401: () => { },
                    503: () => { errorNoti("Có lỗi khi tải dữ liệu của kho") }
                }
            );
            await request(
                "get",
                `${API_PATH.PRODUCT_WAREHOUSE}/${warehouseId}`,
                (res) => {
                    setProductTableData(res.data.products);
                },
                {
                    401: () => { },
                    503: () => { errorNoti("Có lỗi khi tải dữ liệu của kho") }
                }
            );
            setLoading(false);
        }
        if (warehouseId) {
            fetchData();
        } else {
            setLoading(false);
        }
    }, [warehouseId]);

    useEffect(() => {
        if (selectPosition) {
            setUpdateAddress(true);
            console.log("Update address is set to true");
        }
    }, [selectPosition]);

    const updateHub = (data) => {
        // If in view mode, don't allow updates
        if (isViewMode) {
            return;
        }

        data.longitude = updateAddress ? selectPosition.lon : warehouseInfo.longitude;
        data.latitude = updateAddress ? selectPosition.lat : warehouseInfo.latitude;
        data.id = warehouseId; // Set the hub ID for updating
        console.log("Data to be updated -> ", data);
        request(
            "put", // Use PUT for updating existing resources
            `${API_PATH.UPDATE_HUB}`, // API path for updating the hub
            (res) => {
                successNoti("Cập nhật kho thành công");
                // Navigate back to list after successful update
                history.push('/hubmanager/hub');
            },
            {
                401: () => { errorNoti("Có lỗi xảy ra. Vui lòng thử lại sau") },
                400: (e) => { errorNoti("Có lỗi xảy ra. Vui lòng thử lại sau") }
            },
            data
        );
    };

    const addFile = (e) => {
        // Only allow file upload in update mode
        if (isViewMode) return;

        e.preventDefault();
        const reader = new FileReader();
        reader.readAsText(e.target.files[0], "UTF-8");
        reader.onload = (e) => {
            const text = e.target.result;
            setListShelf(JSON.parse(text)); // Adjust this to your data structure
        };
    };

    // Function to render the header based on mode
    const renderHeader = () => {
        return (
            <Grid container justifyContent="space-between" className={classes.headerBox}>
                <Grid>
                    <Typography variant="h5">
                        {isViewMode ? "Chi tiết Hub" : "Cập nhật Hub"}
                    </Typography>
                </Grid>
                <Grid className={classes.buttonWrap}>
                    {!isViewMode && (
                        <Button variant="contained" className={classes.addButton}
                                type="submit" onClick={handleSubmit(updateHub)}>Lưu</Button>
                    )}
                    {isViewMode && (
                        <Button variant="contained" className={classes.addButton}
                                onClick={() => history.push(`/hubmanager/hub/update/${warehouseId}`)}>
                            Chỉnh sửa
                        </Button>
                    )}
                    <Button variant="outlined" style={{ marginLeft: '10px' }}
                            onClick={() => history.push('/hubmanager/hub')}>
                        Quay lại
                    </Button>
                </Grid>
            </Grid>
        );
    };

    return (
        isLoading ? <LoadingScreen /> :
            <Fragment>
                <Modal open={openModal && !isViewMode} // Only allow map modal in update mode
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
                            Chọn vị trí kho
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
                <Box className={classes.warehousePage}>
                    {renderHeader()}
                    <Box className={classes.bodyBox}>
                        <Box className={classes.formWrap} component="form" onSubmit={handleSubmit(updateHub)}>
                            {/* Basic Hub Information */}
                            <Box>
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
                                                            value={warehouseInfo?.name || ""}
                                                            onChange={(e) => {
                                                                if (!isViewMode) {
                                                                    setWarehouseInfo({
                                                                        ...warehouseInfo,
                                                                        name: e.target.value
                                                                    });
                                                                }
                                                            }}
                                                            disabled={isViewMode}
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
                                                            value={warehouseInfo?.code || ""}
                                                            onChange={(e) => {
                                                                if (!isViewMode) {
                                                                    setWarehouseInfo({
                                                                        ...warehouseInfo,
                                                                        code: e.target.value
                                                                    });
                                                                }
                                                            }}
                                                            disabled={isViewMode}
                                                        />
                                                    </Box>
                                                </Grid>
                                                <Grid item xs={12}>
                                                    <Box className={classes.inputWrap}>
                                                        <Box className={classes.labelInput}>
                                                            Địa chỉ {!isViewMode && <Button style={{ "margin-bottom": 0 }}
                                                                                            onClick={() => setOpenModal(!openModal)}><MapIcon /></Button>}
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
                                                            value={warehouseInfo?.length || ""}
                                                            onChange={(e) => {
                                                                if (!isViewMode) {
                                                                    setWarehouseInfo({
                                                                        ...warehouseInfo,
                                                                        length: e.target.value
                                                                    });
                                                                }
                                                            }}
                                                            disabled={isViewMode}
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
                                                            value={warehouseInfo?.width || ""}
                                                            onChange={(e) => {
                                                                if (!isViewMode) {
                                                                    setWarehouseInfo({
                                                                        ...warehouseInfo,
                                                                        width: e.target.value
                                                                    });
                                                                }
                                                            }}
                                                            disabled={isViewMode}
                                                        />
                                                    </Box>
                                                </Grid>
                                            </Grid>
                                        </Box>
                                    </Grid>
                                </Grid>
                            </Box>
                            {/* Detailed Hub Information */}
                            <Box className={classes.boxInfor} style={{ margin: 0 }}>
                                <Typography className={classes.inforTitle} variant="h6">
                                    Thông tin chi tiết kho
                                    {!isViewMode && (
                                        <>
                                            <input
                                                style={{ display: 'none' }}
                                                id="raised-button-file"
                                                onChange={addFile}
                                                type="file"
                                            />
                                            <label>
                                                <Button
                                                    variant="raised"
                                                    component="span"
                                                    style={{ fontSize: "18px !important", color: "#1976d2", marginLeft: 94, textTransform: "none" }}
                                                >
                                                </Button>
                                            </label>
                                        </>
                                    )}
                                </Typography>
                                <Grid container className={classes.detailWrap}>
                                    <Grid xs={3} sx={{ display: "flex", }} item className={classes.boxWrap}>
                                        <Typography className={classes.inforTitle} style={{ fontWeight: 500 }}>
                                            Cán bộ quản lý Hub:
                                        </Typography>
                                    </Grid>
                                    <Grid xs={9} item sx={{ display: "flex", }} className={classes.boxWrap}>
                                        <Box sx={{
                                            padding: 5,
                                            paddingTop: 0,
                                            height: warehouseHeight,
                                            width: "100%",
                                            display: "flex",
                                            alignItems: "center",
                                            justifyContent: "center",
                                        }}>
                                            <Box className={classes.stageWrap} ref={stageCanvasRef} >
                                                <Stage
                                                    width={width}
                                                    height={height}
                                                >
                                                    <Layer>
                                                        <Rect
                                                            width={width}
                                                            height={warehouseHeight}
                                                            x={0} y={0}
                                                            fill="#FFFEFA"
                                                            strokeWidth={3}
                                                            stroke="#89C4FA"
                                                            cornerRadius={3}
                                                        />
                                                    </Layer>
                                                </Stage>
                                                {
                                                    shelf && pos &&
                                                    <Typography style={{
                                                        position: "absolute",
                                                        top: pos.y + 8 + "px",
                                                        left: pos.x + 8 + "px",
                                                        padding: "4px",
                                                        boxShadow: "0px 2px 4px rgb(168 168 168 / 25%)",
                                                        borderRadius: 3,
                                                        background: "#FFF", }}>
                                                        Kệ {listShelf[shelf - 1].code}
                                                    </Typography>
                                                }
                                            </Box>
                                        </Box>
                                    </Grid>
                                </Grid>
                            </Box>
                            {/* Product Information (if any) */}
                            {productTableData.length > 0 &&
                                <Box mt={2} className={classes.boxInfor}>
                                    <StandardTable
                                        title={"Danh sách hàng tồn kho"}
                                        columns={
                                            [
                                                { title: "Tên hàng", field: "productName" },
                                                { title: "Số lượng", field: "quantity" },
                                                { title: "Số lô", field: "lotId" },
                                                { title: "Kệ hàng", field: "bayCode" },
                                                { title: "Giá nhập (VNĐ)", field: "importPrice" },
                                            ]
                                        }
                                        data={productTableData}
                                        options={{
                                            pageSize: 5,
                                            search: true,
                                            sorting: true,
                                        }}
                                    />
                                </Box>}
                        </Box>
                    </Box>
                </Box>
            </Fragment>
    );
}

export default UpdateHub;