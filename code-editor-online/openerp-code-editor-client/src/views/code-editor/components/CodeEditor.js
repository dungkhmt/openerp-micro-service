/* eslint-disable react-hooks/exhaustive-deps */
import { javascript } from "@codemirror/lang-javascript";
import ReactCodeMirror from "@uiw/react-codemirror";
import React, { useEffect, useState } from "react";
import { request } from "api";
import { errorNoti } from "utils/notification";
import { useSelector } from "react-redux";
import { debounce } from "lodash";
import { SOCKET_EVENTS } from "utils/constants";

const CodeEditor = (props) => {
  const { socket, roomId } = props;
  const [source, setSource] = useState();
  const { selectedLanguage } = useSelector((state) => state.codeEditor);
  const onChange = (value) => {
    socket.current.emit(SOCKET_EVENTS.SEND_CODE_CHANGES, value);
    handleSaveSource(value);
    // setSource(value);
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
          console.log(response.data);
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
      socket.current.on(SOCKET_EVENTS.RECEIVE_CODE_CHANGES, (code) => {
        setSource(code);
      });
    }

    return () => {
      socket.current.off(SOCKET_EVENTS.RECEIVE_CODE_CHANGES);
    };
  }, [socket.current]);

  const loadSource = (roomId, language) => {
    request(
      "get",
      `/code-editor/sources/load-source?roomId=${roomId}&language=${language}`,
      (response) => {
        if (response && response.status === 200) {
          console.log(response.data);
          setSource(response.data.source);
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
  return (
    <ReactCodeMirror
      value={source}
      height="50vh"
      style={{ border: "1px solid black" }}
      extensions={[javascript({ jsx: true })]}
      theme="dark"
      onChange={(value) => {
        onChange(value);
      }}
    />
  );
};

export default CodeEditor;
