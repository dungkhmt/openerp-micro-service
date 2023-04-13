import { createSlice } from "@reduxjs/toolkit";
import { PROGRAMMING_LANGUAGES } from "utils/constants";

const initialState = {
  selectedLanguage: PROGRAMMING_LANGUAGES.CPP.value,
  isVisibleParticipants: false,

  numberOfParticipants: 0,
  participants: [],
};

export const codeEditorSlice = createSlice({
  name: "codeEditor",
  initialState,
  reducers: {
    setSelectedLanguage: (state, action) => {
      state.selectedLanguage = action.payload;
    },
    setIsVisibleParticipants: (state, action) => {
      state.isVisibleParticipants = action.payload;
    },
    setNumberOfParticipants: (state, action) => {
      state.numberOfParticipants = action.payload;
    },
    setParticipants: (state, action) => {
      state.participants = action.payload;
    },
  },
});

export const {
  setSelectedLanguage,
  setIsVisibleParticipants,
  setNumberOfParticipants,
  setParticipants,
} = codeEditorSlice.actions;

export default codeEditorSlice.reducer;
