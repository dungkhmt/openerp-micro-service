import { createSlice } from "@reduxjs/toolkit";
import {jwtDecode} from "jwt-decode";

const authSlice = createSlice({
    name: "auth",
    initialState: {
        token: null,
        isAuthenticated: false,
        user: {
            username: null,
            email: null,
            role: null,
            hubId: null
        }
    },
    reducers: {
        setToken: (state, action) => {
            const token = action.payload;
            state.token = token;
            state.isAuthenticated = true;

            try {
                const decodedToken = jwtDecode(token);
                state.user = {
                    username: decodedToken?.preferred_username,
                    email: decodedToken?.email,
                    role: decodedToken?.resource_access?.smart_delivery?.roles[0],
                    hubId: decodedToken?.hub_id || null
                };
                console.log("User role:", state.user.role);
            } catch (error) {
                console.error("Error decoding token:", error);
            }
        },
        clearAuth: (state) => {
            state.token = null;
            state.isAuthenticated = false;
            state.user = {
                username: null,
                email: null,
                role: null,
                hubId: null
            };
        },
    },
});

export const { setToken, clearAuth } = authSlice.actions;
export default authSlice.reducer;
