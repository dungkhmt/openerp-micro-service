import { createState, useState } from "@hookstate/core";

export const routeState = createState({
  currentRoute: undefined, // When navigate to a public route from a private route, this state is previous route
});

export function useRouteState() {
  return useState(routeState);
}
