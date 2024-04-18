import {ListItem} from "@material-ui/core";
import _ from "lodash";
// import PropTypes from "prop-types";
import React, {forwardRef, useMemo} from "react";
import {Link as RouterLink, useLocation} from "react-router-dom";

export default function ListItemLink({ className, to, children, ...props }) {
  const { pathname } = useLocation();

  const renderLink = useMemo(
    () =>
      forwardRef((linkProps, ref) => (
        <RouterLink
          ref={ref}
          to={to}
          replace={to === pathname}
          {...linkProps}
        />
      )),
    [to]
  );

  return _.isString(to) ? (
    <li>
      <ListItem className={className} component={renderLink} {...props}>
        {children}
      </ListItem>
    </li>
  ) : (
    <ListItem className={className} {...props}>
      {children}
    </ListItem>
  );
}

// ListItemLink.propTypes = {
//   to: PropTypes.string.isRequired,
// };
