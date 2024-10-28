import {Collapse, Fade, Icon, ListItem, ListItemText,} from "@material-ui/core";
import makeStyles from "@material-ui/core/styles/makeStyles";
import ArrowDropDownIcon from "@material-ui/icons/ArrowDropDown";
import classNames from "classnames";
import React, {Fragment, useState} from "react";
import {connect} from "react-redux";
import {NavLink} from "react-router-dom";
import {whiteColor} from "../../../assets/jss/material-dashboard-react";
import styles from "../../../assets/jss/material-dashboard-react/components/sidebarStyle";
import {menuIconMap} from "../../../config/menuconfig";

const useStyles = makeStyles(() => ({
  firstOrderMenu: {
    color: whiteColor,
    "&.MuiListItem-button:hover": {
      backgroundColor: "rgba(200, 200, 200, 0.2)",
    },
  },
  iconExpand: { transform: "rotate(-180deg)", transition: "0.3s" },
  iconCollapse: { transition: "0.3s" },
}));

function MenuItem(props) {
  const assetClasses = makeStyles(styles)();
  const classes = useStyles();

  const { color, config, open, selectedMenu, menu } = props;
  const [expanded, setExpanded] = useState(false);

  if (!config.isPublic) {
    if (!menu?.has(config.id)) return null;
  }

  let listItemClasses = classNames({
    [" " + assetClasses[color]]: selectedMenu?.id == config.id,
  });

  const whiteFontClasses = classNames({
    [" " + assetClasses.whiteFont]: true,
  });

  if (config.child?.length > 0) {
    return (
      <Fragment>
        <ListItem
          button
          key={config.id}
          className={classNames(assetClasses.itemLink, {
            [assetClasses.firstOrderMenu]:
              selectedMenu?.parent?.id == config.id,
            [classes.firstOrderMenu]: !(selectedMenu?.parent?.id == config.id),
          })}
          onClick={() => setExpanded(!expanded)}
        >
          {/* Icon */}
          <Icon
            className={classNames(assetClasses.itemIcon, whiteFontClasses)}
            style={{ paddingLeft: 3 }}
          >
            {menuIconMap.get(config.icon)}
          </Icon>

          {/* Label */}
          <Fade in={open} timeout={500}>
            <ListItemText
              primary={config.text}
              className={classNames(assetClasses.itemText, whiteFontClasses)}
              disableTypography={true}
            />
          </Fade>

          <Icon
            className={classNames(assetClasses.itemIcon, whiteFontClasses, {
              [classes.iconExpand]: expanded,
              [classes.iconCollapse]: !expanded,
            })}
            style={{ marginRight: 0, marginLeft: 6 }}
          >
            <ArrowDropDownIcon />
          </Icon>
        </ListItem>
        <Collapse in={expanded} timeout="auto">
          {config.child.map((childItem) => (
            <MenuItem
              key={childItem.text}
              config={childItem}
              open={open}
              color={color}
              selectedMenu={selectedMenu}
              menu={menu}
            />
          ))}
        </Collapse>
      </Fragment>
    );
  } else
    return (
      <NavLink
        to={process.env.PUBLIC_URL + config.path}
        className={" " + assetClasses.item}
        activeClassName="active"
      >
        <ListItem
          button
          className={classNames(assetClasses.itemLink + listItemClasses, {
            [classes.firstOrderMenu]: !(selectedMenu?.id == config.id),
          })}
          disableGutters={false}
          style={{
            height: 40,
            borderRadius: 3,
            minWidth: 50,
          }}
        >
          <Fade in={open} timeout={500}>
            <ListItemText
              primary={config.text}
              className={classNames(assetClasses.itemText, whiteFontClasses)}
              disableTypography={true}
              style={{ paddingLeft: 40 }}
            />
          </Fade>
        </ListItem>
      </NavLink>
    );
}

const mapStateToProps = (state) => ({
  menu: state.menu.menu,
});

export default connect(mapStateToProps)(MenuItem);
