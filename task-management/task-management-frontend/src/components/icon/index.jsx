import { Icon } from "@iconify/react";
import PropTypes from "prop-types";

const IconifyIcon = ({ icon, ...rest }) => {
  return <Icon icon={icon} fontSize="1.5rem" {...rest} />;
};

IconifyIcon.propTypes = {
  icon: PropTypes.string.isRequired,
};

export default IconifyIcon;
