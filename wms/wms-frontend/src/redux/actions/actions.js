export const FETCH_PRODUCT_LIST = "FETCH_PRODUCT_LIST";
export const FETCH_PRODUCT_LIST_SUCCESS = "FETCH_PRODUCT_LIST_SUCCESS";
export const FETCH_PRODUCT_LIST_FAILED = "FETCH_PRODUCT_LIST_FAILED";

export const FETCH_WAREHOUSE_LIST = "FETCH_WAREHOUSE_LIST";
export const FETCH_WAREHOUSE_LIST_SUCCESS = "FETCH_WAREHOUSE_LIST_SUCCESS";
export const FETCH_WAREHOUSE_LIST_FAILED = "FETCH_WAREHOUSE_LIST_FAILED";

export const fetchProductList = () => {
    console.log("Fetch Product List action");
    return {
        type: FETCH_PRODUCT_LIST,
    }
};
export const fetchProductListSuccess = (productList) => ({
    type: FETCH_PRODUCT_LIST_SUCCESS,
    productList,
});
export const fetchProductListFailed = (error) => ({
    type: FETCH_PRODUCT_LIST_FAILED,
    error,
});
