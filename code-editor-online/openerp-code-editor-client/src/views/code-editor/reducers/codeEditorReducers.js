import { createSlice } from "@reduxjs/toolkit";
import { PROGRAMMING_LANGUAGES } from "utils/constants";
import { ACCESS_PERMISSION } from "../utils/constants";

const initialState = {
  // room
  roomName: null,
  isPublic: false,
  roomAccessPermission: ACCESS_PERMISSION.VIEWER.value,
  roomMaster: null,
  allowedUserList: [],
  reloadAllowedUser: 0,

  // code editor
  selectedLanguage: PROGRAMMING_LANGUAGES.CPP.value,
  source: null,
  input: "",
  output: "",

  isVisibleShareForm: false,
  tabKey: "input",

  isEditCode: false,

  // participants
  isVisibleParticipants: false,
  participants: [],

  // setting editor
  isVisibleConfigEditor: false,
  theme: "dark",
  fontSize: 14,
  tabSpace: 4,
  isAutoComplete: true,

  // mic
  isMute: false,
};

export const codeEditorSlice = createSlice({
  name: "codeEditor",
  initialState,
  reducers: {
    setState: (state, action) => {
      return {
        ...state,
        ...action.payload,
      };
    },
    handleOnOffMicParticipant: (state, action) => {
      const { socketId, audio } = action.payload;
      const participant = state.participants.find((item) => item.socketId === socketId);
      if (participant) {
        participant.audio = audio;
      }
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
    setIsVisibleConfigEditor: (state, action) => {
      state.isVisibleConfigEditor = action.payload;
    },
    setTheme: (state, action) => {
      state.theme = action.payload;
    },
    setFontSize: (state, action) => {
      state.fontSize = action.payload;
    },
    setTabSpace: (state, action) => {
      state.tabSpace = action.payload;
    },
    setIsAutoComplete: (state, action) => {
      state.isAutoComplete = action.payload;
    },
  },
});

export const {
  setState,
  handleOnOffMicParticipant,
  setIsVisibleParticipants,
  setParticipants,
  setSource,
  setIsVisibleShareForm,
  setInput,
  setOutput,
  setTabKey,
  setIsVisibleConfigEditor,
  setTheme,
  setFontSize,
  setTabSpace,
  setIsAutoComplete,
} = codeEditorSlice.actions;

export const codeEditorSelector = ({ codeEditor }) => codeEditor;

export default codeEditorSlice.reducer;
