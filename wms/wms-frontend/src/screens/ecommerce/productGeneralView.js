// import { useRouteMatch } from "react-router-dom";
// import { Box, Grid, Typography, Badge, Link, Button, List, ListItemButton, ListItemText, Pagination } from "@mui/material";
// import { request } from "api";
// import { Fragment, useEffect, useState } from "react";
// import { API_PATH } from "../apiPaths";
// import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
// import { useHistory } from "react-router";
// import { convertToVNDFormat } from "screens/utils/utils";
// import LoadingScreen from "components/common/loading/loading";
// import withScreenSecurity from "components/common/withScreenSecurity";

// const ProductCategories = ({ productCategories, setProductData, allProductData }) => {
//   const [selected, setSelected] = useState(null);

//   const onClickListItemButton = (event, categoryId) => {
//     setSelected(categoryId);
//     if (categoryId == null) {
//       setProductData(allProductData);
//     } else {
//       const filteredProductData = allProductData.filter(product =>  product.productCategoryId == categoryId);
//       setProductData(filteredProductData);
//     }
//   }

//   const flexContainer = {
//     display: 'flex',
//     flexDirection: 'row',
//     padding: 0,
//   };

//   return (
//     <Box>
//       <List style={flexContainer}>
//         <ListItemButton
//             selected={selected == null}
//             onClick={(e) => onClickListItemButton(e, null)}>
//           <Typography variant="h6">Tất cả</Typography>
//         </ListItemButton>
//         {
//           productCategories != null && productCategories.length > 0 &&
//           productCategories.map(category =>
//             <ListItemButton
//               selected={category.categoryId == selected}
//               onClick={(e) => onClickListItemButton(e, category.categoryId)}>
//               <Typography variant="h6">{category.name}</Typography>
//             </ListItemButton>)
//         }
//       </List>
//     </Box>
//   )
// }

// const ProductGeneralView = () => {
//   const history = useHistory();
//   const { path } = useRouteMatch();
//   const [productData, setProductData] = useState([]);
//   const [allProductData, setAllProductData] = useState([]);
//   const [itemInCartCount, setItemInCartCount] = useState(0);
//   const [isLoading, setLoading] = useState(true);
//   const [productCategories, setProductCategories] = useState([]);

//   const productPerPage = 12;
//   const [pageCount, setPageCount] = useState(0);
//   const [startProductIndexInPage, setStartProductIndexInPage] = useState(0);

//   useEffect(() => {
//     async function fetchData() {
//       await request(
//         "get",
//         API_PATH.PRODUCT,
//         (res) => {
//           setProductData(res.data);
//           setAllProductData(res.data);
//           console.log("Page count => ", parseInt(Math.ceil(res.data.length / productPerPage)));
//           setPageCount(parseInt(Math.ceil(res.data.length / productPerPage)));
//         }
//       );

//       await request(
//         "get",
//         API_PATH.PRODUCT_CATEGORY,
//         (res) => {
//           setProductCategories(res.data);
//         }
//       )

//       setLoading(false);
//     }

//     fetchData();

//   }, []);

//   useEffect(() => {
//     setPageCount(parseInt(productData.length / productPerPage));
//     setStartProductIndexInPage(0);
//   }, [productData]);

//   const handlePageChange = (event, value) => {
//     setStartProductIndexInPage( (parseInt(value) - 1) * productPerPage + 1 );
//   }

//   return (
//     isLoading ? <LoadingScreen /> :
//     <Fragment>
//       <Grid container mb={3}>
//         <Grid item xs={8}>
//           <Typography variant="h4">Mua hàng online</Typography>
//         </Grid>
//         <Grid item xs={4} justifyContent="flex-end">
//           <Button onClick={() => history.push('/customer/cart')}>
//             <Badge color="secondary" badgeContent={itemInCartCount}>
//               <ShoppingCartIcon />{" "}
//             </Badge>
//           </Button>
//         </Grid>
//       </Grid>

//       <ProductCategories
//         productCategories={productCategories}
//         setProductData={setProductData}
//         allProductData={allProductData}
//       />

//       <Grid container spacing={3}>
//         {
//           productData.length > 0 &&
//           productData.slice(startProductIndexInPage, startProductIndexInPage + productPerPage)
//                      .map(product =>
//             <Grid item xs={3}>
//               <Box>
//                 <img src={"data:" + product?.imageContentType + ";base64," + product?.imageData} width={"100%"} height={"100%"} />
//                 <Typography variant="h6">{product?.name}</Typography>
//                 <Typography variant="h6">{"Giá bán lẻ: " + (product?.retailPrice == null ? "Liên hệ" : convertToVNDFormat(product?.retailPrice))}</Typography>
//                 <Link href={`${path}/${product.productId}`}>Xem chi tiết</Link>
//               </Box>
//             </Grid>
//             )
//         }
//       </Grid>

//       <Pagination count={pageCount} onChange={handlePageChange} />
//     </Fragment>
//   )
// }

// const SCR_ID = "SCR_WMSv2_ONLINE_CUSTOMER_PRODUCT_GENERAL";
// export default withScreenSecurity(ProductGeneralView, SCR_ID, true);

// By Diep
import { useRouteMatch } from "react-router-dom";
import {
    Box,
    Grid,
    Typography,
    Badge,
    Button,
    List,
    ListItemButton,
    Pagination,
    Tabs,
    Tab,
} from "@mui/material";
import { request } from "api";
import { Fragment, useEffect, useState } from "react";
import { API_PATH, API_PATH_2 } from "../apiPaths";
import ShoppingCartIcon from "@mui/icons-material/ShoppingCart";
import { useHistory } from "react-router";
import { convertToVNDFormat } from "screens/utils/utils";
import LoadingScreen from "components/common/loading/loading";
import withScreenSecurity from "components/common/withScreenSecurity";
import "./ProductGeneralView.css";

const ProductCategories = ({
    productCategories,
    setProductData,
    allProductData,
}) => {
    const [selected, setSelected] = useState("0");

    const handleChangeSelectedTab = (event, newValue) => {
        setSelected(newValue);
        if (newValue == 0 || newValue == null) {
            setProductData(allProductData);
        } else {
            const filteredProductData = allProductData.filter(
                (product) => product.productCategoryId == newValue
            );
            setProductData(filteredProductData);
        }
    };

    return (
        <Box
            sx={{ borderBottom: 1, borderColor: "divider", width: "100%" }}
            marginBottom="8px"
        >
            <Tabs
                value={selected}
                onChange={handleChangeSelectedTab}
                variant="scrollable"
                scrollButtons
                allowScrollButtonsMobile
                aria-label="scrollable force tabs example"
                sx={{
                    "& .MuiTab-root": { fontSize: "1em" },
                }}
            >
                <Tab label="Tất cả" value="0" key="0" />
                {productCategories != null &&
                    productCategories.length > 0 &&
                    productCategories.map((category) => (
                        <Tab
                            label={category.name}
                            value={category.categoryId}
                            key={category.categoryId}
                        />
                    ))}
            </Tabs>
        </Box>
    );
};

const ProductGeneralView = () => {
    const history = useHistory();
    const { path } = useRouteMatch();
    const [productData, setProductData] = useState([]);
    const [allProductData, setAllProductData] = useState([]);
    const [itemInCartCount, setItemInCartCount] = useState(0);
    const [isLoading, setLoading] = useState(true);
    const [productCategories, setProductCategories] = useState([]);

    const productPerPage = 12;
    const [pageCount, setPageCount] = useState(0);
    const [startProductIndexInPage, setStartProductIndexInPage] = useState(0);

    useEffect(() => {
        async function fetchData() {
            await request("get", API_PATH_2.PRODUCT, (res) => {
                setProductData(res.data);
                setAllProductData(res.data);
                console.log(
                    "Page count => ",
                    parseInt(Math.ceil(res.data.length / productPerPage))
                );
                setPageCount(
                    parseInt(Math.ceil(res.data.length / productPerPage))
                );
            });

            await request("get", API_PATH_2.PRODUCT_CATEGORY, (res) => {
                setProductCategories(res.data);
            });

            setLoading(false);
        }

        fetchData();
    }, []);

    useEffect(() => {
        setPageCount(parseInt(productData.length / productPerPage));
        setStartProductIndexInPage(0);
    }, [productData]);

    const handlePageChange = (event, value) => {
        setStartProductIndexInPage((parseInt(value) - 1) * productPerPage + 1);
    };

    return isLoading ? (
        <LoadingScreen />
    ) : (
        <Fragment>
            <Grid container mb={3}>
                <Grid item xs={8}>
                    <Typography variant="h4">Mua hàng online</Typography>
                </Grid>
                <Grid item xs={4} container justifyContent="flex-end">
                    <Button onClick={() => history.push("/customer/cart")}>
                        <Badge color="secondary" badgeContent={itemInCartCount}>
                            <ShoppingCartIcon />{" "}
                        </Badge>
                    </Button>
                </Grid>
            </Grid>

            <ProductCategories
                productCategories={productCategories}
                setProductData={setProductData}
                allProductData={allProductData}
            />

            <Grid container spacing={3}>
                {productData.length > 0 &&
                    productData
                        .slice(
                            startProductIndexInPage,
                            startProductIndexInPage + productPerPage
                        )
                        .map((product) => (
                            <Grid item xs={12} sm={6} md={3}>
                                <div
                                    className="product-item"
                                    style={{ cursor: "pointer" }}
                                    onClick={() => {
                                        console.log(
                                            "detail: " + product.productId
                                        );
                                        history.push(
                                            `${path}/${product.productId}`
                                        );
                                    }}
                                >
                                    <img
                                        src={
                                            "data:" +
                                            product?.imageContentType +
                                            ";base64," +
                                            product?.imageData
                                        }
                                        width={"100%"}
                                        height={"100%"}
                                    />
                                    <h5
                                        style={{
                                            fontSize: "1em",
                                            padding: "0 8px",
                                            marginBottom: "0",
                                            fontWeight: "400",
                                        }}
                                    >
                                        {product?.name}
                                    </h5>
                                    <h6
                                        style={{
                                            fontSize: "1em",
                                            padding: "8px 8px",
                                            marginTop: "0",
                                            marginBottom: "0",
                                            fontWeight: "400",
                                            color: "#1976D2"
                                        }}
                                    >
                                        <span style={{ color: "black" }}>
                                            {"Giá bán lẻ: "}
                                        </span>
                                        {product?.retailPrice == null
                                            ? "Liên hệ"
                                            : convertToVNDFormat(
                                                  product?.retailPrice
                                              )}
                                    </h6>
                                </div>
                            </Grid>
                        ))}
            </Grid>

            <Pagination count={pageCount} onChange={handlePageChange} />
        </Fragment>
    );
};

const SCR_ID = "SCR_WMSv2_ONLINE_CUSTOMER_PRODUCT_GENERAL";
export default withScreenSecurity(ProductGeneralView, SCR_ID, true);
