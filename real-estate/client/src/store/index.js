import { configureStore, combineReducers } from '@reduxjs/toolkit';import authReducer from './auth';
import storage from 'redux-persist/lib/storage';
import { persistReducer } from 'redux-persist';
import accountReducer from './account'


const persistConfigOne = {
    key: 'youtubeKey',
    storage
};

const myCombinedReducer = combineReducers({
    auth: authReducer,
    account: accountReducer
})

const localStorageReducer = persistReducer(persistConfigOne, myCombinedReducer);
export const store = configureStore({
    reducer: localStorageReducer
});