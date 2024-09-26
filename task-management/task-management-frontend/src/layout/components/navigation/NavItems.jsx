import PropTypes from "prop-types";
import { SectionTitle } from "./SectionTitle";
import { NavGroup } from "./NavGroup";
import { NavLink } from "./NavLink";

const resolveNavItemComponent = (item) => {
  if (item.sectionTitle) return SectionTitle;
  if (item.children) return NavGroup;

  return NavLink;
};

const NavItems = (props) => {
  const { items } = props;

  const RenderMenuItems = items?.map((item, index) => {
    const TagName = resolveNavItemComponent(item);

    return <TagName {...props} key={index} item={item} />;
  });

  return <>{RenderMenuItems}</>;
};

NavItems.propTypes = {
  items: PropTypes.array.isRequired,
};

export { NavItems };
