import { useRef } from "react";

export const usePreventOverflow = () => {
  const ref = useRef(null);

  return {
    ref,
    updateMaxHeight: () => {
      setTimeout(() => {
        if (!ref.current) return;

        // Preserve scroll position
        const scrollTop = ref.current.scrollTop;

        // Reset any previously set max-height
        ref.current.style.maxHeight = "none";

        // Get the menu DOM rect
        const domRect = ref.current.getBoundingClientRect();
        ref.current.style.maxHeight = `${window.innerHeight - domRect.top}px`;

        // Restore scroll position
        ref.current.scrollTop = scrollTop;
      }, 10);
    },
    updateHeight: (gap) => {
      if (!ref.current) return;

      // Preserve scroll position
      const scrollTop = ref.current.scrollTop;

      // Reset height
      ref.current.style.height = "auto";

      // Get the menu DOM rect
      const domRect = ref.current.getBoundingClientRect();
      ref.current.style.height = `${window.innerHeight - domRect.top - gap}px`;

      // Restore scroll position
      ref.current.scrollTop = scrollTop;
    },
  };
};
