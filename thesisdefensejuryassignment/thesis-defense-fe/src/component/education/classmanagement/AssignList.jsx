import {Button, Collapse, List, ListItemIcon} from "@material-ui/core";
import React, {Fragment, useState} from "react";
import {StyledBadge} from "./StyledBadge";
import {makeStyles} from "@material-ui/core/styles";
import ChevronRightRoundedIcon from "@material-ui/icons/ChevronRightRounded";
import clsx from "clsx";

const useStyles = makeStyles(() => ({
  icon: {
    minWidth: 24,
    justifyContent: "center",
  },
  title: {
    fontSize: "1rem",
    paddingBottom: 0,
    marginTop: 12,
    textTransform: "none",
  },

  open: { transform: "rotate(90deg)", transition: "0.3s" },
  close: { transition: "0.3s" },
  item: {
    paddingLeft: 32,
  },
}));

function AssignList(props) {
  const classes = useStyles();
  const { title, children } = props;

  const [open, setOpen] = useState(false);

  const handleClick = () => {
    setOpen(!open);
  };

  return (
    <Fragment>
      <Button
        disableRipple
        onClick={handleClick}
        className={classes.title}
        startIcon={
          <ListItemIcon className={classes.icon}>
            <ChevronRightRoundedIcon
              className={clsx(!open && classes.close, open && classes.open)}
            />
          </ListItemIcon>
        }
      >
        <StyledBadge badgeContent={children.length} color="error">
          {title}
        </StyledBadge>
      </Button>
      <br />
      <Collapse in={open} timeout="auto" unmountOnExit>
        {children.length > 0 && (
          <List component="div" className={classes.item}>
            {children}
          </List>
        )}
      </Collapse>
    </Fragment>
  );
}

export default AssignList;
