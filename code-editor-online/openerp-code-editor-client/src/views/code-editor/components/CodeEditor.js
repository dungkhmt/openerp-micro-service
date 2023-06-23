/* eslint-disable react-hooks/exhaustive-deps */
import React, { useEffect } from "react";
import { request } from "api";
import { errorNoti } from "utils/notification";
import { useDispatch, useSelector } from "react-redux";
import { debounce } from "lodash";
import { PROGRAMMING_LANGUAGES, SOCKET_EVENTS } from "utils/constants";
import AceEditor from "react-ace";

import "ace-builds/src-noconflict/theme-monokai";
import "ace-builds/src-noconflict/theme-tomorrow";
import "ace-builds/src-noconflict/ext-language_tools";
import { setSource, setState } from "../reducers/codeEditorReducers";
import { useKeycloak } from "@react-keycloak/web";
import { ACCESS_PERMISSION } from "../utils/constants";
import { useRef } from "react";
import { AceMultiCursorManager } from "@convergencelabs/ace-collab-ext";
import "@convergencelabs/ace-collab-ext/dist/css/ace-collab-ext.css";
import { useState } from "react";
import randomColor from "randomcolor";

const CodeEditor = (props) => {
  const { socket, roomId, roomMasterId } = props;
  const dispatch = useDispatch();
  const { keycloak } = useKeycloak();
  const token = keycloak.tokenParsed;
  const {
    selectedLanguage,
    source,
    theme,
    fontSize,
    tabSpace,
    isAutoComplete,
    isEditCode,
    isPublic,
    roomAccessPermission,
    allowedUserList,
    participants,
  } = useSelector((state) => state.codeEditor);
  const editorRef = useRef(null);
  const cursorMgr = useRef(null);
  useEffect(() => {
    if (editorRef.current) {
      const editor = editorRef.current.editor;
      cursorMgr.current = new AceMultiCursorManager(editor.getSession());
    }
  }, []);
  useEffect(() => {
    if (cursorMgr.current) {
      participants?.map((participant) => {
        try {
          cursorMgr.current.addCursor(participant?.socketId, participant?.fullName, randomColor(), {
            row: 0,
            column: 0,
          });
        } catch (error) {
        }
      });
    }
  }, [participants]);
  const onChange = (value) => {
    if (editorRef.current) {
      const editor = editorRef.current.editor;
      const cursorPosition = editor.getCursorPosition();
      socket.current.emit(SOCKET_EVENTS.SEND_CODE_CHANGES, {
        language: selectedLanguage,
        source: value,
        cursor: {
          id: socket.current.id,
          label: token.name,
          position: cursorPosition,
        },
      });
      handleSaveSource(value);
    }
  };

  /**
   * check current user can edit the source code
   */
  useEffect(() => {
    if (roomMasterId === token.preferred_username) {
      dispatch(setState({ isEditCode: true }));
    } else if (isPublic && roomAccessPermission === ACCESS_PERMISSION.EDITOR.value) {
      dispatch(setState({ isEditCode: true }));
    } else {
      const user = allowedUserList.find((item) => item.id.userId === token.preferred_username);
      if (user && user.accessPermission === ACCESS_PERMISSION.EDITOR.value) {
        dispatch(setState({ isEditCode: true }));
      } else {
        dispatch(setState({ isEditCode: false }));
      }
    }
  }, [roomMasterId, token, isPublic, roomAccessPermission]);

  useEffect(() => {
    if (socket.current) {
      socket.current.on(SOCKET_EVENTS.REFRESH_ROOM_PERMISSION, ({ isPublic, accessPermission }) => {
        dispatch(
          setState({
            isPublic: isPublic,
            roomAccessPermission: accessPermission,
          })
        );
      });
    }
  }, [socket.current]);
  useEffect(() => {
    if (socket.current && cursorMgr.current) {
      socket.current.on(SOCKET_EVENTS.LEAVE_ROOM, ({ socketId }) => {
        cursorMgr.current.clearCursor(socketId);
        cursorMgr.current.removeCursor(socketId);
      });
    }
  }, [socket.current, cursorMgr.current]);

  useEffect(() => {
    if (socket.current) {
      socket.current.on(SOCKET_EVENTS.REFRESH_USER_PERMISSION, ({ userId, accessPermission }) => {
        if (userId === token.preferred_username) {
          if (isPublic) {
            if (roomAccessPermission === ACCESS_PERMISSION.VIEWER.value) {
              if (accessPermission === ACCESS_PERMISSION.VIEWER.value) {
                dispatch(setState({ isEditCode: false }));
              } else if (accessPermission === ACCESS_PERMISSION.EDITOR.value) {
                dispatch(setState({ isEditCode: true }));
              }
            }
          } else {
            if (accessPermission === ACCESS_PERMISSION.VIEWER.value) {
              dispatch(setState({ isEditCode: false }));
            } else if (accessPermission === ACCESS_PERMISSION.EDITOR.value) {
              dispatch(setState({ isEditCode: true }));
            }
          }
        }
      });
    }
  }, [socket.current, token, isPublic, roomAccessPermission]);

  const handleSaveSource = debounce((value) => {
    dispatch(setSource(value));
    const payload = {
      language: selectedLanguage,
      source: value,
      roomId: roomId,
    };
    request(
      "post",
      `/code-editor/sources`,
      (response) => {
        if (response && response.status === 200) {
          // dispatch(setSource(value));
        }
      },
      {
        404: (e) => {
          errorNoti("Không tìm thấy room", true);
        },
      },
      payload
    );
  }, 500);

  useEffect(() => {
    if (socket.current) {
      socket.current.on(SOCKET_EVENTS.RECEIVE_CODE_CHANGES, ({ language, source, cursor }) => {
        if (language === selectedLanguage) {
          dispatch(setSource(source));
          cursorMgr.current.setCursor(cursor?.id, cursor?.position);
        }
      });
    }

    return () => {
      socket.current.off(SOCKET_EVENTS.RECEIVE_CODE_CHANGES);
    };
  }, [socket.current, selectedLanguage]);

  const loadSource = (roomId, language) => {
    request(
      "get",
      `/code-editor/sources/load-source?roomId=${roomId}&language=${language}`,
      (response) => {
        if (response && response.status === 200) {
          dispatch(setSource(response.data.source));
        }
      },
      {
        404: (e) => {
          errorNoti("Không tìm thấy source code", true);
        },
      }
    );
  };
  useEffect(() => {
    loadSource(roomId, selectedLanguage);
  }, [roomId, selectedLanguage]);

  function getModeLanguage(language) {
    if (language === PROGRAMMING_LANGUAGES.CPP.value) {
      require("ace-builds/src-noconflict/mode-c_cpp");
      return "c_cpp";
    }
    if (language === PROGRAMMING_LANGUAGES.JAVA.value) {
      require("ace-builds/src-noconflict/mode-java");
      return "java";
    }
    if (language === PROGRAMMING_LANGUAGES.PYTHON.value) {
      require("ace-builds/src-noconflict/mode-python");
      return "python";
    }
  }

  function getTheme(theme) {
    if (theme === "dark") {
      return "monokai";
    }

    return "tomorrow";
  }
  return (
    <AceEditor
      ref={editorRef}
      mode={`${getModeLanguage(selectedLanguage)}`}
      theme={getTheme(theme)}
      onChange={onChange}
      width="100%"
      height="100%"
      fontSize={fontSize}
      showPrintMargin={false}
      showGutter={true}
      highlightActiveLine={true}
      value={source}
      readOnly={!isEditCode}
      setOptions={{
        enableBasicAutocompletion: isAutoComplete,
        enableLiveAutocompletion: isAutoComplete,
        enableSnippets: false,
        showLineNumbers: true,
        tabSize: tabSpace,
      }}
    />
  );
};

export default CodeEditor;
