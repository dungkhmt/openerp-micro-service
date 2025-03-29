import PropTypes from "prop-types";
import {
  Grid,
  FormControl,
  TextField,
  FormHelperText,
  Box,
  Button,
  DialogActions,
  DialogTitle,
  Dialog,
  DialogContent,
  IconButton,
  Icon,
} from "@mui/material";
import { Controller, useForm } from "react-hook-form";
import { DateTimePicker } from "@mui/x-date-pickers";
import { useState } from "react";
import dayjs from "dayjs";

const MeetingPlanDialog = ({
  initialValues,
  openDialog,
  onClose,
  onSubmit,
}) => {
  const {
    handleSubmit,
    control,
    formState: { errors },
  } = useForm({
    defaultValues: initialValues,
  });
  const [loading, setLoading] = useState(false);
  const [deadline, setDeadline] = useState(
    initialValues?.registrationDeadline ||
      dayjs().add(1, "day").endOf("day").millisecond(0).second(0).toDate()
  );
  const [deadlineError, setDeadlineError] = useState("");

  const isFormReadOnly = initialValues?.statusId === "PLAN_COMPLETED";
  const isDeadlineEditable =
    ["PLAN_REG_OPEN", "PLAN_DRAFT"].includes(initialValues?.statusId) ||
    !initialValues?.registrationDeadline;

  const handleClose = () => {
    onClose();
    setDeadlineError("");
  };

  const onSubmitForm = async (data) => {
    if (!deadline) {
      setDeadlineError("Hãy chọn thời gian đóng đăng ký phiên!");
      return;
    }
    if (isDeadlineEditable && dayjs(deadline).isBefore(dayjs())) {
      setDeadlineError(
        "Thời gian đóng đăng ký phải lớn hơn thời gian hiện tại!"
      );
      return;
    }

    setLoading(true);
    try {
      data.name = data.name?.trim();
      data.description = data.description?.trim();
      data.location = data.location?.trim();
      data.registrationDeadline = deadline;
      await onSubmit(data);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
      handleClose();
    }
  };

  return (
    <>
      <Dialog open={openDialog} maxWidth="sm" fullWidth>
        <Box
          id="datetimepicker"
          style={{ position: "fixed", top: 0, right: 0, width: 0, height: 0 }}
        />
        <DialogTitle>
          {initialValues ? "Cập nhật kế hoạch" : "Thêm Kế Hoạch Cuộc Họp"}
        </DialogTitle>
        <DialogContent>
          <IconButton
            size="small"
            onClick={handleClose}
            sx={{ position: "absolute", right: "1rem", top: "1rem" }}
          >
            <Icon icon="mdi:close" />
          </IconButton>
          <Grid container spacing={4}>
            <Grid item xs={12} sx={{ mt: 2 }}>
              <FormControl fullWidth>
                <Controller
                  name="name"
                  control={control}
                  defaultValue={initialValues?.name || ""}
                  rules={{ required: { value: true, message: "Required" } }}
                  render={({ value, onChange }) => (
                    <TextField
                      value={value}
                      onChange={onChange}
                      label="Tiêu đề"
                      variant="outlined"
                      fullWidth
                      autoFocus
                      error={Boolean(errors.name)}
                      spellCheck={false}
                    />
                  )}
                />
                {errors.name && (
                  <FormHelperText sx={{ color: "error.main" }}>
                    Tiêu đề không được để trống
                  </FormHelperText>
                )}
              </FormControl>
            </Grid>
            <Grid item xs={12}>
              <FormControl fullWidth>
                <Controller
                  name="description"
                  control={control}
                  defaultValue={initialValues?.description || ""}
                  render={({ value, onChange }) => (
                    <TextField
                      value={value}
                      onChange={onChange}
                      label="Mô tả"
                      multiline
                      fullWidth
                      spellCheck={false}
                      minRows={3}
                    />
                  )}
                />
              </FormControl>
            </Grid>
            <Grid item xs={12}>
              <FormControl fullWidth>
                <Controller
                  name="location"
                  control={control}
                  defaultValue={initialValues?.location || ""}
                  render={({ value, onChange }) => (
                    <TextField
                      value={value}
                      onChange={onChange}
                      label="Địa điểm"
                      variant="outlined"
                      fullWidth
                      spellCheck={false}
                    />
                  )}
                />
              </FormControl>
            </Grid>
            <Grid item xs={12}>
              <FormControl fullWidth>
                <DateTimePicker
                  disabled={!isDeadlineEditable}
                  disablePast={isDeadlineEditable}
                  label="Hạn đăng ký"
                  format="HH:mm dd/MM/yyyy"
                  value={deadline}
                  onChange={(value) => {
                    setDeadline(dayjs(value).millisecond(0).second(0).toDate());
                    setDeadlineError("");
                  }}
                  slotProps={{
                    textField: {
                      fullWidth: true,
                    },
                    popper: {
                      anchorEl: document.getElementById("datetimepicker"),
                    },
                  }}
                />
                {deadlineError && (
                  <FormHelperText sx={{ color: "error.main" }}>
                    {deadlineError}
                  </FormHelperText>
                )}
              </FormControl>
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button
            onClick={handleClose}
            variant="outlined"
            sx={{
              color: "grey.700",
              borderColor: "grey.300",
              textTransform: "none",
              "&:hover": {
                borderColor: "grey.200",
                backgroundColor: "grey.200",
              },
            }}
          >
            Hủy
          </Button>
          <Button
            onClick={handleSubmit(onSubmitForm)}
            color="primary"
            variant="contained"
            disabled={loading || isFormReadOnly}
            sx={{ textTransform: "none" }}
          >
            {loading
              ? initialValues
                ? "Đang lưu..."
                : "Đang tạo..."
              : initialValues
              ? "Lưu"
              : "Tạo"}
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
};

MeetingPlanDialog.propTypes = {
  initialValues: PropTypes.object,
  onSubmit: PropTypes.func.isRequired,
  openDialog: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
};

export default MeetingPlanDialog;
