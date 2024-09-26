import { FETCH_PRODUCT_LIST, FETCH_PRODUCT_LIST_SUCCESS, FETCH_PRODUCT_LIST_FAILED } from "../actions/actions";

const initialState = {
    loading: false,
    productList: null,
    error: null
};

const productState = (state = initialState, action) => {
    switch (action.type) {
        case FETCH_PRODUCT_LIST:
            return {
                ...state,
                loading: true,
                error: null
            };
        case FETCH_PRODUCT_LIST_SUCCESS:
            console.log('FETCH_PRODUCT_LIST_SUCCESS: ', action.productList);
            return {
                ...state,
                loading: false,
                productList: action.productList
            };
        case FETCH_PRODUCT_LIST_FAILED:
            return {
                ...state,
                loading: false,
                error: action.error
            };
        default:
            return state;
    }
};

export default productState;