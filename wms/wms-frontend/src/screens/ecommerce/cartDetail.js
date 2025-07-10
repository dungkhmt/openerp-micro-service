// import DeleteIcon from '@mui/icons-material/Delete';
// import { ORDER_TYPE } from 'components/constants';
// import MapIcon from '@mui/icons-material/Map';
// import { Button, Grid, IconButton, MenuItem, Modal, Select, TextField, Typography } from "@mui/material";
// import { Box } from "@mui/system";
// import useStyles from 'screens/styles';
// import Maps from 'components/map/maps';
// import { RequireStar } from 'components/common/requireStar';
// import SearchBox from 'components/map/searchBox';
// import { useForm } from "react-hook-form";
// import { request } from 'api';
// import { API_PATH } from '../apiPaths';
// import { LOCAL_STORAGE } from 'components/constants';
// import { errorNoti, successNoti } from 'utils/notification';
// import { Fragment, useState, useEffect } from 'react';
// import { useHistory } from 'react-router';
// import { convertToVNDFormat } from 'screens/utils/utils';
// import LoadingScreen from 'components/common/loading/loading';
// import withScreenSecurity from 'components/common/withScreenSecurity';

// const ItemDetail = ( { product, cartItems, setCartItems } ) => {
//   const [prevQuantity, setPrevQuantity] = useState(product.quantity);
//   const [quantity, setQuantity] = useState(product.quantity);

//   useEffect(() => {
//     const diffTotalCost = (quantity - prevQuantity) * product.priceUnit;
//     const newTotalProductCost = cartItems.totalProductCost + diffTotalCost;
//     const newTotalOrderCost = cartItems.totalOrderCost + diffTotalCost;
//     var newItems = cartItems.items;
//     for (var i = 0; i < newItems.length; i++) {
//       if (newItems[i].productId == product.productId) {
//         newItems[i].quantity = quantity;
//       }
//     }
//     setCartItems({
//       items: newItems,
//       totalProductCost: newTotalProductCost,
//       totalOrderCost: newTotalOrderCost,
//       deliveryFee: cartItems.deliveryFee
//     })
//   }, [quantity]);

//   const productDeleteHandle = () => {
//     const diffTotalCost = quantity * product.priceUnit;
//     const newTotalProductCost = cartItems.totalProductCost - diffTotalCost;
//     const newTotalOrderCost = cartItems.totalOrderCost - diffTotalCost;
//     var newItems = cartItems.items;
//     var index = -1;
//     for (var i = 0; i < newItems.length; i++) {
//       if (newItems[i].productId == product.productId) {
//         index = i;
//       }
//     }
//     newItems.splice(index, 1);
//     console.log("New items in delete butotn handle => ", newItems)
//     setCartItems({
//       items: newItems,
//       totalProductCost: newTotalProductCost,
//       totalOrderCost: newTotalOrderCost,
//       deliveryFee: cartItems.deliveryFee
//     });
//   }

//   return (
//     <Grid container spacing={3}>
//       <Grid item xs={8}>
//         <img width={"100%"} height={"100%"} src={"data:" + product?.imageContentType + ";base64," + product?.imageData} />
//       </Grid>
//       <Grid item xs={4}>
//         <Grid item
//           // justify="flex-end"
//         >
//           <IconButton onClick={productDeleteHandle}>
//             <DeleteIcon />
//           </IconButton>
//         </Grid>
//         <Grid item xs={12}>
//           <Typography variant="h6">{product.name}</Typography>
//         </Grid>
//         <Grid item xs={12}>
//           <Typography variant="h6">{"Đơn giá: " + convertToVNDFormat(product.priceUnit)}</Typography>
//         </Grid>
//         <Grid item xs={12}>
//           <Typography variant="h6">Số lượng:</Typography>
//           <TextField type="number" value={quantity} onChange={(e) => {setPrevQuantity(quantity); setQuantity(e.target.value);}}
//             InputProps={{
//               inputProps: {
//                   min: 1
//                 }
//             }} />
//         </Grid>
//       </Grid>
//     </Grid>
//   );
// }

// const CartDetail = () => {
//   const classes = useStyles();
//   const [isOpenMapModal, setOpenMapModal] = useState(false);
//   const [selectPosition, setSelectPosition] = useState(null);
//   const [cartItems, setCartItems] = useState(null);
//   const [paymentType, setPaymentType] = useState("0");
//   const [isLoading, setLoading] = useState(true);

//   const { register, errors, handleSubmit, watch, getValues } = useForm();

//   const history = useHistory();

//   useEffect(() => {
//     async function fetchData() {
//       const cartRequestBody = {
//         "items": JSON.parse(localStorage.getItem(LOCAL_STORAGE.CART_ITEMS)),
//         "longitude": selectPosition == null ? null : selectPosition?.lon,
//         "latitude": selectPosition == null ? null : selectPosition?.lat
//       };

//       await request(
//         "post",
//         API_PATH.CART,
//         (res) => {
//           setCartItems(res.data);
//           console.log("response data => ", res.data);
//         },
//         {

//         },
//         cartRequestBody
//       );

//       await request(
//         'get',
//         API_PATH.SYNC_USER,
//         (res) => {
//           console.log("Synchonized user from keycloak");
//         }
//       );

//       setLoading(false);
//     }

//     fetchData();
//   }, []);

//   const payOderHandle = (data) => {
//     const dataItems = [];
//     for (var i = 0; i < cartItems?.items.length; i++) {
//       const item = {
//         productId: cartItems?.items[i].productId ,
//         quantity: cartItems?.items[i].quantity
//       };
//       dataItems.push(item);
//     }
//     data.items = dataItems;
//     data.paymentTypeCode = paymentType;
//     data.orderTypeCode = ORDER_TYPE.ONLINE;
//     data.longitude = selectPosition?.lon;
//     data.latitude = selectPosition?.lat;
//     console.log("Data in request body => ", data);
//     request(
//       "post",
//       API_PATH.CUSTOMER_SALE_ORDER,
//       (res) => {
//         if (res.status == 200) {
//           successNoti("Tạo đơn hàng thành công");
//           // remove cart items in localstorage
//           localStorage.removeItem(LOCAL_STORAGE.CART_ITEMS);
//           history.push("/customer/products")
//         } else {
//           errorNoti("Có lỗi xảy ra khi tạo đơn hàng. Vui lòng thử lại sau");
//         }
//       },
//       {
//         500: () => errorNoti("Có lỗi xảy ra khi tạo đơn hàng. Vui lòng thử lại sau")
//       },
//       data
//     )
//   }

//   return (
//     isLoading ? <LoadingScreen /> :
//     <Fragment>
//       <Modal open={isOpenMapModal}
//         onClose={() => setOpenMapModal(!isOpenMapModal)}
//         aria-labelledby="modal-modal-title"
//         aria-describedby="modal-modal-description"
//       >
//         <Box sx={{
//           position: 'absolute',
//           top: '50%',
//           left: '50%',
//           width: '75%',
//           height: '90%',
//           transform: 'translate(-50%, -50%)',
//           bgcolor: 'background.paper',
//           border: '2px solid #000',
//           boxShadow: 24,
//           p: 4,
//         }}>
//           <Typography variant="h5">
//             Chọn vị trí nhận hàng
//             <Button variant="contained"
//             className={classes.addButton}
//             type="submit"
//             onClick={() => {
//                 setOpenMapModal(false);
//                 console.log("Selected position => ", selectPosition);
//               }} >
//               Lưu
//             </Button>
//           </Typography>

//           <div style={{
//             display: "flex",
//             flexDirection: "row",
//             width: "100%",
//             height: "100%",
//           }}>
//             <div style={{ width: "50%", height: "90%", marginRight: 10 }}>
//               <Maps selectPosition={selectPosition}
//                 setSelectPosition={setSelectPosition} />
//             </div>
//             <div style={{ width: "50%", height: "90%" }}>
//               <SearchBox selectPosition={selectPosition}
//                 setSelectPosition={setSelectPosition} />
//             </div>
//           </div>
//         </Box>
//       </Modal>

//       <Box>
//         <Grid container justifyContent="space-between" className={classes.headerBox} >
//           <Grid>
//             <Typography variant="h5">
//               {"Xem thông tin giỏ hàng"}
//             </Typography>
//           </Grid>
//           <Grid className={classes.buttonWrap}>
//             <Button variant="contained" className={classes.addButton}
//               type="submit" onClick={handleSubmit(payOderHandle)} >Mua hàng</Button>
//           </Grid>
//         </Grid>
//       </Box>

//       <Box className={classes.formWrap}>
//         <Grid container spacing={3}>
//           <Grid item xs={6}>
//             {/* Delivery info */}
//             <Box className={classes.boxInfor}>
//               <Typography variant="h6" className={classes.inforTitle}>
//                 Thông tin vận chuyển
//               </Typography>
//               {/* TODO: List all previous address info here */}
//               <Grid container spacing={3} className={classes.inforWrap}>
//                 <Grid item xs={6}>
//                   <Box className={classes.labelInput}>
//                     Tên <RequireStar />
//                   </Box>
//                   <TextField
//                     fullWidth
//                     variant="outlined"
//                     size="small"
//                     inputRef={register({ required: "Vui lòng điền tên người nhận hàng" })}
//                     name="customerName"
//                     error={!!errors.name}
//                   />
//                   {/* TODO: fetch from user_login table */}
//                 </Grid>

//                 <Grid item xs={6}>
//                   <Box className={classes.labelInput}>
//                     Số điện thoại <RequireStar />
//                   </Box>
//                   <TextField
//                     fullWidth
//                     variant="outlined"
//                     size="small"
//                     inputRef={register({ required: "Vui lòng điền số điện thoại" })}
//                     name="customerPhoneNumber"
//                     error={!!errors.name}
//                   />
//                 </Grid>

//                 <Grid item xs={12}>
//                   <Box className={classes.labelInput}>
//                     Địa chỉ nhận hàng <RequireStar />
//                     <Button style={{ "margin-bottom": 0 }}
//                       onClick={() => setOpenMapModal(!isOpenMapModal)}><MapIcon /></Button>
//                   </Box>
//                   <TextField
//                     fullWidth
//                     variant="outlined"
//                     size="small"
//                     inputRef={register({ required: true })}
//                     name="addressName"
//                     disabled
//                     value={selectPosition?.display_name}
//                   />
//                 </Grid>

//                 <Grid item xs={12}>
//                   <Box className={classes.labelInput}>
//                     Ghi chú thêm
//                   </Box>
//                   <TextField
//                     fullWidth
//                     variant="outlined"
//                     size="small"
//                     inputRef={register({ required: false })}
//                     name="description"
//                   />
//                 </Grid>
//                 <Grid item xs={12}>
//                   <Box className={classes.labelInput}>Hình thức thanh toán <RequireStar /></Box>
//                   <Select
//                     label="paymentType"
//                     value={paymentType}
//                     defaultValue={paymentType}
//                     onChange={(e) => setPaymentType(e.target.value)}
//                     fullWidth
//                   >
//                     <MenuItem value={"0"} >COD</MenuItem>
//                   </Select>
//                 </Grid>
//               </Grid>

//             </Box>
//           </Grid>
//           <Grid item xs={6}>
//             {/* Cart item listing */}
//             <Typography variant="h6" className={classes.inforTitle}>
//               Giỏ hàng
//             </Typography>
//             <Grid container spacing={3} className={classes.inforWrap}>
//               {
//                 cartItems?.items?.length > 0 &&
//                 cartItems?.items.map(item => <ItemDetail product={item} cartItems={cartItems} setCartItems={setCartItems} />)
//               }
//             </Grid>
//             <Grid>
//               <Typography variant="h6" className={classes.inforTitle}>
//                 {"Tạm tính: " + (cartItems?.totalProductCost == undefined ? 0 : convertToVNDFormat(cartItems?.totalProductCost))}
//               </Typography>
//               <Typography variant="h6" className={classes.inforTitle}>
//                 {"Phí vận chuyển: " + (cartItems?.deliveryFee == undefined ? 0 : convertToVNDFormat(cartItems?.deliveryFee))}
//               </Typography>
//               <Typography variant="h6" className={classes.inforTitle}>
//                 {"Tổng: " + (cartItems?.totalOrderCost == undefined ? 0 : convertToVNDFormat(cartItems?.totalOrderCost))}
//               </Typography>
//             </Grid>
//           </Grid>

//         </Grid>
//       </Box>
//     </Fragment>
//   )
// }

// const SCR_ID = "SCR_WMSv2_ONLINE_CUSTOMER_CART";
// export default withScreenSecurity(CartDetail, SCR_ID, true);

// By Diep
import DeleteIcon from "@mui/icons-material/Delete";
import { ORDER_TYPE } from "components/constants";
import MapIcon from "@mui/icons-material/Map";
import {
    Button,
    Grid,
    IconButton,
    MenuItem,
    Modal,
    Select,
    TextField,
    Typography,
} from "@mui/material";
import { Box } from "@mui/system";
import useStyles from "screens/styles";
import Maps from "components/map/maps";
import { RequireStar } from "components/common/requireStar";
import SearchBox from "components/map/searchBox";
import { useForm } from "react-hook-form";
import { request } from "api";
import { API_PATH, API_PATH_2 } from "../apiPaths";
import { LOCAL_STORAGE } from "components/constants";
import { errorNoti, successNoti } from "utils/notification";
import { Fragment, useState, useEffect } from "react";
import { useHistory } from "react-router";
import { convertToVNDFormat } from "screens/utils/utils";
import LoadingScreen from "components/common/loading/loading";
import withScreenSecurity from "components/common/withScreenSecurity";

const ItemDetail = ({ product, cartItems, setCartItems }) => {
    const [prevQuantity, setPrevQuantity] = useState(product.quantity);
    const [quantity, setQuantity] = useState(product.quantity);

    useEffect(() => {
        console.log("Product: ", quantity, product.maxQuantity);
        const items = JSON.parse(
            localStorage.getItem(LOCAL_STORAGE.CART_ITEMS)
        );
        var oldIndex;
        for (var i = 0; i < items?.length; i++) {
            if (items[i].productId == product.productId) {
                oldIndex = i;
                break;
            }
        }
        items.splice(oldIndex, 1);
        const newItem = {
            productId: product.productId,
            quantity: quantity,
        };
        localStorage.setItem(
            LOCAL_STORAGE.CART_ITEMS,
            JSON.stringify([...items, newItem])
        );

        const diffTotalCost = (quantity - prevQuantity) * product.priceUnit;
        const newTotalProductCost = cartItems.totalProductCost + diffTotalCost;
        const newTotalOrderCost = cartItems.totalOrderCost + diffTotalCost;
        var newItems = cartItems.items;
        for (var i = 0; i < newItems.length; i++) {
            if (newItems[i].productId == product.productId) {
                newItems[i].quantity = quantity;
            }
        }
        setCartItems({
            items: newItems,
            totalProductCost: newTotalProductCost,
            totalOrderCost: newTotalOrderCost,
            deliveryFee: cartItems.deliveryFee,
        });
    }, [quantity]);

    const productDeleteHandle = () => {
        const diffTotalCost = quantity * product.priceUnit;
        const newTotalProductCost = cartItems.totalProductCost - diffTotalCost;
        const newTotalOrderCost = cartItems.totalOrderCost - diffTotalCost;
        var newItems = cartItems.items;
        var index = -1;
        for (var i = 0; i < newItems.length; i++) {
            if (newItems[i].productId == product.productId) {
                index = i;
            }
        }
        newItems.splice(index, 1);
        console.log("New items in delete button handle => ", newItems);
        setCartItems({
            items: newItems,
            totalProductCost: newTotalProductCost,
            totalOrderCost: newTotalOrderCost,
            deliveryFee: cartItems.deliveryFee,
        });
    };

    const handleChangeCart = (newQuantity) => {
        if (newQuantity >= product.maxQuantity) {
            setPrevQuantity(quantity);
            setQuantity(product.maxQuantity);
            window.alert(
                `Chỉ có thể đặt tối đa ${product.maxQuantity} sản phẩm này!`
            );
        } else {
            setPrevQuantity(quantity);
            setQuantity(newQuantity);
        }
    };
    return (
        <Grid container spacing={3}>
            <Grid item xs={2}>
                <img
                    width={"100%"}
                    height={"100%"}
                    src={
                        "data:" +
                        product?.imageContentType +
                        ";base64," +
                        product?.imageData
                    }
                />
            </Grid>
            <Grid item xs={4}>
                <h5 style={{ fontSize: "1em", margin: "3px" }}>
                    {product.name}
                </h5>
            </Grid>
            <Grid item xs={2}>
                <h6
                    style={{
                        fontSize: "1em",
                        padding: "1px 8px",
                        marginTop: "0",
                        marginBottom: "0",
                        fontWeight: "400",
                    }}
                >
                    {"Đơn giá: " + convertToVNDFormat(product.priceUnit)}
                </h6>
            </Grid>
            <Grid item xs={2}>
                <h6
                    style={{
                        fontSize: "1em",
                        padding: "0 8px",
                        marginTop: "0",
                        marginBottom: "0",
                        fontWeight: "400",
                    }}
                >
                    Số lượng:
                </h6>
                <TextField
                    type="number"
                    value={quantity}
                    onChange={(e) => {
                        handleChangeCart(e.target.value);
                    }}
                    InputProps={{
                        inputProps: {
                            min: 1,
                            max: product.maxQuantity,
                        },
                    }}
                />
            </Grid>
            <Grid
                item
                xs={2}
                container
                justifyContent="flex-end"
                alignItems="center"
            >
                <IconButton onClick={productDeleteHandle}>
                    <DeleteIcon />
                </IconButton>
            </Grid>
        </Grid>
    );
};

const CartDetail = () => {
    const classes = useStyles();
    const [isOpenMapModal, setOpenMapModal] = useState(false);
    const [selectPosition, setSelectPosition] = useState(null);
    const [cartItems, setCartItems] = useState(null);
    const [paymentType, setPaymentType] = useState("0");
    const [isLoading, setLoading] = useState(true);

    const { register, errors, handleSubmit } = useForm();

    const history = useHistory();
    useEffect(() => {
        async function fetchData() {
            const items = JSON.parse(
                localStorage.getItem(LOCAL_STORAGE.CART_ITEMS)
            );
            const cartRequestBody = {
                items: items,
                longitude: selectPosition == null ? null : selectPosition?.lon,
                latitude: selectPosition == null ? null : selectPosition?.lat,
            };

            const productIds = items
                ? items.map((item) => item.productId.toString())
                : [];

            await request(
                "post",
                API_PATH_2.CART,
                (res) => {
                    setCartItems(res.data);
                    console.log("response data => ", res.data);
                },
                {},
                cartRequestBody
            );

            await request("get", API_PATH.SYNC_USER, (res) => {
                console.log("Synchonized user from keycloak");
            });
            console.log(
                "get items quantity: ",
                productIds,
                cartRequestBody.items
            );

            setLoading(false);
        }

        fetchData();
    }, []);

    const placeOderHandle = (data) => {
        if (cartItems.items.length > 0) {
            console.log("Starting place oder handle");
            const dataItems = [];
            for (var i = 0; i < cartItems?.items.length; i++) {
                const item = {
                    productId: cartItems?.items[i].productId,
                    quantity: cartItems?.items[i].quantity,
                };
                dataItems.push(item);
            }
            data.items = dataItems;
            data.paymentTypeCode = paymentType;
            data.orderTypeCode = ORDER_TYPE.ONLINE;
            data.longitude = selectPosition?.lon;
            data.latitude = selectPosition?.lat;
            data.deliveryFee = cartItems?.deliveryFee;
            data.totalProductCost = cartItems?.totalProductCost;
            data.totalOrderCost = cartItems?.totalOrderCost;
            console.log("Data in request body => ", data);
            request(
                "post",
                API_PATH_2.CUSTOMER_SALE_ORDER,
                (res) => {
                    if (res.status == 200) {
                        successNoti("Tạo đơn hàng thành công");
                        // remove cart items in localstorage
                        localStorage.removeItem(LOCAL_STORAGE.CART_ITEMS);
                        history.push("/customer/products");
                    } else {
                        errorNoti(
                            "Có lỗi xảy ra khi tạo đơn hàng. Vui lòng thử lại sau"
                        );
                    }
                },
                {
                    500: () =>
                        errorNoti(
                            "Có lỗi xảy ra khi tạo đơn hàng. Vui lòng thử lại sau"
                        ),
                },
                data
            );
        } else {
            window.alert("Giỏ hàng của bạn đang trống!");
            return;
        }
    };

    return isLoading ? (
        <LoadingScreen />
    ) : (
        <Fragment>
            {/* Select delivery address modal */}
            <Modal
                open={isOpenMapModal}
                onClose={() => setOpenMapModal(!isOpenMapModal)}
                aria-labelledby="modal-modal-title"
                aria-describedby="modal-modal-description"
            >
                <Box
                    sx={{
                        position: "absolute",
                        top: "50%",
                        left: "50%",
                        width: "75%",
                        height: "90%",
                        transform: "translate(-50%, -50%)",
                        bgcolor: "background.paper",
                        border: "2px solid #000",
                        boxShadow: 24,
                        p: 4,
                    }}
                >
                    <Typography variant="h5">
                        Chọn vị trí nhận hàng
                        <Button
                            variant="contained"
                            className={classes.addButton}
                            type="submit"
                            onClick={() => {
                                setOpenMapModal(false);
                                console.log(
                                    "Selected position => ",
                                    selectPosition
                                );
                            }}
                        >
                            Lưu
                        </Button>
                    </Typography>

                    <div
                        style={{
                            display: "flex",
                            flexDirection: "row",
                            width: "100%",
                            height: "100%",
                        }}
                    >
                        <div
                            style={{
                                width: "50%",
                                height: "90%",
                                marginRight: 10,
                            }}
                        >
                            <Maps
                                selectPosition={selectPosition}
                                setSelectPosition={setSelectPosition}
                            />
                        </div>
                        <div style={{ width: "50%", height: "90%" }}>
                            <SearchBox
                                selectPosition={selectPosition}
                                setSelectPosition={setSelectPosition}
                            />
                        </div>
                    </div>
                </Box>
            </Modal>

            <Box>
                <Grid
                    container
                    justifyContent="space-between"
                    className={classes.headerBox}
                >
                    <Grid>
                        <Typography variant="h5">
                            {"Xem thông tin giỏ hàng"}
                        </Typography>
                    </Grid>
                    <Grid className={classes.buttonWrap}>
                        <Button
                            variant="contained"
                            className={classes.addButton}
                            type="submit"
                            onClick={handleSubmit(placeOderHandle)}
                        >
                            Mua hàng
                        </Button>
                    </Grid>
                </Grid>
            </Box>

            {/* Thông tin giỏi hàng             */}
            <Box className={classes.formWrap}>
                <Grid item xs={6}>
                    {/* Cart item listing */}
                    <Typography variant="h6" className={classes.inforTitle}>
                        Sản phẩm
                    </Typography>
                    <Grid container spacing={3} className={classes.inforWrap}>
                        {cartItems?.items?.length > 0 &&
                            cartItems?.items.map((item) => (
                                <ItemDetail
                                    key={"item" + item.productId}
                                    product={item}
                                    cartItems={cartItems}
                                    setCartItems={setCartItems}
                                />
                            ))}
                    </Grid>
                </Grid>

                {/* Thông tin hoá đơn */}
                <Grid container>
                    <Grid
                        item
                        xs={6}
                        sm={9}
                        md={9}
                        container
                        justifyContent="flex-end"
                    >
                        <h6
                            style={{
                                fontSize: "1em",
                                padding: "2px 8px",
                                marginTop: "0",
                                marginBottom: "0",
                                fontWeight: "400",
                            }}
                        >
                            Tạm tính:
                        </h6>
                    </Grid>
                    <Grid
                        item
                        xs={6}
                        sm={3}
                        md={3}
                        container
                        justifyContent="flex-end"
                    >
                        <h6
                            style={{
                                fontSize: "1em",
                                padding: "2px 8px",
                                marginTop: "0",
                                marginBottom: "0",
                                fontWeight: "400",
                            }}
                        >
                            {cartItems?.totalProductCost == undefined
                                ? 0
                                : convertToVNDFormat(
                                      cartItems?.totalProductCost
                                  )}
                        </h6>
                    </Grid>
                </Grid>
                <Grid container>
                    <Grid
                        item
                        xs={6}
                        sm={9}
                        md={9}
                        container
                        justifyContent="flex-end"
                    >
                        <h6
                            style={{
                                fontSize: "1em",
                                padding: "2px 8px",
                                marginTop: "0",
                                marginBottom: "0",
                                fontWeight: "400",
                            }}
                        >
                            Phí vận chuyển:
                        </h6>
                    </Grid>
                    <Grid
                        item
                        xs={6}
                        sm={3}
                        md={3}
                        container
                        justifyContent="flex-end"
                    >
                        <h6
                            style={{
                                fontSize: "1em",
                                padding: "2px 8px",
                                marginTop: "0",
                                marginBottom: "0",
                                fontWeight: "400",
                            }}
                        >
                            {cartItems?.deliveryFee == undefined
                                ? 0
                                : convertToVNDFormat(cartItems?.deliveryFee)}
                        </h6>
                    </Grid>
                </Grid>
                <Grid container>
                    <Grid
                        item
                        xs={6}
                        sm={9}
                        md={9}
                        container
                        justifyContent="flex-end"
                    >
                        <h6
                            style={{
                                fontSize: "1em",
                                padding: "2px 8px",
                                marginTop: "0",
                                marginBottom: "0",
                                fontWeight: "400",
                            }}
                        >
                            Tổng:
                        </h6>
                    </Grid>
                    <Grid
                        item
                        xs={6}
                        sm={3}
                        md={3}
                        container
                        justifyContent="flex-end"
                    >
                        <h6
                            style={{
                                fontSize: "1.25em",
                                padding: "2px 8px",
                                marginTop: "0",
                                marginBottom: "0",
                                fontWeight: "600",
                                color: "#FF0000",
                            }}
                        >
                            {cartItems?.totalOrderCost == undefined
                                ? 0
                                : convertToVNDFormat(cartItems?.totalOrderCost)}
                        </h6>
                    </Grid>
                </Grid>
            </Box>
            <Box className={classes.formWrap}>
                <Grid container spacing={3}>
                    <Grid item xs={6}>
                        {/* Delivery info */}
                        <Box className={classes.boxInfor}>
                            <Typography
                                variant="h6"
                                className={classes.inforTitle}
                            >
                                Thông tin vận chuyển
                            </Typography>
                            {/* TODO: List all previous address info here */}
                            <Grid
                                container
                                spacing={3}
                                className={classes.inforWrap}
                            >
                                <Grid item xs={6}>
                                    <Box className={classes.labelInput}>
                                        Tên <RequireStar />
                                    </Box>
                                    <TextField
                                        fullWidth
                                        variant="outlined"
                                        size="small"
                                        inputRef={register({
                                            required:
                                                "Vui lòng điền tên người nhận hàng",
                                        })}
                                        name="customerName"
                                        error={!!errors.name}
                                    />
                                    {/* TODO: fetch from user_login table */}
                                </Grid>

                                <Grid item xs={6}>
                                    <Box className={classes.labelInput}>
                                        Số điện thoại <RequireStar />
                                    </Box>
                                    <TextField
                                        fullWidth
                                        variant="outlined"
                                        size="small"
                                        inputRef={register({
                                            required:
                                                "Vui lòng điền số điện thoại",
                                        })}
                                        name="customerPhoneNumber"
                                        error={!!errors.name}
                                    />
                                </Grid>

                                <Grid item xs={12}>
                                    <Box className={classes.labelInput}>
                                        Địa chỉ nhận hàng <RequireStar />
                                        <Button
                                            style={{ marginBottom: 0 }}
                                            onClick={() =>
                                                setOpenMapModal(!isOpenMapModal)
                                            }
                                        >
                                            <MapIcon />
                                        </Button>
                                    </Box>
                                    <TextField
                                        fullWidth
                                        variant="outlined"
                                        size="small"
                                        inputRef={register({ required: true })}
                                        name="addressName"
                                        disabled
                                        value={selectPosition?.display_name}
                                    />
                                </Grid>

                                <Grid item xs={12}>
                                    <Box className={classes.labelInput}>
                                        Ghi chú thêm
                                    </Box>
                                    <TextField
                                        fullWidth
                                        variant="outlined"
                                        size="small"
                                        inputRef={register({ required: false })}
                                        name="description"
                                    />
                                </Grid>
                                <Grid item xs={12}>
                                    <Box className={classes.labelInput}>
                                        Hình thức thanh toán <RequireStar />
                                    </Box>
                                    <Select
                                        label="paymentType"
                                        value={paymentType}
                                        defaultValue={paymentType}
                                        onChange={(e) =>
                                            setPaymentType(e.target.value)
                                        }
                                        fullWidth
                                    >
                                        <MenuItem value={"0"}>COD</MenuItem>
                                    </Select>
                                </Grid>
                            </Grid>
                        </Box>
                    </Grid>
                </Grid>
            </Box>
        </Fragment>
    );
};

const SCR_ID = "SCR_WMSv2_ONLINE_CUSTOMER_CART";
export default withScreenSecurity(CartDetail, SCR_ID, true);
