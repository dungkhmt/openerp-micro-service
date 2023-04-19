import { createSlice } from "@reduxjs/toolkit";
import { PROGRAMMING_LANGUAGES } from "utils/constants";

const initialState = {
  selectedLanguage: PROGRAMMING_LANGUAGES.CPP.value,
  source: null,
  input: "",
  output: "",
  isVisibleParticipants: false,
  isVisibleShareForm: false,

  tabKey: "input",

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
    setInput: (state, action) => {
      state.input = action.payload;
    },
    setOutput: (state, action) => {
      state.output = action.payload;
    },
    setTabKey: (state, action) => {
      state.tabKey = action.payload;
    },
  },
});

export const {
  setSelectedLanguage,
  setIsVisibleParticipants,
  setParticipants,
  setSource,
  setIsVisibleShareForm,
  setInput,
  setOutput,
  setTabKey,
} = codeEditorSlice.actions;

export default codeEditorSlice.reducer;
