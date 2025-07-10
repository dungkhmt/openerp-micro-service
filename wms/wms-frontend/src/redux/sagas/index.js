import { all } from "redux-saga/effects";
import { watchProductSaga } from "./product.saga";

export default function* rootSaga() {
    yield all([
        watchProductSaga(),
    ]);
}