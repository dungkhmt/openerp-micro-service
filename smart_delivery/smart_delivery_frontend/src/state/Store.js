// src/state/store.js
import { configureStore } from '@reduxjs/toolkit';
import authReducer from './authSlice'; // Import authSlice

const Store = configureStore({
    reducer: {
        auth: authReducer, // Thêm auth vào reducer
    },
});

export default Store;
