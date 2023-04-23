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
import { setSource } from "../reducers/codeEditorReducers";

const CodeEditor = (props) => {
  const { socket, roomId } = props;
  const dispatch = useDispatch();
  const { selectedLanguage, source, theme, fontSize, tabSpace, isAutoComplete } = useSelector((state) => state.codeEditor);
  const onChange = (value) => {
    socket.current.emit(SOCKET_EVENTS.SEND_CODE_CHANGES, {
      language: selectedLanguage,
      source: value,
    });
    handleSaveSource(value);
  };

  const handleSaveSource = debounce((value) => {
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
          dispatch(setSource(value))
        }
      },
      {
        404: (e) => {
          errorNoti("Không tìm thấy room", true);
        },
      },
      payload
    );
  }, 1000);

  useEffect(() => {
    if (socket.current) {
      socket.current.on(SOCKET_EVENTS.RECEIVE_CODE_CHANGES, ({ language, source }) => {
        if (language === selectedLanguage) {
          dispatch(setSource(source));
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
          dispatch(setSource(response.data.source))
          
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

  function getTheme(theme){
    if(theme === "dark"){
      return "monokai"
    }

    return "tomorrow"
  }
  return (
    <AceEditor
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
