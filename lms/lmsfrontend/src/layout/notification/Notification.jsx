import {useState} from "@hookstate/core";
import {Avatar, Box, ListItemAvatar, Typography} from "@material-ui/core";
import {grey} from "@material-ui/core/colors";
import {makeStyles} from "@material-ui/core/styles";
import {Skeleton} from "@material-ui/lab";
import React from "react";
import {request} from "../../api";
import {useNotificationState} from "../../state/NotificationState";
import ListItemLink from "../sidebar/v1/ListItemLink";
import NotificationReadIcon from "./NotificationReadIcon";

const useStyles = makeStyles((theme) => ({
  itemLink: {
    padding: "0px 8px",
    color: theme.palette.text.primary,
  },
  contentContainer: {
    padding: "0px 8px",
    borderRadius: 8,
    "&:hover": {
      backgroundColor: grey[200],
    },
  },
  itemAvatar: {
    marginRight: 12,
    marginBottom: 8,
    marginTop: 8,
  },
  time: {
    fontSize: "0.8125rem",
    marginTop: 5,
    fontWeight: (read) =>
      read
        ? theme.typography.fontWeightRegular
        : theme.typography.fontWeightMedium,
  },
  content: {
    marginBottom: 5,
    fontSize: ".9375rem",
    color: (read) => (read ? "inherit" : "#050505"),
    fontWeight: (read) =>
      read
        ? theme.typography.fontWeightRegular
        : theme.typography.fontWeightMedium,

    // limited lines text
    overflow: "hidden",
    textOverflow: "ellipsis",
    display: "-webkit-box",
    "-webkit-line-clamp": 3 /* number of lines to show */,
    "-webkit-box-orient": "vertical",
  },
  avatar: {
    width: 56,
    height: 56,
  },
}));

const ONE_MINUTE = 60000;
const ONE_HOUR = 60 * ONE_MINUTE;
const ONE_DAY = 24 * ONE_HOUR;
const ONE_WEEK = 7 * ONE_DAY;

const formatTime = (createdTime) => {
  const now = new Date().getTime();
  const time = new Date(createdTime).getTime();

  const duration = now - time;
  let convertDuration = (duration / ONE_WEEK) | 0;

  if (convertDuration > 0) {
    return `${convertDuration} tuần trước`;
  } else {
    convertDuration = (duration / ONE_DAY) | 0;

    if (convertDuration > 0) {
      return `${convertDuration} ngày trước`;
    } else {
      convertDuration = (duration / ONE_HOUR) | 0;

      if (convertDuration > 0) {
        return `${convertDuration} giờ trước`;
      } else {
        convertDuration = (duration / ONE_MINUTE) | 0;
        convertDuration = convertDuration > 1 ? convertDuration : 1;

        return `${convertDuration} phút trước`;
      }
    }
  }
};

export function getRandomIntInclusive(min, max) {
  min = Math.ceil(min);
  max = Math.floor(max);
  return Math.floor(Math.random() * (max - min + 1) + min); //The maximum is inclusive and the minimum is inclusive
}

const STATUS_NOTIFICATION_CREATED = "NOTIFICATION_CREATED";
export const STATUS_NOTIFICATION_READ = "NOTIFICATION_READ";

function Notification(props) {
  const { numUnRead } = useNotificationState();
  const { handleClose, currentURL, notification } = props;

  //
  const {
    id,
    url,
    content,
    time,
    read,
    avatar: avatarContent,
    avatarColor,
  } = useState(notification);

  const classes = useStyles(read?.get());

  const avatar = (
    <Avatar
      alt="notification"
      className={classes.avatar}
      style={{
        backgroundColor: avatarColor?.get(),
      }}
    >
      {avatarContent?.get()
        ? avatarContent.get().substring(0, 2).toLocaleUpperCase()
        : "N"}
    </Avatar>
  );

  // Temporary
  const closeIfOnSameSite = (e) => {
    // TODO: consider case URL = ""
    if (currentURL === url.get()) {
      // e.preventDefault();
      handleClose(e);
    }
  };

  //
  const onClick = (e) => {
    if (!read.get()) {
      request(
        "patch",
        `/notification/${id.get()}/status`,
        (res) => {
          if (!read.get()) {
            read.set(true);
            numUnRead.set(numUnRead.get() - 1);
            closeIfOnSameSite(e);
          }
        },
        { onError: () => closeIfOnSameSite(e), 401: () => {} },
        { status: STATUS_NOTIFICATION_READ }
      );
    } else {
      closeIfOnSameSite(e);
    }
  };

  return id?.get() ? (
    <ListItemLink
      disableGutters
      className={classes.itemLink}
      onClick={onClick}
      to={url.get()}
    >
      <div
        className={classes.contentContainer}
        style={{ display: "flex", alignItems: "flex-start", width: "100%" }}
      >
        <ListItemAvatar className={classes.itemAvatar}>{avatar}</ListItemAvatar>

        {/* {icon ? <ListItemIcon>{icon}</ListItemIcon> : null} */}
        <div
          style={{
            padding: "6px 0px 10px",
            position: "relative",
            display: "flex",
            flexGrow: 1,
          }}
        >
          <div
            style={{
              position: "relative",
              flexGrow: 1,
            }}
          >
            <Typography className={classes.content}>{content.get()}</Typography>
            <Typography
              color={read.get() ? "inherit" : "primary"}
              className={classes.time}
            >
              {formatTime(time.get())}
            </Typography>
          </div>
          <NotificationReadIcon read={read.get()} />
        </div>
      </div>
    </ListItemLink>
  ) : (
    <Box
      position="relative"
      pl={2}
      pr={2}
      maxWidth="100%"
      display="flex"
      flexDirection="column"
      flexShrink={0}
    >
      <Box display="flex" pt={1} pb={1}>
        <Box
          mr="12px"
          display="flex"
          justifyContent="center"
          alignItems="flex-start"
        >
          <Skeleton variant="circle" width={56} height={56} />
        </Box>
        <Box
          flexBasis={0}
          alignSelf="center"
          minWidth={0}
          flexShrink={1}
          flexGrow={1}
          width="100%"
        >
          <Typography variant="body1" component="div">
            <Skeleton
              width={`${getRandomIntInclusive(50, 100)}%`}
              style={{ borderRadius: 8 }}
            />
          </Typography>
        </Box>
      </Box>
    </Box>
  );
}

Notification.whyDidYouRender = {
  logOnDifferentValues: true,
};

export default React.memo(Notification);
