import {useEffect} from "react";

/**
 * Hook to run a handler once on mount and never again.
 * @param {() => void} handler Function to run on mount.
 * See https://reactjs.org/docs/hooks-reference.html#useeffect
 * See https://github.com/facebook/create-react-app/issues/6880
 * This function is mainly to provide a better signal to the developer than
 * knowing how `useEffect` works when you pass an empty array. It also helps to
 * get around warnings raised by `react-hooks/exhaustive-deps` and we use it
 * instead of `// eslint-disable-next-line react-hooks/exhaustive-deps`.
 * We only run the handler once, so we don't care if dependencies change!
 */
export default function useOnMount(handler) {
  return useEffect(handler, []);
}
