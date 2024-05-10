import {createSlice} from "@reduxjs/toolkit";

const initialState = {
    isLoggedIn: false,
    token: null,
    msg: '',
    update: false
}

const authSlice = createSlice ({
    name: 'auth',
    initialState,
    reducers: {
        login_success: (state,action) => {
            state.token = action.payload;
            state.isLoggedIn = true;
            localStorage.setItem('token', action.payload);
        },

        logout_success: (state, action) => {
            state.isLoggedIn = false;
            state.token = null;
        }
    }
})

export const {
    login_success,
    logout_success
} = authSlice.actions;

export default authSlice.reducer;