import { ListItem } from "@mui/material";
import _ from "lodash";
import PropTypes from "prop-types";
import { forwardRef, useMemo } from "react";
import { Link as RouterLink, useLocation } from "react-router-dom";

export default function ListItemLink({ sx, to, children, ...props }) {
  const { pathname } = useLocation();

  const renderLink = useMemo(
    () =>
      // eslint-disable-next-line react/display-name
      forwardRef((linkProps, ref) => (
        <RouterLink
          ref={ref}
          to={to}
          replace={to === pathname}
          {...linkProps}
        />
      )),
    [to, pathname]
  );

  return _.isString(to) ? (
    <li>
      <ListItem sx={sx} component={renderLink} {...props}>
        {children}
      </ListItem>
    </li>
  ) : (
    <ListItem sx={sx} {...props}>
      {children}
    </ListItem>
  );
}

ListItemLink.propTypes = {
  to: PropTypes.string.isRequired,
  children: PropTypes.node.isRequired,
  sx: PropTypes.func,
};
