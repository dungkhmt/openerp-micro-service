import PropTypes from "prop-types";
import { useState } from "react";
import {
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  IconButton,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
  Typography,
} from "@mui/material";
import { Icon } from "@iconify/react";
import ConfirmationDialog from "../dialog/ConfirmationDialog";
import { removeDiacritics } from "../../../utils/stringUtils.js.js";

const ManageTable = ({
  title,
  items,
  columns,
  idKey,
  fetchLoading,
  onCreate,
  onDelete,
  onEdit,
  addFields, // Array of field definitions for adding.
  editFields, // Array of field definitions for editing.
  canDelete = () => false,
  canEdit = () => false,
}) => {
  const [searchTerm, setSearchTerm] = useState("");
  const [openAddDialog, setOpenAddDialog] = useState(false);
  const [openDeleteDialog, setOpenDeleteDialog] = useState(false);
  const [openEditDialog, setOpenEditDialog] = useState(false);
  const [currentItem, setCurrentItem] = useState(null);

  // For Add: if addFields is provided, use an object; otherwise, use a simple string.
  const defaultKey = columns[0].key;
  const [newItemValue, setNewItemValue] = useState("");
  const [addForm, setAddForm] = useState({});

  // For Edit: manage an editForm
  const [editForm, setEditForm] = useState({});

  // Use all columns for search
  const filteredItems = items.filter((item) =>
    columns.some((column) =>
      removeDiacritics(String(item[column.key] || ""))
        .toLowerCase()
        .includes(removeDiacritics(searchTerm.toLowerCase()))
    )
  );

  // ----------------- ADD HANDLERS -----------------
  const resetAddForm = () => {
    setNewItemValue("");
    setAddForm({});
  };

  const handleAdd = async () => {
    // if addFields defined, validate first field in addFields; otherwise, validate simple input.
    if (addFields && addFields.length > 0) {
      const keyToCheck = addFields[0].key;
      if (!addForm[keyToCheck]?.trim()) {
        alert("Vui lòng nhập giá trị hợp lệ!");
        return;
      }
      await onCreate(addForm);
    } else {
      if (!newItemValue.trim()) {
        alert("Vui lòng nhập tên trước khi thêm!");
        return;
      }
      await onCreate(newItemValue);
    }
    setOpenAddDialog(false);
    resetAddForm();
  };

  const handleAddFormChange = (fieldKey, value) => {
    setAddForm((prev) => ({ ...prev, [fieldKey]: value }));
  };

  // ----------------- DELETE HANDLERS -----------------
  const handleDelete = (item) => {
    setCurrentItem(item);
    setOpenDeleteDialog(true);
  };

  const onConfirmDelete = async () => {
    if (currentItem) {
      await onDelete(currentItem[idKey]);
      setOpenDeleteDialog(false);
      setCurrentItem(null);
    }
  };

  // ----------------- EDIT HANDLERS -----------------
  const handleEdit = (item) => {
    setCurrentItem(item);
    if (editFields && editFields.length > 0) {
      const initialForm = {};
      editFields.forEach((field) => {
        initialForm[field.key] = item[field.key] || "";
      });
      setEditForm(initialForm);
    } else {
      setEditForm({ [defaultKey]: item[defaultKey] });
    }
    setOpenEditDialog(true);
  };

  const handleEditFormChange = (fieldKey, value) => {
    setEditForm((prev) => ({ ...prev, [fieldKey]: value }));
  };

  const onConfirmEdit = async () => {
    if (currentItem) {
      const keyToCheck =
        editFields && editFields.length > 0 ? editFields[0].key : defaultKey;
      if (!editForm[keyToCheck]?.trim()) {
        alert("Vui lòng nhập giá trị hợp lệ!");
        return;
      }
      await onEdit(currentItem[idKey], editForm);
      setOpenEditDialog(false);
      setCurrentItem(null);
    }
  };

  return (
    <>
      <Box
        sx={{
          position: "sticky",
          top: "5px",
          backgroundColor: "background.default",
          zIndex: 10,
          m: -2,
          p: 2
        }}
      >
        <Box mb={3}>
          <Typography variant="h6">{title}</Typography>
        </Box>

        <Box
          mb={3}
          display="flex"
          alignItems="center"
          justifyContent="space-between"
        >
          <Box width="40%">
            <TextField
              fullWidth
              placeholder="Tìm kiếm..."
              variant="outlined"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              InputProps={{
                startAdornment: <Icon fontSize={18} icon="ri:search-line" />,
                sx: { height: 40, gap: 2 },
                endAdornment: searchTerm && (
                  <IconButton
                    onClick={() => setSearchTerm("")}
                    sx={{ padding: 0, marginRight: "-4px" }}
                  >
                    <Icon icon="mdi:close" fontSize={20} />
                  </IconButton>
                ),
              }}
            />
          </Box>

          <Button
            variant="contained"
            onClick={() => setOpenAddDialog(true)}
            sx={{ ml: 2, whiteSpace: "nowrap", textTransform: "none" }}
          >
            <Icon icon="mdi:plus" fontSize={20} /> Thêm mới
          </Button>
        </Box>
      </Box>

      <Box sx={{mt: 5}}>
        <TableContainer component={Paper}>
          <Table sx={{ minWidth: 650 }} size="small" aria-label="manage table">
            <TableHead>
              <TableRow>
                {columns.map((col) => (
                  <TableCell key={col.key}>{col.label}</TableCell>
                ))}
                {onEdit && <TableCell align="center">Sửa</TableCell>}
                {onDelete && <TableCell align="center">Xóa</TableCell>}
              </TableRow>
            </TableHead>
            <TableBody>
              {fetchLoading ? (
                <TableRow>
                  <TableCell colSpan={columns.length + 2} align="center">
                    Đang tải dữ liệu...
                  </TableCell>
                </TableRow>
              ) : filteredItems.length > 0 ? (
                filteredItems.map((row) => (
                  <TableRow key={row[idKey]}>
                    {columns.map((col) => (
                      <TableCell key={col.key}>
                        <Typography
                          variant="body2"
                          sx={{
                            color: col.color || "#333333",
                            whiteSpace: "pre-wrap",
                            wordBreak: "break-word",
                            textTransform: col.textTransform || "none",
                          }}
                        >
                          {row[col.key]}
                        </Typography>
                      </TableCell>
                    ))}
                    {onEdit && (
                      <TableCell align="center">
                        {canEdit(row) && (
                          <IconButton
                            color="primary"
                            onClick={() => handleEdit(row)}
                          >
                            <Icon icon="mdi:pencil-outline" fontSize={20} />
                          </IconButton>
                        )}
                      </TableCell>
                    )}
                    {onDelete && (
                      <TableCell align="center">
                        {canDelete(row) ? (
                          <IconButton
                            color="error"
                            onClick={() => handleDelete(row)}
                          >
                            <Icon
                              icon="material-symbols:delete-outline-rounded"
                              fontSize={20}
                            />
                          </IconButton>
                        ) : (
                          <IconButton color="disabled" disabled>
                            <Icon
                              icon="material-symbols:delete-outline-rounded"
                              fontSize={20}
                            />
                          </IconButton>
                        )}
                      </TableCell>
                    )}
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell colSpan={columns.length + 2} align="center">
                    Không tìm thấy dữ liệu.
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </TableContainer>
      </Box>

      {/* Add Dialog */}
      <Dialog
        open={openAddDialog}
        onClose={() => {
          setOpenAddDialog(false);
          setAddForm({});
        }}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle>Thêm mới</DialogTitle>
        <DialogContent>
          {addFields && addFields.length > 0 ? (
            addFields.map((field) => (
              <Box key={field.key} mt={2}>
                <TextField
                  fullWidth
                  label={field.label}
                  variant="outlined"
                  value={addForm[field.key] || ""}
                  onChange={(e) =>
                    handleAddFormChange(field.key, e.target.value)
                  }
                  required={field.required || false}
                  autoFocus={field.autoFocus || false}
                  multiline={field.multiline || false}
                  rows={field.rows || 1}
                />
              </Box>
            ))
          ) : (
            // Fallback: single input using the default field
            <TextField
              fullWidth
              label="Tên"
              variant="outlined"
              value={newItemValue}
              onChange={(e) => setNewItemValue(e.target.value)}
              required
              autoFocus
            />
          )}
        </DialogContent>
        <DialogActions>
          <Button
            onClick={() => {
              setOpenAddDialog(false);
              setAddForm({});
            }}
            variant="outlined"
          >
            Hủy
          </Button>
          <Button
            onClick={handleAdd}
            variant="contained"
            disabled={
              addFields && addFields.length > 0
                ? addFields.some(
                    (field) => field.required && !addForm[field.key]?.trim()
                  )
                : !newItemValue.trim()
            }
          >
            Thêm
          </Button>
        </DialogActions>
      </Dialog>

      {/* Edit Dialog */}
      {currentItem && (
        <Dialog
          open={openEditDialog}
          onClose={() => setOpenEditDialog(false)}
          maxWidth="sm"
          fullWidth
        >
          <DialogTitle>Chỉnh sửa</DialogTitle>
          <DialogContent>
            {editFields && editFields.length > 0 ? (
              editFields.map((field) => (
                <Box key={field.key} mt={2}>
                  <TextField
                    fullWidth
                    label={field.label}
                    variant="outlined"
                    value={editForm[field.key] || ""}
                    onChange={(e) =>
                      handleEditFormChange(field.key, e.target.value)
                    }
                    autoFocus={field.autoFocus || false}
                    multiline={field.multiline || false}
                    rows={field.rows || 1}
                    InputProps={{
                      readOnly: field.readOnly || false,
                    }}
                  />
                </Box>
              ))
            ) : (
              <TextField
                fullWidth
                label="Tên"
                variant="outlined"
                value={editForm[defaultKey] || ""}
                onChange={(e) =>
                  handleEditFormChange(defaultKey, e.target.value)
                }
                required
                autoFocus
              />
            )}
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setOpenEditDialog(false)} variant="outlined">
              Hủy
            </Button>
            <Button
              onClick={onConfirmEdit}
              color="primary"
              variant="contained"
              disabled={
                editFields && editFields.length > 0
                  ? editFields.some(
                      (field) => field.required && !editForm[field.key]?.trim()
                    )
                  : !editForm[defaultKey]?.trim()
              }
            >
              Cập nhật
            </Button>
          </DialogActions>
        </Dialog>
      )}

      {/* Delete Confirmation Dialog */}
      {currentItem && (
        <ConfirmationDialog
          open={openDeleteDialog}
          onClose={() => setOpenDeleteDialog(false)}
          onConfirm={onConfirmDelete}
          title="Xác nhận xóa"
          content={
            <span>
              Bạn có chắc chắn muốn xóa <b>{currentItem[defaultKey]}</b> không?
              Hành động này không thể hoàn tác.
            </span>
          }
          confirmText="Xóa"
          cancelText="Hủy"
        />
      )}
    </>
  );
};

ManageTable.propTypes = {
  title: PropTypes.string.isRequired,
  items: PropTypes.array.isRequired,
  columns: PropTypes.array.isRequired,
  idKey: PropTypes.string.isRequired,
  fetchLoading: PropTypes.bool,
  onCreate: PropTypes.func.isRequired,
  onDelete: PropTypes.func.isRequired,
  onEdit: PropTypes.func,
  addFields: PropTypes.array,
  editFields: PropTypes.array,
  canDelete: PropTypes.func,
  canEdit: PropTypes.func,
};

export default ManageTable;
