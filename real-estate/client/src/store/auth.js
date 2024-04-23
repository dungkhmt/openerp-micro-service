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
            state.token = action.data;
            state.isLoggedIn = true;
        }
    }
})

export const {
    login_success
} = authSlice.actions;

export default authSlice.reducer;