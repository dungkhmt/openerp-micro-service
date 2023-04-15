/* eslint-disable no-unused-vars */
import {
  Avatar,
  Grid,
  IconButton,
  List,
  ListItem,
  ListItemAvatar,
  ListItemText,
  Paper,
  Tooltip,
} from "@mui/material";
import React from "react";
import CircleIcon from "@mui/icons-material/Circle";
import { useSelector } from "react-redux";
import { CHARACTER_COLOR } from "utils/constants";
import { Mic } from "@mui/icons-material";

const Participants = () => {
  const { participants } = useSelector((state) => state.codeEditor);
  return (
    <Paper elevation={3} sx={{ marginTop: "8px" }}>
      <Grid container spacing={1} justifyContent="center" alignItems="center" flexWrap="nowrap">
        <CircleIcon color="success" fontSize="10px" />
        <strong
          style={{
            fontSize: "1rem",
            overflow: "hidden",
            textOverflow: "ellipsis",
            whiteSpace: "nowrap",
          }}
        >
          Người tham gia
        </strong>
      </Grid>
      <List>
        {participants.map((value) => {
          return (
            <ListItem
              key={Math.random()}
              secondaryAction={
                <IconButton edge="end">
                  <Mic fontSize="small" />
                </IconButton>
              }
            >
              <ListItemAvatar>
                <Avatar sx={{ bgcolor: CHARACTER_COLOR[value.fullName.toUpperCase()[0]] }}>{`${
                  value.fullName.toUpperCase()[0]
                }`}</Avatar>
              </ListItemAvatar>
              <div style={{ overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>
                {value.fullName}
              </div>
            </ListItem>
          );
        })}
      </List>
    </Paper>
  );
};

export default Participants;
