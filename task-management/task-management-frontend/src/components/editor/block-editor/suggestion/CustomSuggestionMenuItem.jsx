import { ListItemIcon, MenuItem } from "@mui/material";
import { useRef, useEffect } from "react";
import PropTypes from "prop-types";

const MIN_LEFT_MARGIN = 5;

const CustomSuggestionMenuItem = (props) => {
  const itemRef = useRef(null);

  function isSelected() {
    const isKeyboardSelected = props.isSelected;
    const isMouseSelected = itemRef.current?.matches(":hover");

    return isKeyboardSelected || isMouseSelected;
  }

  function updateSelection() {
    isSelected()
      ? itemRef.current?.setAttribute("data-hovered", "true")
      : itemRef.current?.removeAttribute("data-hovered");
  }

  useEffect(() => {
    // Updates whether the item is selected with the keyboard (triggered on selectedIndex prop change).
    updateSelection();

    if (
      isSelected() &&
      itemRef.current &&
      itemRef.current.getBoundingClientRect().left > MIN_LEFT_MARGIN //TODO: hacky, fix
      // This check is needed because initially the menu is initialized somewhere above outside the screen (with left = 1)
      // scrollIntoView() is called before the menu is set in the right place, and without the check would scroll to the top of the page every time
    ) {
      itemRef.current.scrollIntoView({
        behavior: "smooth",
        block: "nearest",
      });
    }
  });

  return (
    <MenuItem
      onClick={props.onClick}
      // Ensures an item selected with both mouse & keyboard doesn't get deselected on mouse leave.
      onMouseLeave={() => {
        setTimeout(() => {
          updateSelection();
        }, 1);
      }}
    >
      <ListItemIcon>{props.icon}</ListItemIcon>
      <span ref={itemRef}>{props.title}</span>
    </MenuItem>
  );
};

CustomSuggestionMenuItem.propTypes = {
  title: PropTypes.string.isRequired,
  icon: PropTypes.node.isRequired,
  onClick: PropTypes.func.isRequired,
  isSelected: PropTypes.bool,
};

export { CustomSuggestionMenuItem };
