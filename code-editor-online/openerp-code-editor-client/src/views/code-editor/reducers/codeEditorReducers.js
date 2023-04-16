import { createSlice } from "@reduxjs/toolkit";
import { PROGRAMMING_LANGUAGES } from "utils/constants";

const initialState = {
  selectedLanguage: PROGRAMMING_LANGUAGES.CPP.value,
  source: null,
  isVisibleParticipants: false,
  isVisibleShareForm: false,

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

    setParticipants: (state, action) => {
      state.participants = action.payload;
    },
    setSource: (state, action) => {
      state.source = action.payload;
    },
    setIsVisibleShareForm: (state, action) => {
      state.isVisibleShareForm = action.payload;
    },
  },
});

export const {
  setSelectedLanguage,
  setIsVisibleParticipants,
  setParticipants,
  setSource,
  setIsVisibleShareForm,
} = codeEditorSlice.actions;

export default codeEditorSlice.reducer;
