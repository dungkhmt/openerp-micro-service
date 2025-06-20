import PropTypes from "prop-types";
import {
  Grid,
  FormControl,
  TextField,
  FormHelperText,
  Button,
  DialogActions,
  DialogTitle,
  Dialog,
  DialogContent,
  IconButton,
  Icon,
} from "@mui/material";
import { Controller, useForm } from "react-hook-form";
import { useState } from "react";

const GroupDialog = ({ initialValues, openDialog, onClose, onSubmit }) => {
  const {
    handleSubmit,
    control,
    formState: { errors },
  } = useForm({
    defaultValues: {
      name: initialValues?.name || "",
      description: initialValues?.description || "",
    },
  });
  const [loading, setLoading] = useState(false);

  const handleClose = () => {
    onClose();
  };

  const onSubmitForm = async (data) => {
    setLoading(true);
    try {
      data.name = data.name?.trim();
      data.description = data.description?.trim();
      await onSubmit(data);
    } catch (error) {
      console.error(error);
    } finally {
      handleClose();
      setLoading(false);
    }
  };

  return (
    <Dialog open={openDialog} maxWidth="sm" fullWidth>
      <DialogTitle>
        {initialValues ? "Cập nhật Nhóm" : "Tạo Nhóm Mới"}
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
                    label="Tên Nhóm"
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
                  Tên nhóm không được để trống
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
          disabled={loading}
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
  );
};

GroupDialog.propTypes = {
  initialValues: PropTypes.object,
  openDialog: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
  onSubmit: PropTypes.func.isRequired,
};

export default GroupDialog;
