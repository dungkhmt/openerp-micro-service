import PropTypes from "prop-types";
import { useEffect, useMemo } from "react";
// Assuming originalNavigationItems is your main navigation config file
import originalNavigationItems from "../config/navigation";
// Assuming Navigation is the component that renders the menu (index.jsx in your files)
import { Navigation } from "./components/navigation";
// Import the state and fetch function for permitted menu IDs
// Adjust this path based on where you placed navigationMenuState.js
// e.g., if SideBar.jsx is in src/layouts/ and navigationMenuState.js is in src/state/
import { useNavigationMenuState, fetchPermittedMenuIds} from "../state/navigationMenuState.js";

export const navWidth = 260;
export const collapsedNavWidth = 60;

/**
 * Recursively filters navigation items based on a set of permitted IDs.
 * - Items without an 'id' are always included.
 * - Items with an 'id' are included if the 'id' is in permittedIds.
 * - A group item is included only if it's permitted and has at least one permitted child.
 * - Section titles are included if they are followed by at least one permitted item.
 * @param {Array<Object>} items - The array of navigation items to filter.
 * @param {Set<String>} permittedIds - A Set containing the IDs of permitted menu items.
 * @returns {Array<Object>} The filtered array of navigation items.
 */
function filterPermittedNavigationItems(items, permittedIds) {
  if (!items || !Array.isArray(items)) {
    return [];
  }

  // First pass: filter individual items and their children recursively
  const filteredOnce = items.map(item => {
    const newItem = { ...item }; // Shallow clone to avoid mutating original config

    // Determine if the item itself is permitted
    // An item is permitted if it has no 'id' or its 'id' is in the permittedIds set.
    const isItemItselfPermitted = !newItem.id || permittedIds.has(newItem.id);

    if (!isItemItselfPermitted) {
      return null; // Item is not permitted, so discard it (and its children implicitly)
    }

    // If the item is permitted and has children, recursively filter its children
    if (newItem.children && Array.isArray(newItem.children)) {
      newItem.children = filterPermittedNavigationItems(newItem.children, permittedIds);

      // If it's a group (had children defined originally) and now has no visible children,
      // then this group should not be shown.
      if (item.children && newItem.children.length === 0) {
        return null;
      }
    }
    // If item is permitted (and if it's a group, it has permitted children, or it's a leaf node)
    return newItem;
  }).filter(Boolean); // Remove null entries (items that were not permitted or became empty groups)


  // Second pass: Remove section titles that are not followed by any visible non-section items.
  const finalFilteredResult = [];
  for (let i = 0; i < filteredOnce.length; i++) {
    const currentItem = filteredOnce[i];
    if (currentItem.sectionTitle) {
      // A section title is kept if the *next* item in the filtered list exists and is NOT a section title.
      if (i + 1 < filteredOnce.length && !filteredOnce[i + 1].sectionTitle) {
        finalFilteredResult.push(currentItem);
      }
      // Otherwise, the section title is removed (it's either last, or followed by another section title).
    } else {
      // It's a regular navigation item or group, keep it.
      finalFilteredResult.push(currentItem);
    }
  }
  return finalFilteredResult;
}


export default function SideBar(props) {
  const { navVisible, setNavVisible } = props;

  // Get the state for permitted menu IDs
  const menuAuthState = useNavigationMenuState();
  // Destructure values from the state. Using .get() for direct value access if not subscribing in JSX directly.
  const { permittedMenuIds, isFetched, isFetching } = menuAuthState.get();

  useEffect(() => {
    // Fetch permitted IDs when the component mounts,
    // but only if not already fetched and not currently fetching.
    if (!isFetched && !isFetching) {
      fetchPermittedMenuIds();
    }
  }, [isFetched, isFetching]); // Effect dependencies

  const toggleNavVisibility = () => setNavVisible(!navVisible);

  // Memoize the filtered navigation items.
  // This recalculates only if the original items, permitted IDs, or fetched status change.
  const filteredNavItems = useMemo(() => {
    // If data hasn't been fetched yet, permittedMenuIds will be an empty Set.
    // This means items requiring an ID will be hidden, and items without an ID will be shown.
    // This is generally a safe default (show public/unrestricted items).
    // If you prefer to show nothing or a loader, you can add that logic here based on isFetching/isFetched.
    if (!isFetched && isFetching) {
      // Optionally return an empty array or a specific "loading" structure
      // return [];
    }
    return filterPermittedNavigationItems(originalNavigationItems, permittedMenuIds);
  }, [permittedMenuIds, isFetched]); // Removed originalNavigationItems as it's usually static. Add if it can change.


  // If still fetching, you might want to show a loading indicator within the sidebar.
  // For now, it will render based on the current state of filteredNavItems.
  // Example: if (isFetching) return <SidebarSkeleton />;

  return (
    <Navigation
      navWidth={navWidth}
      collapsedNavWidth={collapsedNavWidth}
      toggleNavVisibility={toggleNavVisibility}
      items={filteredNavItems} // Pass the filtered items to the Navigation component
      {...props} // Pass through other props like navCollapsed
    />
  );
}

SideBar.propTypes = {
  navVisible: PropTypes.bool,
  setNavVisible: PropTypes.func,
  // Ensure other props passed to Navigation (like navCollapsed) are also defined here
  // if they are expected by SideBar and passed down.
};
