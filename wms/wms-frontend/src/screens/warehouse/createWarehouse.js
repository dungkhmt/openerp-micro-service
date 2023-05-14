import SearchBox from 'components/map/searchBox.js';
import Maps from 'components/map/maps.js';
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
import { API_PATH } from '../apiPaths.js';
// import { warehouse } from 'config/menuconfig/warehouse.js'; ==> ?
import { useRouteMatch } from "react-router-dom";
import { useHistory } from "react-router";
import StandardTable from 'components/StandardTable';
import withScreenSecurity from 'components/common/withScreenSecurity.js';
import LoadingScreen from 'components/common/loading/loading.js';

const CreateWarehouse = ( props, { screenAuthorization } ) => {
  const holderShelf = { x: "", y: "", width: "", length: "", num: "" };
  const classes = useStyles();
  const [listShelf, setListShelf] = useState([holderShelf]);
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
  const warehouseId = props.match?.params?.id;
  const [warehouseInfo, setWarehouseInfo] = useState(null);
  const isCreateForm = warehouseId == null;
  const history = useHistory();
  const { path } = useRouteMatch();
  // Use to determine what value of Address text field
  // if updateAddress = true -> Address text field = new select address from map
  // else -> current warehouse address or null
  const [updateAddress, setUpdateAddress] = useState(false);
  const [productTableData, setProductTableData] = useState([]);
  const [isLoading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      await request(
        "get",
        API_PATH.WAREHOUSE + "/" + warehouseId,
        (res) => {
          setWarehouseInfo(res.data);
          setListShelf([...res.data.listShelf, ...listShelf]);
        },
        {
          401: () => { },
          503: () => { errorNoti("Có lỗi khi tải dữ liệu của kho") }
        }
      );
      await request(
        "get",
        API_PATH.PRODUCT_WAREHOUSE + "/" + warehouseId,
        ( res ) => {
          setProductTableData(res.data.products);
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

  const handleAddShelf = () => {
    setListShelf([...listShelf, holderShelf])
  }

  let removeFormFields = (i) => {
    let newFormValues = [...listShelf];
    newFormValues.splice(i, 1);
    setListShelf(newFormValues)
  }

  const addWareHouse = () => {
  }

  const addFile = (e) => {
    e.preventDefault();
    const reader = new FileReader();
    reader.readAsText(e.target.files[0], "UTF-8");
    reader.onload = (e) => {
      const text = e.target.result;
      setListShelf(JSON.parse(text))
    };
  };

  let submitForm = (data) => {
    data.listShelf = listShelf.filter((element) => 
      JSON.stringify(element) !== JSON.stringify(holderShelf));
    data.longitude = updateAddress ? selectPosition.lon : warehouseInfo.longitude;
    data.latitude = updateAddress ? selectPosition.lat : warehouseInfo.latitude;
    data.id = isCreateForm ? null : warehouseId;
    console.log("Data in request body -> ", data);
    request(
      "put",
      API_PATH.WAREHOUSE,
      (res) => {
        successNoti(isCreateForm ? "Tạo kho thành công" : "Cập nhật kho thành công");
        // history.push(`${path.replace('/create', '')}/${id}`);
        if (isCreateForm) {
          history.push(`${path.replace('/create', '')}`);
        } else {
          history.push(`${path.substring(0, path.indexOf('/update'))}`);
        }
      },
      {
        401: () => { },
        400: (e) => { errorNoti(e.response.data.errors[0].message); }
      },
      data
    );
  };

  const handleChange = (i, e) => {
    const { name, value } = e.target;
    const list = [...listShelf];
    list[i][name] = value;
    // list[i]["num"] = i + 1;
    setListShelf(list);
  };

  const resetCanvas = () => {
    const data = getValues();
    const width = parseInt(data.warehouseWidth)
    const length = parseInt(data.warehouseLength)
    if (isNaN(width) || isNaN(length) || width <= 0 || length <= 0) {
      errorNoti(
        "Vui lòng điền kích thước kho"
      );
    }
    setCanvasData(listShelf)
    // const data = getValues();
    if (stageCanvasRef.current) {
      setWidth(stageCanvasRef.current.offsetWidth);
      setHeight(stageCanvasRef.current.offsetHeight);
      setWarehouseHeight(stageCanvasRef.current.offsetWidth 
        * data.warehouseLength / data.warehouseWidth)
      setScale(stageCanvasRef.current.offsetWidth / data.warehouseWidth)
    }
  };

  const warehouseBox = (data, maxSize) => {
    return (
      <Rect
        key={data.shelfID}
        x={setCanvasSize(data.x, maxSize)}
        y={setCanvasSize(data.y, maxSize)}
        width={setCanvasSize(data.width, maxSize)}
        height={setCanvasSize(data.length, maxSize)}
        fill={"#87CEFA"}
        stroke='#1976d2'
        strokeWidth={2}
        onMouseEnter={e => {
          e.target._clearCache();
          let mousePos = e.target.getAbsolutePosition();
          setPos(mousePos)
          setShelf(e.target.index)
        }}
        onMouseOut={e => {
          setPos(null)
          setShelf(null)
        }}
      />
    );
  }

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
      <Box className={classes.warehousePage} >
        <Grid container justifyContent="space-between" 
          className={classes.headerBox} >
          <Grid>
            <Typography variant="h5">
              {isCreateForm ? "Tạo mới kho" : "Xem thông tin kho"}
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
                          Chiều dài
                        </Box>
                        <OutlinedInput
                          fullWidth
                          inputRef={register({ required: false })}
                          name="warehouseLength"
                          className={classes.settingInput}
                          endAdornment={<InputAdornment position="end">{`(mét)`}</InputAdornment>}
                          value={warehouseInfo?.warehouseLength}
                          onChange={(e) => {
                            setWarehouseInfo({
                              ...warehouseInfo,
                              warehouseLength: e.target.value
                            });
                          }}
                        />
                      </Box>
                    </Grid>
                    <Grid item xs={12}>
                      <Box className={classes.inputWrap}>
                        <Box className={classes.labelInput}>
                          Chiều rộng
                        </Box>
                        <OutlinedInput
                          fullWidth
                          name="warehouseWidth"
                          inputRef={register({ required: false })}
                          className={classes.settingInput}
                          endAdornment={<InputAdornment position="end">{`(mét)`}</InputAdornment>}
                          value={warehouseInfo?.warehouseWidth}
                          onChange={(e) => {
                            setWarehouseInfo({
                              ...warehouseInfo,
                              warehouseWidth: e.target.value
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
          <Box className={classes.boxInfor} style={{ margin: 0 }}>
            <Typography className={classes.inforTitle} variant="h6">
              Thông tin chi tiết kho
              <input
                style={{ display: 'none' }}
                id="raised-button-file"
                onChange={addFile}
                type="file"
              />
              <label htmlFor="raised-button-file">
                <Button 
                  variant="raised" 
                  component="span" 
                  style={{ fontSize: "18px !important", color: "#1976d2", marginLeft: 94, textTransform: "none" }}>
                  Tải file lên
                </Button>
              </label>
              <label>
                <Button 
                  variant="raised" 
                  component="span" 
                  style={{ fontSize: "18px !important", color: "#1976d2", marginLeft: 94, textTransform: "none" }}
                  onClick={() => handleAddShelf()}
                >
                  Thêm kệ hàng
                </Button>
              </label>
              {/* <Box className={classes.addIconBox} onClick={() => handleAddShelf()}>
                  <Box className={classes.addIconWrap} >
                    <AddCircleOutlineIcon className={classes.addIcon} />
                    <Typography>Thêm kệ hàng</Typography>
                  </Box>
                </Box> */}
            </Typography>
            <Grid container className={classes.detailWrap}>
              <Grid xs={3} sx={{ display: "flex", }} item className={classes.boxWrap}>
                <Typography className={classes.inforTitle} style={{ fontWeight: 500 }}>
                  Danh sách kệ hàng
                </Typography>
                <Box className={classes.listWrap}>
                  {
                    listShelf.map((data, index) => (
                      <Box key={index} width={"100%"} display={"flex"} alignItems={"center"} padding={"8px"} >
                        <Box className={classes.shelfInput} style={{ flexGrow: 1 }} display={"flex"} padding={"8px"}>
                          <Box width={"75px"}>
                            <Typography style={{ fontWeight: 500 }}>Kệ số {data.num}</Typography>
                          </Box>
                          <Box style={{ width: `calc(100% - 75px` }} className={classes.rootInput}>
                            <Grid container spacing={1} >
                              <Grid item xs={6} >
                                <TextField 
                                  variant="outlined" 
                                  size="small" 
                                  label="Tọa độ x" 
                                  fullWidth 
                                  name="x" 
                                  value={data.x} 
                                  onChange={e => handleChange(index, e)} />
                              </Grid>
                              <Grid item xs={6} >
                                <TextField 
                                  variant="outlined" 
                                  size="small" 
                                  label="Chiều dài" 
                                  fullWidth 
                                  name={`width`} 
                                  value={data.width} 
                                  onChange={e => handleChange(index, e)} />
                              </Grid>
                              <Grid item xs={6} >
                                <TextField 
                                  variant="outlined" 
                                  size="small" 
                                  label="Tọa độ y"
                                   fullWidth 
                                   name={`y`} 
                                   value={data.y} 
                                   onChange={e => handleChange(index, e)} />
                              </Grid>
                              <Grid item xs={6} >
                                <TextField 
                                  variant="outlined" 
                                  size="small" 
                                  label="Chiều Rộng" 
                                  fullWidth 
                                  name={`length`} 
                                  value={data.length} 
                                  onChange={e => handleChange(index, e)} />
                              </Grid>
                              <Grid item xs={6}>
                                <TextField 
                                  variant="outlined" 
                                  size="small" 
                                  label="Code" 
                                  fullWidth 
                                  name={`code`} 
                                  value={data.code} 
                                  onChange={e => handleChange(index, e)} />
                              </Grid>
                            </Grid>

                          </Box>
                          <Box 
                            className={classes.removeIconBox} 
                            onClick={() => removeFormFields(index)}  >
                            <HighlightOffIcon className={classes.removeIcon} />
                          </Box>
                        </Box>
                      </Box>
                    ))
                  }
                </Box>
              </Grid>

              <Grid xs={9} item sx={{ display: "flex", }} className={classes.boxWrap}>
                <Box className={classes.titleWap} >
                  <Typography style={{ fontWeight: 500 }}>
                    Mô phỏng
                  </Typography>
                  <Box className={classes.rerloadIconBox} onClick={resetCanvas}>
                    <Box className={classes.reloadIconWrap} >
                      <CachedIcon style={{ color: "#1976d2" }} />
                      <Typography>Tải lại</Typography>
                    </Box>
                  </Box>
                </Box>
                <Box className={classes.canvasWrap}>
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
                        {canvanData.map((data) => (
                          warehouseBox(data, scale)
                        ))}
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

          {!isCreateForm &&
          <Box className={classes.boxInfor}>
            <StandardTable
              title={"Danh sách hàng tồn kho"}
              columns={
                [
                  { title: "Tên hàng", field: "productName" },
                  { title: "Số lượng", field: "quantity" },
                  { title: "Số lô", field: "lotId" },
                  { title: "Kệ hàng", field: "bayCode" },
                  { title: "Giá nhập (VNĐ)", field: "importPrice" },
                  // { title: "Giá bán (VNĐ)", field: "exportPrice" }
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
    </Fragment>
  );
}

const SCR_ID = "SCR_WMSv2_WAREHOUSE";
export default withScreenSecurity(CreateWarehouse, SCR_ID, true);