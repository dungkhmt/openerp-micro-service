// authSlice.js - Enhanced version
import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import { jwtDecode } from "jwt-decode";
import { request } from "api"; // Using your existing request utility

// Create an async thunk to fetch hub ID based on username
export const fetchHubId = createAsyncThunk(
    "auth/fetchHubId",
    async (_, { getState }) => {
        const { auth } = getState();
        const username = auth.user.username;

        if (!username) {
            throw new Error("Username not available");
        }

        // Call your backend endpoint
        const response = await request("get", `/smdeli/hub/username/${username}`);
        return response.data;  // Assuming this returns an object with hubId
    }
);

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
        },
        loading: false,
        error: null
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
                    hubId: null // Will be set later by fetchHubId
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
    extraReducers: (builder) => {
        builder
            .addCase(fetchHubId.pending, (state) => {
                state.loading = true;
                state.error = null;
            })
            .addCase(fetchHubId.fulfilled, (state, action) => {
                state.loading = false;
                state.user.hubId = action.payload.hubId;
            })
            .addCase(fetchHubId.rejected, (state, action) => {
                state.loading = false;
                state.error = action.error.message;
                console.error("Error fetching hub ID:", action.error);
            });
    }
});

export const { setToken, clearAuth } = authSlice.actions;
export default authSlice.reducer;