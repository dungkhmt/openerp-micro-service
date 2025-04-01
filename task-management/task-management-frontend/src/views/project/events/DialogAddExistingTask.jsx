import { Icon } from "@iconify/react";
import {
  Box,
  Button,
  Checkbox,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControl,
  IconButton,
  InputAdornment,
  Link,
  ListItemText,
  MenuItem,
  MenuList,
  TextField,
  Tooltip,
  Typography,
} from "@mui/material";
import PropTypes from "prop-types";
import { useCallback, useEffect, useState } from "react";
import toast from "react-hot-toast";
import { LoadingButton } from "../../../components/mui/button/LoadingButton";
import { useDebounce } from "../../../hooks/useDebounce";
import { TaskService } from "../../../services/api/task.service";
import { useParams } from "react-router";

function renderUserItems(tasks, handleRemoveTask) {
  return tasks.map((task) => {
    const { name } = task;
    return (
      <Box
        sx={{
          display: "flex",
          alignItems: "center",
          border: "1px solid",
          borderColor: (theme) => theme.palette.divider,
          backgroundColor: "#f3f3f3",
          pl: 3,
          pt: 1,
          pb: 1,
          borderRadius: 3,
          gap: 2, // Add gap for spacing between elements
        }}
        key={task.id}
      >
        <Box
          sx={{
            display: "flex",
            alignItems: "flex-start",
            flexDirection: "column",
            flexGrow: 1, // Allow the inner box to grow and take up available space
          }}
        >
          <Tooltip title={name} arrow>
            <Typography
              sx={{
                fontWeight: 600,
                fontSize: "1rem",
                whiteSpace: "nowrap",
                textTransform: "capitalize",
                color: (theme) => theme.palette.text.secondary,
                "&:hover": {
                  color: (theme) => theme.palette.text.primary,
                  cursor: "pointer",
                },
                maxWidth: "35vh",
                overflow: "hidden",
                textOverflow: "ellipsis",
              }}
            >
              {name}
            </Typography>
          </Tooltip>
        </Box>
        <IconButton onClick={() => handleRemoveTask(task)}>
          <Icon icon="carbon:close-filled" fontSize={20} />
        </IconButton>
      </Box>
    );
  });
}

const DialogAddExistingTask = ({ open, setOpen, isUpdate, setIsUpdate }) => {
  const { id, eid } = useParams();
  const [addLoading, setAddLoading] = useState(false);
  const [fetchLoading, setFetchLoading] = useState(false);
  const [tasks, setTasks] = useState([]);
  const [searchedTasks, setSearchedTasks] = useState([]);
  const [selectedTasks, setSelectedTasks] = useState([]);
  const [search, setSearch] = useState("");
  const searchDebounce = useDebounce(search, 500);

  const handleRemoveTask = (task) => {
    setSelectedTasks((prev) => prev.filter((t) => t.id !== task.id));
  };

  const getTasks = useCallback(async () => {
    try {
      setFetchLoading(true);
      const tasksWithoutEvent = await TaskService.getTasksWithoutEvent(id);
      setTasks(tasksWithoutEvent);
      setSearchedTasks(tasksWithoutEvent);
    } catch (e) {
      console.log(e);
      toast.error("Lỗi khi lấy danh sách nhiệm vụ");
    } finally {
      setFetchLoading(false);
    }
  }, [id]);

  const handleAddTasks = async () => {
    if (!selectedTasks) return;
    try {
      setAddLoading(true);
      await TaskService.addExistingTasksToEvent(
        eid,
        selectedTasks.map((task) => task.id)
      );
      toast.success("Thêm nhiệm vụ thành công");
      setIsUpdate(!isUpdate);
    } catch (e) {
      console.log(e);
      toast.error("Lỗi khi thêm nhiệm vụ");
    } finally {
      setAddLoading(false);
      handleClose();
    }
  };

  const handleClose = () => {
    setOpen(false);
    setSelectedTasks([]);
    setSearch("");
  };

  useEffect(() => {
    getTasks();
  }, [getTasks]);

  useEffect(() => {
    setSearchedTasks(
      tasks.filter((task) =>
        task.name.toLowerCase().includes(searchDebounce.toLowerCase())
      )
    );
  }, [tasks, searchDebounce]);

  return (
    <Dialog
      open={open}
      scroll="paper"
      onClose={() => setOpen(false)}
      data-color-mode="light"
      maxWidth="md"
      fullWidth
    >
      <DialogTitle>Thêm nhiệm vụ hiện có</DialogTitle>
      <DialogContent>
        <Box sx={{ display: "flex", flexDirection: "column", gap: 2 }}>
          <IconButton
            size="small"
            onClick={handleClose}
            sx={{ position: "absolute", right: "1.2rem", top: "1rem" }}
          >
            <Icon icon="mdi:close" />
          </IconButton>
          <FormControl fullWidth>
            <TextField
              fullWidth
              autoFocus
              value={search}
              InputProps={{
                placeholder: "Tìm kiếm nhiệm vụ",
                startAdornment: (
                  <InputAdornment position="start">
                    <Icon icon="ri:search-line" fontSize={20} />
                  </InputAdornment>
                ),
              }}
              onChange={(e) => setSearch(e.target.value)}
              sx={{
                "& .MuiInputBase-root": {
                  height: "40px",
                  fontSize: "14px",
                },
              }}
            />
          </FormControl>
          <Box
            onClick={(e) => e.preventDefault()}
            sx={{
              display: "flex",
              justifyContent: "space-between",
              gap: 4,
              alignItems: "center",
            }}
          >
            {selectedTasks.length > 0 ? (
              <Box
                sx={{
                  display: "flex",
                  gap: 2,
                  overflowY: "auto",
                  overflowX: "hidden",
                  flexWrap: "wrap",
                }}
              >
                {renderUserItems(selectedTasks, handleRemoveTask)}
              </Box>
            ) : (
              <Typography
                sx={{
                  fontWeight: 600,
                  fontSize: "1rem",
                  fontStyle: "italic",
                  color: (theme) => theme.palette.text.secondary,
                  p: 2,
                }}
              >
                Chọn nhiệm vụ
              </Typography>
            )}
          </Box>
          <Box>
            {fetchLoading ? (
              <Box
                sx={{
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  minHeight: "10rem",
                }}
              >
                <CircularProgress variant="indeterminate" size={26} />
              </Box>
            ) : (
              <MenuList
                id="list-user-select"
                sx={{
                  minHeight: "10rem",
                  maxHeight: "15rem",
                  overflowY: "auto",
                  border: (theme) => `1px solid ${theme.palette.divider}`,
                  padding: (theme) => theme.spacing(1, 2),
                  borderRadius: "4px",
                }}
              >
                {fetchLoading ? (
                  <Box
                    sx={{
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                      height: "100%",
                    }}
                  >
                    <CircularProgress variant="indeterminate" size={26} />
                  </Box>
                ) : (
                  <>
                    {searchedTasks.map((task) => (
                      <MenuItem
                        key={task.id}
                        onClick={() => {
                          setSelectedTasks((prevSelectedTasks) => {
                            if (prevSelectedTasks.includes(task)) {
                              return prevSelectedTasks.filter(
                                (selectedTask) => selectedTask.id !== task.id
                              );
                            } else {
                              return [...prevSelectedTasks, task];
                            }
                          });
                        }}
                        value={task}
                      >
                        <Checkbox checked={selectedTasks.includes(task)} />
                        <ListItemText>
                          <Typography
                            sx={{
                              fontWeight: 600,
                              fontSize: "1rem",
                              textTransform: "capitalize",
                              color: (theme) => theme.palette.text.primary,
                              overflow: "hidden",
                              textOverflow: "ellipsis",
                              whiteSpace: "nowrap",
                            }}
                          >
                            {task.name}
                          </Typography>
                        </ListItemText>
                        <Link
                          href={`/project/${id}/task/${task.id}`}
                          underline="none"
                          target="_blank"
                          rel="noopener noreferrer"
                          onClick={(e) => e.stopPropagation()}
                          sx={{
                            color: (theme) => theme.palette.text.secondary,
                            mr: 3,
                            pl: 5,
                            "&:hover": {
                              "& svg": {
                                color: (theme) => theme.palette.text.primary,
                              },
                            },
                          }}
                        >
                          <Icon icon="fluent:open-16-filled" fontSize={20} />
                        </Link>
                      </MenuItem>
                    ))}

                    {searchedTasks.length <= 0 && (
                      <Typography
                        variant="subtitle2"
                        sx={{ fontStyle: "italic" }}
                      >
                        Không tìm thấy nhiệm vụ
                      </Typography>
                    )}
                  </>
                )}
              </MenuList>
            )}
          </Box>
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose} color="primary">
          Hủy
        </Button>
        <LoadingButton
          loading={addLoading}
          variant="contained"
          disabled={selectedTasks.length === 0}
          onClick={handleAddTasks}
        >
          Thêm
        </LoadingButton>
      </DialogActions>
    </Dialog>
  );
};

DialogAddExistingTask.propTypes = {
  open: PropTypes.bool.isRequired,
  setOpen: PropTypes.func.isRequired,
  isUpdate: PropTypes.bool,
  setIsUpdate: PropTypes.func,
};

export { DialogAddExistingTask };
