import { Box, List, Typography } from "@mui/material";
import ClickAwayListener from "@mui/material/ClickAwayListener";
import Grow from "@mui/material/Grow";
import MenuList from "@mui/material/MenuList";
import Paper from "@mui/material/Paper";
import Popper from "@mui/material/Popper";
import { ReactComponent as EmptyNotificationIcon } from "../../assets/icons/undraw_happy_announcement_ac67.svg";
import { OverlayScrollbarsComponent } from "overlayscrollbars-react";
import "overlayscrollbars/css/OverlayScrollbars.css";
import React, { useEffect } from "react";
import InfiniteScroll from "react-infinite-scroll-component";
import { useLocation } from "react-router";
import { notificationMenuWidth } from "./ActionsWithNotificationButton";
import Notification from "./Notification";
import NotificationTitle from "./NotificationTitle";

const styles = {
  paper: {
    overflowY: "hidden",
    // maxHeight: `calc(100vh - 80px)`,
    height: `calc(100vh - 80px)`,
    width: notificationMenuWidth,
    borderRadius: 2,
    boxShadow:
      "0 12px 28px 0 rgba(0, 0, 0, 0.2), 0 2px 4px 0 rgba(0, 0, 0, 0.1), inset 0 0 0 1px rgba(255, 255, 255, 0.5)",
  },
  notificationsLoadingList: {
    "& div:first-of-type": {
      "& div": {
        paddingTop: 0,
      },
    },
  },
};

const NotificationsLoading = React.memo(({ quantity }) => {
  const notifications = [];

  for (let i = 0; i < quantity; i++) {
    notifications.push(<Notification key={i} />);
  }

  return notifications;
});

export default function NotificationMenu({
  open,
  anchorRef,
  notifications,
  next,
  hasMore,
}) {
  const { pathname } = useLocation();

  //
  const [displayInfiniteScroll, setDisplayInfiniteScroll] =
    React.useState(false);

  // Use useCallback to prevent Notification rerender because callback is recreated.
  const handleClose = React.useCallback((event) => {
    if (anchorRef.current && anchorRef.current.contains(event.target)) {
      return;
    }

    open.set(false);
  }, []);

  function handleListKeyDown(event) {
    if (event.key === "Tab") {
      event.preventDefault();
      open.set(false);
    }
  }

  // This effect is used to make sure the OverlayScrollbarsComponent already exists in the DOM
  // before rendering InfiniteScroll
  useEffect(() => {
    if (open.get() === true) setDisplayInfiniteScroll(true);
    else setDisplayInfiniteScroll(false);
  }, [open.get()]);

  return (
    <Popper
      transition
      disablePortal
      open={open.get()}
      anchorEl={anchorRef.current}
      modifiers={[
        {
          name: "flip",
          enabled: false,
        },
        {
          name: "preventOverflow",
          enabled: true,
          options: {
            padding: 36,
            boundary: "scrollParent",
          },
        },
      ]}
    >
      {({ TransitionProps, placement }) => (
        <Grow
          {...TransitionProps}
          style={{
            transformOrigin: placement.startsWith("bottom")
              ? "center top"
              : "center bottom",
          }}
        >
          <Paper elevation={0} sx={styles.paper}>
            <ClickAwayListener onClickAway={handleClose}>
              <MenuList
                autoFocusItem={open.get()}
                id="menu-list-grow"
                onKeyDown={handleListKeyDown}
                style={{ padding: 0 }}
              >
                <OverlayScrollbarsComponent
                  style={{
                    width: notificationMenuWidth,
                    maxHeight: `calc(100vh - 80px)`,
                    // overscrollBehaviorY: "none", // To prevent tag <main> be scrolled when menu'scrollbar reach end
                  }}
                  options={{ scrollbars: { autoHide: "scroll" } }}
                >
                  {notifications.get() ? (
                    <>
                      <NotificationTitle
                        showActionsMenu={notifications.length > 0}
                      />
                      {notifications.length > 0 ? (
                        displayInfiniteScroll && (
                          <List disablePadding aria-label="notifications list">
                            <InfiniteScroll
                              dataLength={notifications.get().length}
                              next={next}
                              hasMore={hasMore.get()}
                              scrollThreshold={0.85}
                              loader={<NotificationsLoading quantity={2} />}
                              scrollableTarget={document.querySelector(
                                ".os-viewport"
                              )}
                            >
                              {notifications.map((notification) => (
                                <Notification
                                  key={notification.id.get()}
                                  notification={notification}
                                  currentURL={pathname}
                                  handleClose={handleClose}
                                />
                              ))}
                            </InfiniteScroll>
                          </List>
                        )
                      ) : (
                        // Empty notification.
                        <Box
                          display="flex"
                          alignItems="center"
                          flexDirection="column"
                          pl={4}
                          pr={4}
                          pb={3}
                        >
                          <EmptyNotificationIcon width={225} height={200} />
                          <Typography style={{ textAlign: "center" }}>
                            Đừng bỏ lỡ những thông tin quan trọng. Khi có thông
                            báo mới, chúng sẽ hiển thị tại đây
                          </Typography>
                        </Box>
                      )}
                    </>
                  ) : (
                    // Notifications loading.
                    <div>
                      <NotificationTitle />
                      <List
                        disablePadding
                        aria-label="notifications list"
                        sx={styles.notificationsLoadingList}
                      >
                        <NotificationsLoading quantity={10} />
                      </List>
                    </div>
                  )}
                </OverlayScrollbarsComponent>
              </MenuList>
            </ClickAwayListener>
          </Paper>
        </Grow>
      )}
    </Popper>
  );
}

// NotificationMenu.whyDidYouRender = {
//   logOnDifferentValues: true,
// };
