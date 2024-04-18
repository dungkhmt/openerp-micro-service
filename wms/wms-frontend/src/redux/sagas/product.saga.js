import { call, put, takeLatest } from "redux-saga/effects";
import axios from "axios";
import {
    FETCH_PRODUCT_LIST,
    FETCH_PRODUCT_LIST_SUCCESS,
    FETCH_PRODUCT_LIST_FAILED,
    fetchProductList,
    fetchProductListSuccess,
    fetchProductListFailed
} from "../actions/actions";

import { config } from "config/constant";

import { API_PATH, API_PATH_2 } from "screens/apiPaths";

async function getProductAPI() {
    const url = `${config.url.API_URL}${API_PATH_2.PRODUCT_WITHOUT_IMAGE}`;
    return axios.get(url).then((data) => {
        console.log('saga 1: ', data.data);
        if (data.status === 200 && data.data) {
            return data.data;
        }
        return [];
    });
}

function* getProductSaga(action) {
    try {
        const list = yield call(getProductAPI);
        console.log('saga 2: ', list);
        yield put(fetchProductListSuccess(list));
    } catch (error) {
        yield put(fetchProductListFailed(error));
    }
}

export function* watchProductSaga() {
    yield takeLatest(FETCH_PRODUCT_LIST, getProductSaga);
}
