import { useRef } from "react";

export const usePreventOverflow = () => {
  const ref = useRef(null);

  return {
    ref,
    updateMaxHeight: () => {
      // Seems a small delay is necessary to get the correct DOM rect
      setTimeout(() => {
        if (!ref.current) {
          return;
        }

        if (ref.current) {
          // Reset any previously set max-height
          ref.current.style.maxHeight = "none";

          // Get the menu DOM rect
          const domRect = ref.current.getBoundingClientRect();
          ref.current.style.maxHeight = `${window.innerHeight - domRect.top}px`;
        }
      }, 10);
    },
    updateHeight: (gap) => {
      if (!ref.current) {
        return;
      }

      if (ref.current) {
        // Reset any previously set max-height
        ref.current.style.height = "auto";

        // Get the menu DOM rect
        const domRect = ref.current.getBoundingClientRect();
        ref.current.style.height = `${
          window.innerHeight - domRect.top - gap
        }px`;
      }
    },
  };
};
