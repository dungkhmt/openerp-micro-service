import { Icon } from "@iconify/react";
import { Box, Badge, IconButton } from "@mui/material";
import { styled } from "@mui/material/styles";
import { EventSourcePolyfill } from "event-source-polyfill";
import randomColor from "randomcolor";
import React from "react";
import { BASE_URL, bearerAuth } from "../../../api";
import keycloak from "../../../config/keycloak";
import { useNotificationState } from "../../../state/NotificationState";
import NotificationMenu from "./NotificationMenu";
import { request } from "../../../api";
import { useSelector } from "react-redux";

const StyledAvatar = styled(Box)(({ isOpen, theme }) => ({
  width: 25,
  height: 25,
  color: "#ddeeddee",
  display: "flex",
  alignItems: "center",
  justifyContent: "center",
  // backgroundColor: grey[200],
  overflow: "unset",
  "& svg": {
    width: 20,
    height: 20,
    ...(isOpen && { color: theme.palette.primary.main }),
  },
}));

const StyledBadge = styled(Badge)(() => ({
  "& .MuiBadge-badge": {
    fontSize: "0.6rem",
  },
}));

const SSE_EVENTS = {
  HEARTBEAT: "HEARTBEAT",
  NEW_NOTIFICATION: "NEW_NOTIFICATION",
};

const processNotificationsContent = (notifications) => {
  return notifications.map((notification) => ({
    id: notification.id,
    url: notification.url,
    avatar: notification.avatar,
    content: notification.content,
    time: notification.createdStamp,
    read: notification.read,
    avatarColor: randomColor({
      luminosity: "dark",
      hue: "random",
    }),
  }));
};

function NotificationButton() {
  const [open, setOpen] = React.useState(false);
  const { notifications, numUnRead, hasMore } = useNotificationState();
  const organizationCode = useSelector(
    (state) => state.organization.currentOrganization?.code
  );

  // return focus to the button when we transitioned from !open -> open
  const prevOpen = React.useRef(open);
  const anchorRef = React.useRef(null);

  const handleToggle = () => {
    setOpen((prevOpen) => !prevOpen);
  };

  const fetchNotification = () => {
    if (!organizationCode) {
      notifications.set([]);
      numUnRead.set(0);
      hasMore.set(false);
      return;
    }

    let fromId = null;
    const fetchedNoties = notifications.get();
    if (fetchedNoties && fetchedNoties.length > 0) {
      fromId = fetchedNoties[fetchedNoties.length - 1].id;
    }
    try {
      request(
        "get",
        `/notification?fromId=${fromId || ""}&page=${0}&size=${10}`,
        (res) => {
          let data = res.data;
          const noties = processNotificationsContent(
            data.notifications.content
          );
          if (fromId === null) {
            notifications.set(noties);
          } else {
            notifications.merge(noties);
          }
          numUnRead.set(data.numUnRead);
          hasMore.set(!data.notifications.last);
        },
        { 401: () => {} }
      );
    } catch (e) {
      notifications.set([]);
      numUnRead.set(0);
      hasMore.set(false);
    }
  };

  React.useEffect(() => {
    if (prevOpen.current === true && open === false) {
      anchorRef.current.focus();
    }
    prevOpen.current = open;
    // if (open.get() === false && numUnRead.get() > 0) numUnRead.set(0);
  }, [open]);

  React.useEffect(() => {
    // Reset notification state when organization changes
    notifications.set([]);
    numUnRead.set(0);
    hasMore.set(false);

    // When user open multiple tabs, only one tab will receive events at any point of time,
    // all other tabs will wait for "heartbeatTimeout" secs and reconnect to server,
    // one of them will successfully connect and receive next events

    // SSE event handlers
    const handleHeartbeatEvent = function () {
      if (!notifications.get()) fetchNotification();
      // console.log(new Date(), e);
    };

    const handleNewNotificationEvent = function (e) {
      if (notifications.get()) {
        let newNotification = processNotificationsContent([JSON.parse(e.data)]);
        const len = notifications.get().length;

        if (len === 0) {
          // Notification list is empty
          notifications.set(newNotification);
          numUnRead.set(1);
        } else {
          newNotification = newNotification[0];
          const newCreatedTime = new Date(newNotification.time).getTime();
          let consideredCreatedTime;

          // case 1: new is later than the considered one -> insert at that position and stop
          // case 2: new is the same as the considered one -> stop
          // case 3: new is earlier than the considered one -> continuously iterate
          for (let i = 0; i < len; i++) {
            consideredCreatedTime = new Date(
              notifications[i].time.get()
            ).getTime();

            if (newCreatedTime > consideredCreatedTime) {
              notifications.set((p) => {
                p.splice(i, 0, newNotification);
                return p;
              });

              numUnRead.set(numUnRead.get() + 1);
              return;
            } else if (newCreatedTime === consideredCreatedTime) {
              return;
            }
          }
        }
      } else {
        fetchNotification();
      }
    };

    const onError = function (e) {
      // When server SseEmitters timeout, it cause error
      // console.error(
      //   `EventSource connection state: ${
      //     es.readyState
      //   }, error occurred: ${JSON.stringify(e)}`
      // );

      if (e.target.readyState === EventSource.CLOSED) {
        // console.log(
        //   new Date(),
        //   `SSE closed (event readyState = ${e.target.readyState})`
        // );
      } else if (e.target.readyState === EventSource.CONNECTING) {
        // console.log(
        //   new Date(),
        //   `SSE reconnecting (event readyState = ${e.target.readyState})`
        // );
      }

      es.close();
      // console.info(new Date(), `SSE closed`);
      reconnect();
    };

    // Setup EventSource
    let es;
    let reconnectFrequencySeconds = 1;

    // Putting these functions in extra variables is just for the sake of readability
    const wait = function () {
      return reconnectFrequencySeconds * 1000;
    };

    const tryToSetup = function () {
      setupEventSource();
      reconnectFrequencySeconds *= 2;

      if (reconnectFrequencySeconds >= 64) {
        reconnectFrequencySeconds = 64;
      }
    };

    // Reconnect on every error
    const reconnect = function () {
      setTimeout(tryToSetup, wait());
    };

    // let count = 0;

    function setupEventSource() {
      fetchNotification();

      es = new EventSourcePolyfill(`${BASE_URL}/notification/subscription`, {
        headers: {
          Authorization: bearerAuth(keycloak.token),
          "X-Organization-Code": organizationCode,
          // Count: count++,
        },
        heartbeatTimeout: 120000,
      });

      // In fact, this callback function is usually not fired as soon as the connection is opened,
      // but fired when the first event is received. Don't know the reason but this doesn't matter
      es.onopen = () => {
        // console.info(new Date(), `SSE opened`);
        // reconnectFrequencySeconds = 1;
      };

      // This event only to keep sse connection alive
      es.addEventListener(SSE_EVENTS.HEARTBEAT, handleHeartbeatEvent);

      es.addEventListener(
        SSE_EVENTS.NEW_NOTIFICATION,
        handleNewNotificationEvent
      );

      es.onerror = onError;
    }

    setupEventSource();

    return () => {
      es.close();
      es = null;
      // console.info(new Date(), `SSE closed`);
    };
  }, [organizationCode]);

  return (
    <>
      <IconButton
        disableRipple
        color="inherit"
        component="span"
        ref={anchorRef}
        aria-haspopup="true"
        aria-label="notification button"
        aria-controls={open ? "menu-list-grow" : undefined}
        onClick={handleToggle}
        sx={{ p: 0, mr: 1 }}
      >
        <StyledAvatar alt="notification button" isOpen={open}>
          {open ? (
            <Icon icon="gravity-ui:bell-dot" />
          ) : (
            <StyledBadge
              badgeContent={numUnRead.get() < 10 ? numUnRead.get() : "+9"}
              color="error"
            >
              <Icon icon="gravity-ui:bell-dot" />
            </StyledBadge>
          )}
        </StyledAvatar>
      </IconButton>
      <NotificationMenu
        open={open}
        setOpen={setOpen}
        anchorRef={anchorRef}
        notifications={notifications}
        next={fetchNotification}
        hasMore={hasMore}
      />
    </>
  );
}

// NotificationButton.whyDidYouRender = {
//   logOnDifferentValues: true,
// };

export default React.memo(NotificationButton);
