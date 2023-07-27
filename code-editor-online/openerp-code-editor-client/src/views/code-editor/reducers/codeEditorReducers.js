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

  isVisibleInput: false,

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
  isShowCamera: true,

  remoteUsers: [],
  localVideo: null,
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
    handleTurnOffCamera: (state, action) => {
      state.localVideo?.getTracks().forEach((track) => track.stop());
    },
    handleOnOffMic: (state, action) => {
      const { isMute } = action.payload;
      if (state.localVideo) {
        state.localVideo.getAudioTracks()[0].enabled = isMute;
      }
    },
    handleOnOffCamera: (state, action) => {
      const { isShowCamera } = action.payload;
      if (state.localVideo) {
        state.localVideo.getVideoTracks().forEach((track) => {
          track.enabled = isShowCamera;
        });
      }
    },
    handleAddRemoteUser: (state, action) => {
      const { media, peerId } = action.payload;
      const remoteUser = state.remoteUsers.find((item) => item.peerId === peerId);
      const participant = state.participants.find((item) => item.peerId === peerId);
      if (remoteUser) {
        remoteUser.media = media;
        remoteUser.socketId = participant?.socketId;
        remoteUser.fullName = participant?.fullName;
        remoteUser.audio = participant?.audio;
        remoteUser.video = participant?.video;
      } else {
        state.remoteUsers.push({
          socketId: participant?.socketId,
          audio: participant?.audio,
          video: participant?.video,
          media,
          peerId,
          fullName: participant?.fullName,
        });
      }
    },
    handleRemoveRemoteUser: (state, action) => {
      const { socketId } = action.payload;
      const index = state.remoteUsers.findIndex((item) => item.socketId === socketId);
      if (index !== -1) {
        state.remoteUsers.splice(index, 1);
      }
    },
    handleOnOffMicRemoteUser: (state, action) => {
      const { socketId, audio } = action.payload;
      const remoteUser = state.remoteUsers.find((item) => item.socketId === socketId);
      if (remoteUser) {
        remoteUser.audio = audio;
      }
    },
    handleOnOffCameraRemoteUser: (state, action) => {
      const { socketId, video } = action.payload;
      const remoteUser = state.remoteUsers.find((item) => item.socketId === socketId);
      if (remoteUser) {
        remoteUser.video = video;
      }
    },
    handleOnOffMicParticipant: (state, action) => {
      const { socketId, audio } = action.payload;
      const participant = state.participants.find((item) => item.socketId === socketId);
      if (participant) {
        participant.audio = audio;
      }
    },
    handleOnOffCameraParticipant: (state, action) => {
      const { socketId, video } = action.payload;
      const participant = state.participants.find((item) => item.socketId === socketId);
      if (participant) {
        participant.video = video;
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
  handleTurnOffCamera,
  handleOnOffMic,
  handleOnOffCamera,
  handleAddRemoteUser,
  handleRemoveRemoteUser,
  handleOnOffMicRemoteUser,
  handleOnOffCameraRemoteUser,
  handleOnOffMicParticipant,
  handleOnOffCameraParticipant,
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
