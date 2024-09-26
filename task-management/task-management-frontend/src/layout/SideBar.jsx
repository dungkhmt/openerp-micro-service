import PropTypes from "prop-types";
import navigationItem from "../config/navigation";
import { Navigation } from "./components/navigation";

export const navWidth = 260;
export const collapsedNavWidth = 60;

export default function SideBar(props) {
  const { navVisible, setNavVisible } = props;

  const toggleNavVisibility = () => setNavVisible(!navVisible);

  return (
    <Navigation
      navWidth={navWidth}
      collapsedNavWidth={collapsedNavWidth}
      toggleNavVisibility={toggleNavVisibility}
      items={navigationItem}
      {...props}
    />
  );
}

SideBar.propTypes = {
  navVisible: PropTypes.bool,
  setNavVisible: PropTypes.func,
};
