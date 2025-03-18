import { createSlice } from "@reduxjs/toolkit";
import {jwtDecode} from "jwt-decode";

const authSlice = createSlice({
    name: "auth",
    initialState: {
        hubId: null,
        email: null,
        token: null,
        id: null,
    },
    reducers: {
        setToken: (state, action) => {
            const token = action.payload;
            state.token = token;

            try {
                const decodedToken = jwtDecode(token);
                state.hubId = decodedToken?.hub_id || null; // Lấy hubId từ token
                state.role = decodedToken?.resource_access?.smart?.roles[0];
                state.email = decodedToken?.email;
                state.username = decodedToken?.preferred_username;
                console.log("Role",state.role);

            } catch (error) {
                console.error("Error decoding token:", error);
            }
        },
        clearAuth: (state) => {
            state.token = null;
            state.hubId = null;
            state.role = null;
        },
    },
});

export const { setToken, clearAuth } = authSlice.actions;
export default authSlice.reducer;
