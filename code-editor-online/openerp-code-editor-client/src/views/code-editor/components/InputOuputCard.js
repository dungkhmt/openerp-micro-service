/* eslint-disable react-hooks/exhaustive-deps */
import { ContentCopy, DeleteOutline } from "@mui/icons-material";
import {
  Card,
  CardContent,
  CardHeader,
  IconButton,
  OutlinedInput,
  Tab,
  Tabs,
  Tooltip,
} from "@mui/material";
import copy from "copy-to-clipboard";
import React from "react";
import { useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { successNoti } from "utils/notification";
import { setInput, setOutput, setTabKey } from "../reducers/codeEditorReducers";
import { useLocation } from "react-router-dom";
import { useEffect } from "react";

const InputOutputCard = () => {
  const { pathname } = useLocation();
  const dispatch = useDispatch();
  const { input, output, tabKey } = useSelector((state) => state.codeEditor);
  const handleChangeTab = (event, value) => {
    dispatch(setTabKey(value));
  };
  const handleCopy = (tab) => {
    if (tab === "input") {
      copy(input);
    } else if (tab === "output") {
      copy(output);
    }
    successNoti("Copied to clipboard", true);
  };

  const handleClear = (tab) => {
    if (tab === "input") {
      dispatch(setInput(""));
    } else if (tab === "output") {
      dispatch(setOutput(""));
    }
  };
  useEffect(() => {
    dispatch(setInput(""));
    dispatch(setOutput(""));
  }, [pathname]);
  return (
    <Card sx={{ height: "100%" }}>
      <CardHeader
        title={
          <Tabs
            value={tabKey}
            onChange={handleChangeTab}
            textColor="primary"
            indicatorColor="primary"
          >
            <Tab label="Input" value="input" />
            <Tab label="Output" value="output" />
          </Tabs>
        }
        action={
          <>
            <Tooltip title={`Copy ${tabKey}`}>
              <IconButton
                onClick={() => {
                  handleCopy(tabKey);
                }}
              >
                <ContentCopy />
              </IconButton>
            </Tooltip>

            <IconButton
              title={`Clear ${tabKey}`}
              onClick={() => {
                handleClear(tabKey);
              }}
            >
              <DeleteOutline />
            </IconButton>
          </>
        }
      />
      <CardContent sx={{ height: "100%" }}>
        {tabKey === "input" && (
          <OutlinedInput
            value={input}
            multiline
            fullWidth
            sx={{ height: "100%", alignItems: "start" }}
            minRows={5}
            onChange={(e) => {
              dispatch(setInput(e.target.value));
            }}
          />
        )}
        {tabKey === "output" && (
          <OutlinedInput
            value={output}
            readOnly
            multiline
            fullWidth
            sx={{ height: "100%", alignItems: "start" }}
            minRows={5}
            onChange={(e) => {
              dispatch(setOutput(e.target.value));
            }}
          />
        )}
      </CardContent>
    </Card>
  );
};

export default InputOutputCard;
