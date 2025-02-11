import { useState, useEffect } from "react";
import PropTypes from "prop-types";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Box,
  Button,
  Grid,
  TextField,
  Typography,
} from "@mui/material";
import { ProjectService } from "../../../../services/api/project.service";
import { useDispatch } from "react-redux";
import { updateProject } from "../../../../store/project";
import toast from "react-hot-toast";

const DialogEditInfo = ({ isEditing, setIsEditing, project }) => {
  const [editedProject, setEditedProject] = useState({ ...project });
  const [warning, setWarning] = useState("");
  const [existingCodes, setExistingCodes] = useState([]);

  const dispatch = useDispatch();

  const fetchProjectCodes = async () => {
    try {
      const codes = await ProjectService.getProjectsCode();
      setExistingCodes(codes);
    } catch (error) {
      console.error("Failed to fetch project codes", error);
    }
  };

  const handleSaveClick = async () => {
    try {
      const updatedFields = Object.keys(editedProject).filter(
        (key) => editedProject[key] !== project[key]
      );
      if (updatedFields.length === 0) {
        setIsEditing(false);
        toast.success("Không có thay đổi cần lưu.");
        return;
      }
      const updatedData = {};
      updatedFields.forEach((key) => {
        updatedData[key] = editedProject[key];
      });
      await dispatch(
        updateProject({
          id: project.id,
          data: updatedData,
        })
      );
      fetchProjectCodes();
      toast.success("Thông tin được cập nhật thành công.");
    } catch (error) {
      console.error(error);
      toast.error("Có lỗi xảy ra khi cập nhật thông tin.");
      setEditedProject({ ...project });
    } finally {
      setIsEditing(false);
    }
  };

  const handleCancelClick = () => {
    setIsEditing(false);
    setEditedProject({ ...project });
    setWarning("");
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setEditedProject((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  useEffect(() => {
    fetchProjectCodes();
  }, []);

  const validateAndSave = () => {
    if (!editedProject.code || !editedProject.name) {
      setWarning("Mã dự án và Tên dự án không được để trống.");
      return;
    }

    const isCodeTaken = existingCodes.some(
      (code) => code !== project.code && code === editedProject.code
    );
    if (isCodeTaken) {
      setWarning("Mã dự án đã tồn tại.");
      return;
    }

    setWarning("");
    handleSaveClick();
  };

  return (
    <Dialog
      open={isEditing}
      onClose={handleCancelClick}
      maxWidth="sm"
      fullWidth
    >
      <DialogTitle>Chỉnh sửa thông tin dự án</DialogTitle>
      <DialogContent>
        <Box sx={{ display: "flex", flexDirection: "column", gap: 7, pt: 5 }}>
          <Grid item>
            <TextField
              label="Mã dự án"
              name="code"
              value={editedProject.code}
              onChange={handleInputChange}
              fullWidth
              InputProps={{ style: { fontSize: "18px" } }}
              InputLabelProps={{ style: { fontSize: "16px" } }}
              error={warning === "Mã dự án đã tồn tại."}
              helperText={warning === "Mã dự án đã tồn tại." ? warning : ""}
            />
          </Grid>
          <TextField
            label="Tên dự án"
            name="name"
            value={editedProject.name}
            onChange={handleInputChange}
            fullWidth
            InputProps={{ style: { fontSize: "18px" } }}
            InputLabelProps={{ style: { fontSize: "16px" } }}
          />
          {warning && warning !== "Mã dự án đã tồn tại." && (
            <Typography variant="body2" color="error">
              {warning}
            </Typography>
          )}
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={validateAndSave} color="primary">
          Save
        </Button>
        <Button onClick={handleCancelClick} color="secondary">
          Cancel
        </Button>
      </DialogActions>
    </Dialog>
  );
};

DialogEditInfo.propTypes = {
  isEditing: PropTypes.bool.isRequired,
  setIsEditing: PropTypes.func.isRequired,
  project: PropTypes.object.isRequired,
};

export { DialogEditInfo };
