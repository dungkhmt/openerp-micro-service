// hooks/useErrorHandler.js
import { useDispatch } from "react-redux";
import { useTranslation } from "react-i18next";
import toast from "react-hot-toast";

export const useAPIExceptionHandler = (fetchLoading, errors, clearErrorsAction) => {
  const { t } = useTranslation();
  const dispatch = useDispatch();

  // If loading, let the component handle it
  if (fetchLoading) {
    return { errorType: null };
  }

  if (errors?.length > 0) {
    const { code, message } = errors[0];
    if (code?.includes("E03")) {
      return { errorType: "notFound" };
    }
    if (code?.includes("E02")) {
      toast.error(t(message) || "Có lỗi xảy ra!");
      dispatch(clearErrorsAction());
      return { errorType: null };
    }
    return { errorType: "unknown" };
  }

  return { errorType: null };
};