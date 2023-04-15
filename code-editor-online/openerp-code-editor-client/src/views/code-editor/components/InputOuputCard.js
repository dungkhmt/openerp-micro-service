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
import { successNoti } from "utils/notification";

const InputOutputCard = () => {
  const [tabKey, setTabKey] = useState("input");
  const handleChangeTab = (event, value) => {
    setTabKey(value);
  };
  const [valueInput, setValueInput] = useState();
  const [valueOutput, setValueOutput] = useState();
  const handleCopy = (tab) => {
    if (tab === "input") {
      copy(valueInput || "");
    } else if (tab === "output") {
      copy(valueOutput || "");
    }
    successNoti("Copied to clipboard", true);
  };

  const handleClear = (tab) => {
    if (tab === "input") {
      setValueInput();
    } else if (tab === "output") {
      setValueOutput();
    }
  };
  return (
    <Card>
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
      <CardContent>
        {tabKey === "input" && (
          <OutlinedInput
            value={valueInput}
            multiline
            fullWidth
            minRows={5}
            onChange={(e) => {
              setValueInput(e.target.value);
            }}
          />
        )}
        {tabKey === "output" && (
          <OutlinedInput
            value={valueOutput}
            multiline
            fullWidth
            minRows={5}
            onChange={(e) => {
              setValueOutput(e.target.value);
            }}
          />
        )}
      </CardContent>
    </Card>
  );
};

export default InputOutputCard;
