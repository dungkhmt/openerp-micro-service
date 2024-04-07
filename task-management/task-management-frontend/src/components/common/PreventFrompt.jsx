import { useEffect, useCallback, useRef } from "react";
import { useBeforeUnload, useBlocker } from "react-router-dom";
import PropTypes from "prop-types";

export const PreventPrompt = ({ hasUnsavedChanges, allowLinks, message }) => {
  const onLocationChange = useCallback(
    ({ nextLocation }) => {
      if (!allowLinks?.includes(nextLocation.pathname) && hasUnsavedChanges) {
        const confirm = window.confirm(
          message ??
            "Bạn có những thay đổi chưa lưu, bạn có chắc chắn muốn rời đi không?"
        );
        return !confirm;
      }
      return false;
    },
    [hasUnsavedChanges, allowLinks, message]
  );

  usePrompt(onLocationChange, hasUnsavedChanges);

  useBeforeUnload(
    useCallback(
      (event) => {
        if (hasUnsavedChanges) {
          event.preventDefault();
          event.returnValue =
            message ??
            "Bạn có những thay đổi chưa lưu, bạn có chắc chắn muốn rời đi không?";
        }
      },
      [hasUnsavedChanges, message]
    ),
    { capture: true }
  );

  return null;
};

PreventPrompt.propTypes = {
  hasUnsavedChanges: PropTypes.bool,
  allowLinks: PropTypes.arrayOf(PropTypes.string),
  message: PropTypes.string,
};

function usePrompt(onLocationChange, hasUnsavedChanges) {
  const blocker = useBlocker(hasUnsavedChanges ? onLocationChange : false);
  const prevState = useRef(blocker.state);

  useEffect(() => {
    if (blocker.state === "blocked") {
      blocker.reset();
    }
    prevState.current = blocker.state;
  }, [blocker]);
}
