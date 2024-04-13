import {Box, Typography} from "@material-ui/core";
import {grey} from "@material-ui/core/colors";
import {makeStyles} from "@material-ui/core/styles";
import React from "react";
import ActionsWithNotificationButton from "./ActionsWithNotificationButton";

const useStyles = makeStyles((theme) => ({
  notification: {
    fontWeight: theme.typography.fontWeightMedium,
  },
  iconButton: {
    padding: 0,
    "&:hover": {
      backgroundColor: grey[200],
    },
  },
}));

export default function NotificationTitle() {
  const classes = useStyles();

  return (
    <div style={{ margin: "20px 16px 12px", position: "relative" }}>
      <Box
        position="relative"
        display="flex"
        flexShrink={0}
        flexWrap="no-wrap"
        flexDirection="row"
        justifyContent="space-between"
        alignItems="center"
      >
        <Box
          position="relative"
          minWidth={0}
          maxWidth="100%"
          flexShrink={1}
          flexGrow={1}
          flexBasis="auto"
        >
          <Typography
            component="h1"
            variant="h5"
            className={classes.notification}
            style={{ marginTop: "-7px", marginBottom: "-7px" }}
          >
            Thông báo
          </Typography>
        </Box>
        <ActionsWithNotificationButton />
      </Box>
    </div>
  );
}
