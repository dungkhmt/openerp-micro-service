import { Icon } from "@iconify/react";
import {
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  Fade,
  FormControl,
  FormHelperText,
  Grid,
  IconButton,
  InputLabel,
  MenuItem,
  Select,
  TextField,
  Typography,
} from "@mui/material";
import { DateTimePicker } from "@mui/x-date-pickers";
import PropTypes from "prop-types";
import { forwardRef, useState } from "react";
import { Controller, useForm } from "react-hook-form";
import toast from "react-hot-toast";
import { useNavigate } from "react-router";
import { UserAvatar } from "../../../components/common/avatar/UserAvatar";
import { CustomMDEditor } from "../../../components/editor/md-editor/CustomMDEditor";
import { FileUploader } from "../../../components/file-uploader";
import { LoadingButton } from "../../../components/mui/button/LoadingButton";
import CustomChip from "../../../components/mui/chip";
import { useProjectContext } from "../../../hooks/useProjectContext";
import { useTaskContext } from "../../../hooks/useTaskContext";
import { FileService } from "../../../services/api/file.service";
import { TaskService } from "../../../services/api/task.service";
import {
  getCategoryColor,
  getPriorityColor,
  getStatusColor,
} from "../../../utils/color.util";

const Transition = forwardRef(function Transition(props, ref) {
  return <Fade ref={ref} {...props} />;
});

const DialogAddSubTask = ({ open, setOpen }) => {
  const navigate = useNavigate();
  const { statuses, priorities, categories, members, project } =
    useProjectContext();
  const { task } = useTaskContext();
  const [files, setFiles] = useState([]);
  const [createLoading, setCreateLoading] = useState(false);

  const { register, handleSubmit, errors, setValue, control } = useForm();

  const onCreate = async (data) => {
    let attachmentPaths = null;
    try {
      setCreateLoading(true);
      if (files) {
        const uploadRes = await FileService.uploadFile(files[0]);
        attachmentPaths = uploadRes?.id
          ? `${files[0].name},${uploadRes.id}`
          : null;
      }

      const res = await TaskService.createTask({
        ...data,
        attachmentPaths,
        projectId: project.id,
        fromDate: data.fromDate.toISOString(),
        parentId: task.id,
      });
      toast.success("Thêm nhiệm vụ con thành công");
      navigate(`/project/${project.id}/task/${res.id}`);
    } catch (e) {
      console.error(e);
      toast.error("Thêm nhiệm vụ con thất bại. Vui lòng thử lại sau.");
    } finally {
      setOpen(false);
      setCreateLoading(false);
    }
  };

  return (
    <Dialog
      fullWidth
      open={open}
      maxWidth="md"
      scroll="paper"
      onClose={() => setOpen(false)}
      TransitionComponent={Transition}
      data-color-mode="light"
    >
      <DialogContent
        sx={{
          position: "relative",
        }}
      >
        <IconButton
          size="small"
          onClick={() => setOpen(false)}
          sx={{ position: "absolute", right: "1rem", top: "1rem" }}
        >
          <Icon icon="mdi:close" />
        </IconButton>

        <Box sx={{ mb: 8, textAlign: "center" }}>
          <Typography variant="h5" sx={{ mb: 3 }}>
            Thêm mới nhiệm vụ con
          </Typography>
        </Box>

        <Grid container spacing={6}>
          <Grid item sm={12} xs={12}>
            <FormControl fullWidth>
              <TextField
                name="name"
                defaultValue=""
                inputRef={register({ required: true })}
                label="Tên"
                placeholder="Nhập tên nhiệm vụ"
                error={!!errors.name}
                helperText={errors.name && "Tên không được để trống"}
              />
            </FormControl>
          </Grid>
          <Grid item sm={12} xs={12}>
            <Typography>Mô tả</Typography>
            <Controller
              name="description"
              control={control}
              defaultValue=""
              render={(field) => (
                <CustomMDEditor
                  {...field}
                  setValue={(value) => setValue("description", value)}
                />
              )}
            />
          </Grid>
          <Grid item sm={4} xs={12}>
            <FormControl fullWidth>
              <InputLabel id="status-select">Trạng thái</InputLabel>
              <Select
                fullWidth
                id="select-status"
                label="Trạng thái"
                labelId="status-select"
                defaultValue="TASK_OPEN"
                disabled
                inputProps={{ placeholder: "Trạng thái" }}
              >
                {statuses.map(({ statusId, description }) => (
                  <MenuItem key={statusId} value={statusId}>
                    <CustomChip
                      size="small"
                      skin="light"
                      label={description}
                      color={getStatusColor(statusId)}
                    />
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>
          <Grid item sm={4} xs={12}>
            <FormControl fullWidth>
              <InputLabel id="role-category">Danh mục</InputLabel>
              <Controller
                name="categoryId"
                control={control}
                defaultValue={categories[0]?.categoryId ?? ""}
                rules={{ required: true }}
                as={
                  <Select
                    fullWidth
                    id="select-category"
                    label="Danh mục"
                    labelId="role-category"
                    inputProps={{ placeholder: "Danh mục" }}
                  >
                    {categories.map(({ categoryId, categoryName }) => (
                      <MenuItem key={categoryId} value={categoryId}>
                        <CustomChip
                          size="small"
                          skin="light"
                          label={categoryName}
                          color={getCategoryColor(categoryId)}
                        />
                      </MenuItem>
                    ))}
                  </Select>
                }
              />
              {errors.categoryId && (
                <FormHelperText>Danh mục là trường bắt buộc</FormHelperText>
              )}
            </FormControl>
          </Grid>

          <Grid item sm={4} xs={12}>
            <FormControl fullWidth>
              <InputLabel id="priority-select">Ưu tiên</InputLabel>
              <Controller
                name="priorityId"
                control={control}
                defaultValue={priorities[0]?.priorityId ?? ""}
                rules={{ required: true }}
                as={
                  <Select
                    fullWidth
                    id="select-priority"
                    label="Ưu tiên"
                    labelId="priority-select"
                    inputProps={{ placeholder: "Ưu tiên" }}
                  >
                    {priorities.map(({ priorityId, priorityName }) => (
                      <MenuItem key={priorityId} value={priorityId}>
                        <CustomChip
                          size="small"
                          skin="light"
                          label={priorityName}
                          color={getPriorityColor(priorityId)}
                        />
                      </MenuItem>
                    ))}
                  </Select>
                }
              />
              {errors.priorityId && (
                <FormHelperText>Ưu tiên là trường bắt buộc</FormHelperText>
              )}
            </FormControl>
          </Grid>
          <Grid item sm={4} xs={12}>
            <FormControl fullWidth>
              <Controller
                name="fromDate"
                control={control}
                defaultValue={new Date()}
                as={
                  <DateTimePicker
                    label="Ngày bắt đầu"
                    format="dd/MM/yyyy HH:mm"
                    renderInput={(params) => <TextField {...params} />}
                  />
                }
              />
            </FormControl>
          </Grid>
          <Grid item sm={4} xs={12}>
            <FormControl fullWidth>
              <Controller
                name="dueDate"
                control={control}
                defaultValue={null}
                as={
                  <DateTimePicker
                    label="Thời hạn"
                    format="dd/MM/yyyy HH:mm"
                    renderInput={(params) => <TextField {...params} />}
                  />
                }
              />
            </FormControl>
          </Grid>
          <Grid item sm={4} xs={12}>
            <FormControl fullWidth>
              <TextField
                name="estimatedTime"
                defaultValue={null}
                inputRef={register()}
                label="Thời gian ước tính (h)"
                placeholder="Thời gian ước tính theo giờ"
                type="number"
              />
            </FormControl>
          </Grid>
          <Grid item sm={8} xs={12}>
            <FormControl fullWidth>
              <InputLabel id="assignee-select">Phân công cho</InputLabel>
              <Controller
                name="assigneeId"
                control={control}
                defaultValue={null}
                as={
                  <Select
                    id="select-assignee"
                    label="Phân công cho"
                    labelId="assignee-select"
                    inputProps={{ placeholder: "Phân công cho" }}
                  >
                    <MenuItem value="">Không chọn</MenuItem>
                    {members.map(({ member }) => (
                      <MenuItem key={member.id} value={member.id}>
                        <Box
                          sx={{ display: "flex", gap: 2, alignItems: "center" }}
                        >
                          <UserAvatar user={member} />
                          <Typography variant="subtitle2">{`${
                            member.firstName ?? ""
                          } ${member.lastName ?? ""}`}</Typography>
                        </Box>
                      </MenuItem>
                    ))}
                  </Select>
                }
              />
            </FormControl>
          </Grid>
          <Grid item sm={4} xs={12}>
            <FormControl fullWidth>
              <TextField
                name="parentId"
                defaultValue={task.id}
                disabled
                inputRef={register()}
                label="Nhiệm vụ cha"
                placeholder="Nhiệm vụ cha"
              />
            </FormControl>
          </Grid>
          <Grid item sm={12} xs={12}>
            <FormControl fullWidth>
              <Typography variant="subtitle2">Tài liệu</Typography>
              <FileUploader files={files} setFiles={setFiles} />
            </FormControl>
          </Grid>
        </Grid>
      </DialogContent>

      <DialogActions
        sx={{
          justifyContent: "flex-end",
          px: (theme) => [
            `${theme.spacing(5)} !important`,
            `${theme.spacing(15)} !important`,
          ],
          pt: 1,
        }}
      >
        <LoadingButton
          variant="contained"
          sx={{ mr: 1, position: "relative" }}
          onClick={handleSubmit(onCreate)}
          loading={createLoading}
        >
          Tạo
        </LoadingButton>
        <Button
          variant="outlined"
          color="secondary"
          onClick={() => setOpen(false)}
        >
          Hủy
        </Button>
      </DialogActions>
    </Dialog>
  );
};

DialogAddSubTask.propTypes = {
  open: PropTypes.bool.isRequired,
  setOpen: PropTypes.func.isRequired,
};

export { DialogAddSubTask };
