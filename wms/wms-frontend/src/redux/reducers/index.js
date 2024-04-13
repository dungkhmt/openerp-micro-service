import { combineReducers } from "redux";
import productState from "./product.reducer";

const rootReducer = combineReducers({
   productState,
});

export default rootReducer;
