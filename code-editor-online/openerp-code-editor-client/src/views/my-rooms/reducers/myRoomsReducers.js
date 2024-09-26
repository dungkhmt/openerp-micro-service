import { createSlice } from "@reduxjs/toolkit";

const initialState = {
  selectedRoom: null,
  isVisibleRoomForm: false,
  reloadData: 0,
};

export const myRoomsSlice = createSlice({
  name: "myRooms",
  initialState,
  reducers: {
    setSelectedRoom: (state, action) => {
      state.selectedRoom = action.payload;
    },
    setIsVisibleRoomForm: (state, action) => {
      state.isVisibleRoomForm = action.payload;
    },
    setReloadData: (state, action) => {
      state.reloadData = action.payload;
    },
  },
});

export const { setSelectedRoom, setIsVisibleRoomForm, setReloadData } = myRoomsSlice.actions;

export default myRoomsSlice.reducer;
