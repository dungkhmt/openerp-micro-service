import { Avatar, Grid, List, ListItem, ListItemAvatar, ListItemText, Paper } from "@mui/material";
import randomColor from "randomcolor";
import React from "react";
import CircleIcon from "@mui/icons-material/Circle";
import { useSelector } from "react-redux";

const Participants = () => {
  const { participants } = useSelector((state) => state.codeEditor);
  return (
    <Paper elevation={3} sx={{ marginTop: "8px" }}>
      <Grid container spacing={1}>
        <Grid item>
          <CircleIcon color="success" fontSize="small" />
        </Grid>
        <Grid item>
          <strong>Người tham gia</strong>
        </Grid>
      </Grid>
      <List>
        {participants.map((value) => {
          return (
            <ListItem>
              <ListItemAvatar>
                <Avatar sx={{ bgcolor: randomColor() }}>{`${
                  value.fullName.toUpperCase()[0]
                }`}</Avatar>
              </ListItemAvatar>
              <ListItemText id={Math.random()} primary={`${value.fullName}`} />
            </ListItem>
          );
        })}
      </List>
    </Paper>
  );
};

export default Participants;
