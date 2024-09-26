import { configureStore } from "@reduxjs/toolkit";
import codeEditorReducers from "views/code-editor/reducers/codeEditorReducers";
import myRoomsReducers from "views/my-rooms/reducers/myRoomsReducers";

export const store = configureStore({
  reducer: {
    myRooms: myRoomsReducers,
    codeEditor: codeEditorReducers
  },
});
