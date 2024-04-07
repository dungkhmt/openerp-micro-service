import {
  CircularProgress,
  ClickAwayListener,
  Grow,
  MenuItem,
  MenuList,
  Paper,
  Popper,
  Typography,
} from "@mui/material";
import PropTypes from "prop-types";
import { Children, useMemo, useState, useRef } from "react";
import { CustomSuggestionMenuItem } from "./CustomSuggestionMenuItem";

const groupItems = (items) => {
  const groupedItems = new Map();

  for (let i = 0; i < items.length; i++) {
    const item = items[i];
    if (groupedItems.has(item.group)) {
      groupedItems.get(item.group).push(item);
    } else {
      groupedItems.set(item.group, [item]);
    }
  }

  return groupedItems;
};

const CustomSuggestionMenu = (props) => {
  const { items, loadingState, selectedIndex, onItemClick } = props;

  const [open, setOpen] = useState(true);
  const popperRef = useRef(null);

  const loader =
    loadingState === "loading-initial" || loadingState === "loading" ? (
      <CircularProgress
        className={"bn-slash-menu-loader"}
        variant="indeterminate"
      />
    ) : null;

  const renderedItems = useMemo(() => {
    const groupedItems = groupItems(items);
    const renderedItems = [];

    for (const [group, gItems] of groupedItems) {
      if (group) {
        renderedItems.push(
          <Typography key={group} variant="caption" sx={{ px: 2, py: 1 }}>
            {group}
          </Typography>
        );

        for (let i = 0; i < gItems.length; i++) {
          const item = gItems[i];
          renderedItems.push(
            <CustomSuggestionMenuItem
              {...item}
              key={item.title}
              onClick={() => {
                setOpen(false);
                onItemClick?.(item);
              }}
              isSelected={
                selectedIndex === items.find((i) => i.title === item.title)
              }
            />
          );
        }
      }
    }
    return renderedItems;
  }, [items, selectedIndex, onItemClick]);

  const handleListKeyDown = (event) => {
    console.log(event);
    if (event.key === "Tab") {
      event.preventDefault();
      setOpen(false);
    } else if (event.key === "Escape") {
      setOpen(false);
    }
  };

  return (
    <Popper
      transition
      open={open}
      disablePortal
      popperRef={popperRef}
      autoFocus
      popperOptions={{
        modifiers: [
          {
            name: "flip",
            options: {
              enabled: true,
              boundary: "window",
            },
          },
        ],
      }}
    >
      {({ TransitionProps, placement }) => (
        <Grow
          {...TransitionProps}
          style={{
            transformOrigin:
              placement === "bottom-start" ? "left top" : "left bottom",
          }}
        >
          <Paper
            elevation={0}
            sx={{
              border: (theme) => `1px solid ${theme.palette.divider}`,
              maxHeight: "150px",
              overflowY: "auto",
            }}
          >
            <ClickAwayListener onClickAway={() => setOpen(false)}>
              <MenuList
                autoFocusItem={true}
                id="suggestion-menu"
                onKeyDown={handleListKeyDown}
              >
                {renderedItems}
                {Children.count(renderedItems) === 0 &&
                  (props.loadingState === "loading" ||
                    props.loadingState === "loaded") && (
                    <MenuItem>Không tìm thấy kết quả phù hợp</MenuItem>
                  )}
                {loader}
              </MenuList>
            </ClickAwayListener>
          </Paper>
        </Grow>
      )}
    </Popper>
  );
};

CustomSuggestionMenu.propTypes = {
  items: PropTypes.array,
  loadingState: PropTypes.oneOf(["loading-initial", "loading", "loaded"]),
  selectedIndex: PropTypes.number,
  onItemClick: PropTypes.func,
};

export { CustomSuggestionMenu };
