import { hookstate, useHookstate } from "@hookstate/core";
// Assuming your api request function is located similarly to the example
// Adjust the path if your 'api.js' or request function is elsewhere.
import { request } from "../api";

/**
 * State for managing permitted menu item IDs.
 */
export const navigationMenuState = hookstate({
  isFetching: false,
  isFetched: false,
  permittedMenuIds: new Set(), // Stores the string IDs of permitted menu items
});

/**
 * Hook to access and subscribe to navigationMenuState.
 * @returns {import("@hookstate/core").State<typeof navigationMenuState.value>}
 */
export function useNavigationMenuState() {
  return useHookstate(navigationMenuState);
}

/**
 * Fetches the list of permitted menu IDs from the backend.
 * The API endpoint /entity-authorization/MENU_ is used as per the example.
 * Expects the response data (res.data) to be an array of strings (menu IDs).
 */
export function fetchPermittedMenuIds() {
  const state = navigationMenuState; // Get a direct reference to the state

  // Avoid redundant fetches if already fetching or successfully fetched.
  // You can remove !state.isFetched.get() if you always want to allow refetching.
  if (state.isFetching.get()) {
    console.log("Already fetching permitted menu IDs.");
    return;
  }

  state.isFetching.set(true);
  state.isFetched.set(false); // Reset fetched status on new fetch attempt

  request(
    "get", // HTTP method
    "/entity-authorization/MENU_", // API endpoint
    (res) => {
      // Assuming res.data is an array of menu item IDs (strings)
      const ids = Array.isArray(res.data) ? res.data : [];
      state.merge({
        permittedMenuIds: new Set(ids),
        isFetching: false,
        isFetched: true,
      });
    },
    {
      // Error handler for network issues or non-2xx/401 responses
      onError: (err) => {
        console.error("Failed to fetch permitted menu IDs:", err);
        state.merge({
          isFetching: false,
          isFetched: false, // Indicate that fetching failed
          permittedMenuIds: new Set(), // Clear any potentially stale IDs
        });
      },
      // Specific handler for 401 Unauthorized
      401: () => {
        console.warn("Unauthorized (401) to fetch permitted menu IDs.");
        state.merge({
          isFetching: false,
          isFetched: false,
          permittedMenuIds: new Set(),
        });
        // Potentially trigger a logout or redirect here
      },
      // You can add handlers for other specific HTTP status codes if needed
      // e.g., 403: () => { ... }
    }
  );
}
