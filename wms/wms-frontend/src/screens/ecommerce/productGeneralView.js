import { useRouteMatch } from "react-router-dom";
import { Box, Grid, Typography, Badge, Link, Button } from "@mui/material";
import { request } from "api";
import { Fragment, useEffect, useState } from "react";
import { API_PATH } from "../apiPaths";
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import { useHistory } from "react-router";

const ProductGeneralView = () => {
  const history = useHistory();
  const { path } = useRouteMatch();
  const [productData, setProductData] = useState([]);
  const [itemInCartCount, setItemInCartCount] = useState(0);
  
  useEffect(() => {
    async function fetchData() {
      request(
        "get",
        API_PATH.PRODUCT,
        (res) => {
          setProductData(res.data);
        }
      );
    }

    fetchData();

  }, []);

  return (
    <Fragment>
      <Grid container mb={3}>
        <Grid item xs={8}>
          <Typography variant="h4">Mua hàng online</Typography>
        </Grid>
        <Grid item xs={4} justifyContent="flex-end">
          <Button onClick={() => history.push('/wmsv2/customer/cart')}>
            <Badge color="secondary" badgeContent={itemInCartCount}>
              <ShoppingCartIcon />{" "}
            </Badge>
          </Button>
        </Grid>
      </Grid>

      <Grid container spacing={3}>
        {
          productData.length > 0 &&
          productData.map(product => 
            <Grid item xs={3}>
              <Box>
                <img src={"data:" + product?.imageContentType + ";base64," + product?.imageData} width={"100%"} height={"100%"} />
                <Typography variant="h6">{product?.name}</Typography>
                <Typography variant="h6">{"Giá bán lẻ: " + (product?.retailPrice == null ? "Liên hệ" : product?.retailPrice)}</Typography>
                <Link href={`${path}/${product.productId}`}>Xem chi tiết</Link>
              </Box>
            </Grid>
            )
        }
      </Grid>
    </Fragment>
  )
}

export default ProductGeneralView;