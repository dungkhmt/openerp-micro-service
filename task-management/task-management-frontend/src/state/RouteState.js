import { hookstate, useHookstate } from "@hookstate/core";

export const routeState = hookstate({
  currentRoute: undefined, // When navigate to a public route from a private route, this state is previous route
});

export function useRouteState() {
  return useHookstate(routeState);
}
