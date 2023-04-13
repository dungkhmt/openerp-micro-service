import { ContentCopy, Download, ExitToApp, Groups, PlayArrow, Share } from "@mui/icons-material";
import { Badge, Button, FormControl, Grid, InputLabel, MenuItem, Select } from "@mui/material";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { useHistory } from "react-router";
import { PROGRAMMING_LANGUAGES, SOCKET_EVENTS } from "utils/constants";
import { setIsVisibleParticipants, setSelectedLanguage } from "../reducers/codeEditorReducers";

const NavBarRoom = (props) => {
  const { socket } = props;
  const dispatch = useDispatch();
  const history = useHistory();
  const { isVisibleParticipants, selectedLanguage, numberOfParticipants } = useSelector(
    (state) => state.codeEditor
  );
  const handleDisplayParticipants = () => {
    dispatch(setIsVisibleParticipants(!isVisibleParticipants));
  };

  const handleLeaveRoom = () => {
    history.push("/code-editor/create-join-room");
    socket.current.disconnect();

    
  };
  return (
    <div>
      <Grid container justifyContent="space-between">
        <Grid item>
          <Grid container spacing={2}>
            <Grid item>
              <Button size="small" startIcon={<PlayArrow />} variant="contained" color="success">
                Run
              </Button>
            </Grid>
            <Grid item>
              <Button size="small" startIcon={<ContentCopy />} variant="contained">
                Copy
              </Button>
            </Grid>
            <Grid item>
              <Button size="small" startIcon={<Download />} variant="contained">
                Download
              </Button>
            </Grid>
          </Grid>
        </Grid>
        <Grid item xs={1}>
          <FormControl fullWidth size="small">
            <InputLabel id="demo-simple-select-label">Language</InputLabel>
            <Select
              labelId="demo-simple-select-label"
              id="demo-simple-select"
              value={selectedLanguage}
              label="Language"
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
          <Grid container spacing={2}>
            <Grid item>
              <Badge badgeContent={numberOfParticipants} color="primary">
                <Groups
                  sx={{ cursor: "pointer" }}
                  fontSize="large"
                  onClick={() => {
                    handleDisplayParticipants();
                  }}
                />
              </Badge>
            </Grid>
            <Grid item>
              <Button size="small" startIcon={<Share />} variant="contained">
                Share
              </Button>
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
