import { configureStore } from '@reduxjs/toolkit';
import hubReducer from './slices/hubSlice';
import authReducer from './slices/authSlice'; // Assuming you have an auth slice

export const store = configureStore({
  reducer: {
    hub: hubReducer,
    auth: authReducer,
    // ... other reducers
  },
}); 