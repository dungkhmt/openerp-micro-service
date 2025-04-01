import { useDispatch } from "react-redux";
import { useTranslation } from "react-i18next";
import toast from "react-hot-toast";
import { useNavigate } from "react-router";

export const useAPIExceptionHandler = (
  fetchLoading,
  errors,
  clearErrorsAction
) => {
  const { t } = useTranslation();
  const dispatch = useDispatch();
  const navigate = useNavigate();

  if (fetchLoading || !errors?.length) {
    return;
  }

  const error = errors[0];
  const status = error?.status || error?.response?.status || 500;
  const message =
    error?.data?.message ||
    error?.response?.data?.message ||
    "Lỗi không xác định";

  if (error.config?.method === "get") {
    navigate("/not-found");
  } else {
    switch (status) {
      case 400: // Bad Request
        toast.error(t(message) || "Yêu cầu không hợp lệ!");
        break;

      case 403: // Forbidden
        toast.error(t(message) || "Không có quyền truy cập!");
        break;

      case 404: // Not Found
        toast.error(t(message) || "Không tìm thấy tài nguyên!");
        break;

      case 409: // Conflict
        toast.warn(t(message) || "Xảy ra xung đột!");
        break;

      default: // 500 or unknown
        navigate("/unknown-error", {
          state: { message: t(message) || "Lỗi không xác định" },
        });
    }
  }

  dispatch(clearErrorsAction());
};
