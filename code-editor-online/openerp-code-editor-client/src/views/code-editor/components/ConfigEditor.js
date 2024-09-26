import {
  Grid,
  MenuItem,
  Paper,
  Popover,
  Select,
  Switch,
} from "@mui/material";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  setFontSize,
  setIsAutoComplete,
  setTabSpace,
  setTheme,
} from "../reducers/codeEditorReducers";

const ConfigEditor = (props) => {
  const { anchorElement, handleClose } = props;
  const dispatch = useDispatch();
  const { isVisibleConfigEditor, theme, fontSize, tabSpace, isAutoComplete } = useSelector(
    (state) => state.codeEditor
  );
  const fontSizeList = [9, 10, 12, 14, 16, 18, 20, 24, 28, 32, 40];
  const tabSpaceList = [1, 2, 3, 4, 5, 6, 7, 8];
  return (
    <Popover
      open={isVisibleConfigEditor}
      anchorEl={anchorElement}
      onClose={handleClose}
      anchorOrigin={{
        vertical: "bottom",
        horizontal: "left",
      }}
    >
      <Paper sx={{ width: "250px", padding: "10px" }}>
        <Grid container spacing={2} alignItems="center" marginBottom={2}>
          <Grid item xs={6}>
            <strong>Theme</strong>
          </Grid>
          <Grid item xs={6}>
            <Select
              fullWidth
              size="small"
              value={theme}
              defaultValue="dark"
              onChange={(e) => {
                dispatch(setTheme(e.target.value));
              }}
            >
              <MenuItem value="dark">Dark</MenuItem>
              <MenuItem value="light">Light</MenuItem>
            </Select>
          </Grid>
        </Grid>
        <Grid container spacing={2} alignItems="center" marginBottom={2}>
          <Grid item xs={6}>
            <strong>Font Size</strong>
          </Grid>
          <Grid item xs={6}>
            <Select
              fullWidth
              size="small"
              value={fontSize}
              onChange={(e) => {
                dispatch(setFontSize(e.target.value));
              }}
            >
              {fontSizeList.map((e) => {
                return (
                  <MenuItem key={`font_size_${e}`} value={e}>
                    {e}
                  </MenuItem>
                );
              })}
            </Select>
          </Grid>
        </Grid>
        <Grid container spacing={2} alignItems="center" marginBottom={2}>
          <Grid item xs={6}>
            <strong>Tab Space</strong>
          </Grid>
          <Grid item xs={6}>
            <Select
              fullWidth
              size="small"
              value={tabSpace}
              onChange={(e) => {
                dispatch(setTabSpace(e.target.value));
              }}
            >
              {tabSpaceList.map((e) => {
                return (
                  <MenuItem key={`tab_space_${e}`} value={e}>
                    {e}
                  </MenuItem>
                );
              })}
            </Select>
          </Grid>
        </Grid>
        <Grid container spacing={2} alignItems="center">
          <Grid item xs={8}>
            <strong>Auto Complete</strong>
          </Grid>
          <Grid item xs={4}>
            <Switch
              checked={isAutoComplete}
              onChange={(e) => {
                dispatch(setIsAutoComplete(e.target.checked));
              }}
            />
          </Grid>
        </Grid>
      </Paper>
    </Popover>
  );
};

export default ConfigEditor;
