/* eslint-disable no-unused-vars */
import {
  ContentCopy,
  Download,
  ExitToApp,
  Groups,
  PlayArrow,
  Settings,
  Share,
} from "@mui/icons-material";
import {
  Badge,
  Button,
  FormControl,
  Grid,
  IconButton,
  InputLabel,
  MenuItem,
  Select,
  Tooltip,
} from "@mui/material";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { useHistory } from "react-router";
import { PROGRAMMING_LANGUAGES } from "utils/constants";
import {
  setIsVisibleParticipants,
  setIsVisibleShareForm,
  setSelectedLanguage,
} from "../reducers/codeEditorReducers";
import { getLanguageFileType } from "utils/CodeEditorUtils";
import ShareForm from "./ShareForm";

const NavBarRoom = (props) => {
  const { socket } = props;
  const dispatch = useDispatch();
  const history = useHistory();
  const { isVisibleParticipants, selectedLanguage, participants, source } = useSelector(
    (state) => state.codeEditor
  );
  const handleDisplayParticipants = () => {
    dispatch(setIsVisibleParticipants(!isVisibleParticipants));
  };

  const handleLeaveRoom = () => {
    history.push("/code-editor/create-join-room");
    socket.current.disconnect();
  };
  function handleDownloadSource(language) {
    const blob = new Blob([source], { type: "text/plain" });
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.download = `code.${getLanguageFileType(language)}`;
    link.href = url;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }
  return (
    <div>
      <ShareForm />
      <Grid container justifyContent="space-between" alignItems="center">
        <Grid item>
          <Grid container spacing={2}>
            <Grid item>
              <Button size="small" startIcon={<PlayArrow />} variant="contained" color="success">
                Run
              </Button>
            </Grid>
            <Grid item>
              <Button
                size="small"
                startIcon={<Download />}
                variant="contained"
                onClick={() => {
                  handleDownloadSource(selectedLanguage);
                }}
              >
                Download
              </Button>
            </Grid>
            <Grid item>
              <Button
                size="small"
                startIcon={<Share />}
                variant="contained"
                onClick={() => {
                  dispatch(setIsVisibleShareForm(true));
                }}
              >
                Share
              </Button>
            </Grid>
          </Grid>
        </Grid>
        <Grid item>
          <FormControl fullWidth size="small" sx={{ minWidth: 100 }}>
            <InputLabel id="demo-simple-select-label">Language</InputLabel>
            <Select
              labelId="demo-simple-select-label"
              id="demo-simple-select"
              value={selectedLanguage}
              label="Language"
              autoWidth
              onChange={(e) => {
                console.log(e);
                dispatch(setSelectedLanguage(e.target.value));
              }}
            >
              <MenuItem value={PROGRAMMING_LANGUAGES.CPP.value}>
                {PROGRAMMING_LANGUAGES.CPP.label}
              </MenuItem>
              <MenuItem value={PROGRAMMING_LANGUAGES.JAVA.value}>
                {PROGRAMMING_LANGUAGES.JAVA.label}
              </MenuItem>
              <MenuItem value={PROGRAMMING_LANGUAGES.PYTHON.value}>
                {PROGRAMMING_LANGUAGES.PYTHON.label}
              </MenuItem>
            </Select>
          </FormControl>
        </Grid>
        <Grid item>
          <Grid container spacing={2} alignItems="center">
            <Grid item>
              <Tooltip title="Người tham gia">
                <IconButton
                  onClick={() => {
                    handleDisplayParticipants();
                  }}
                >
                  <Badge badgeContent={participants.length} color="primary">
                    <Groups fontSize="large" />
                  </Badge>
                </IconButton>
              </Tooltip>
            </Grid>
            <Grid item>
              <Tooltip title="Cài đặt">
                <IconButton>
                  <Settings fontSize="large" />
                </IconButton>
              </Tooltip>
            </Grid>

            <Grid item>
              <Button
                size="small"
                startIcon={<ExitToApp />}
                variant="contained"
                color="error"
                onClick={() => {
                  handleLeaveRoom();
                }}
              >
                Leave room
              </Button>
            </Grid>
          </Grid>
        </Grid>
      </Grid>
    </div>
  );
};

export default NavBarRoom;
