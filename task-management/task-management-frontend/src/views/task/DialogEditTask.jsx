import { Icon } from "@iconify/react";
import {
  Box,
  Button,
  Collapse,
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
import dayjs from "dayjs";
import PropTypes from "prop-types";
import { forwardRef, useState } from "react";
import { Controller, useForm } from "react-hook-form";
import toast from "react-hot-toast";
import { UserAvatar } from "../../components/common/avatar/UserAvatar";
import { CustomMDEditor } from "../../components/editor/md-editor/CustomMDEditor";
import { FileUploader } from "../../components/file-uploader";
import { LoadingButton } from "../../components/mui/button/LoadingButton";
import CustomChip from "../../components/mui/chip";
import { useProjectContext } from "../../hooks/useProjectContext";
import { useTaskContext } from "../../hooks/useTaskContext";
import { FileService } from "../../services/api/file.service";
import { TaskService } from "../../services/api/task.service";
import {
  getCategoryColor,
  getPriorityColor,
  getStatusColor,
} from "../../utils/color.util";

const Transition = forwardRef(function Transition(props, ref) {
  return <Fade ref={ref} {...props} />;
});

const DialogEditTask = ({ open, setOpen }) => {
  const { task, isUpdate, setIsUpdate } = useTaskContext();
  const { statuses, priorities, categories, members } = useProjectContext();
  const [toggleEditDesc, setToggleEditDesc] = useState(false);
  const [files, setFiles] = useState(null);
  const [updateLoading, setUpdateLoading] = useState(false);

  const { register, handleSubmit, errors, setValue, control } = useForm();

  const onUpdated = async (data) => {
    let attachmentPaths = null;
    try {
      setUpdateLoading(true);
      if (files && files[0]) {
        const uploadRes = await FileService.uploadFile(files[0]);
        attachmentPaths = uploadRes?.id
          ? `${files[0].name},${uploadRes.id}`
          : null;
      }

      await TaskService.updateTask(task.id, {
        ...data,
        attachmentPaths,
      });
      toast.success("Cập nhật nhiệm vụ thành công");
      setIsUpdate(!isUpdate);
    } catch (e) {
      console.error(e);
      toast.error("Cập nhật nhiệm vụ thất bại. Vui lòng thử lại sau.");
    } finally {
      setOpen(false);
      setUpdateLoading(false);
    }
  };

  const membersNotAssign = members.filter(
    ({ member }) => !task.assignees?.some((a) => a.id === member?.id)
  );

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
            Chỉnh sửa nhiệm vụ
          </Typography>
        </Box>

        <Grid container spacing={6}>
          <Grid item sm={12} xs={12}>
            <FormControl fullWidth>
              <TextField
                name="name"
                defaultValue={task.name}
                inputRef={register({ required: true })}
                label="Tên"
                placeholder="Nhập tên nhiệm vụ"
                error={!!errors.name}
                helperText={errors.name && "Tên không được để trống"}
              />
            </FormControl>
          </Grid>
          <Grid item sm={12} xs={12}>
            <Typography>
              Mô tả
              <Typography
                component="span"
                sx={{
                  color: "primary.light",
                  cursor: "pointer",
                  "&:hover": {
                    color: "primary.dark",
                  },
                }}
                onClick={() => setToggleEditDesc(!toggleEditDesc)}
              >
                {" "}
                {` (sửa)`}
              </Typography>
            </Typography>
            <Collapse in={toggleEditDesc}>
              <Controller
                name="description"
                control={control}
                defaultValue={task.description}
                render={(field) => (
                  <CustomMDEditor
                    {...field}
                    setValue={(value) => setValue("description", value)}
                  />
                )}
              />
            </Collapse>
          </Grid>
          <Grid item sm={4} xs={12}>
            <FormControl fullWidth>
              <InputLabel id="role-category">Danh mục</InputLabel>
              <Controller
                name="categoryId"
                control={control}
                defaultValue={task.categoryId}
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
              <InputLabel id="status-select">Trạng thái</InputLabel>
              <Controller
                name="statusId"
                control={control}
                defaultValue={task.statusId}
                rules={{ required: true }}
                as={
                  <Select
                    fullWidth
                    id="select-status"
                    label="Trạng thái"
                    labelId="status-select"
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
                }
              />
              {errors.statusId && (
                <FormHelperText>Trạng thái là trường bắt buộc</FormHelperText>
              )}
            </FormControl>
          </Grid>
          <Grid item sm={4} xs={12}>
            <FormControl fullWidth>
              <InputLabel id="priority-select">Ưu tiên</InputLabel>
              <Controller
                name="priorityId"
                control={control}
                defaultValue={task.priorityId}
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
          <Grid item sm={3} xs={12}>
            <FormControl fullWidth>
              <Controller
                name="fromDate"
                control={control}
                defaultValue={
                  task.fromDate ? dayjs(task.fromDate).toDate() : null
                }
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
          <Grid item sm={3} xs={12}>
            <FormControl fullWidth>
              <Controller
                name="dueDate"
                control={control}
                defaultValue={
                  task.dueDate ? dayjs(task.dueDate).toDate() : null
                }
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
          <Grid item sm={3} xs={12}>
            <FormControl fullWidth>
              <TextField
                name="estimatedTime"
                defaultValue={task.estimatedTime}
                inputRef={register()}
                label="Thời gian ước tính"
                placeholder="Thời gian ước tính theo giờ"
                type="number"
              />
            </FormControl>
          </Grid>
          <Grid item sm={3} xs={12}>
            <FormControl fullWidth>
              <InputLabel id="progress">Tiến độ</InputLabel>
              <Controller
                name="progress"
                control={control}
                defaultValue={task.progress}
                as={
                  <Select
                    id="select-progress"
                    label="Tiến độ"
                    labelId="progress"
                    inputProps={{ placeholder: "Tiến độ" }}
                  >
                    <MenuItem value={0}>0%</MenuItem>
                    <MenuItem value={10}>10%</MenuItem>
                    <MenuItem value={20}>20%</MenuItem>
                    <MenuItem value={30}>30%</MenuItem>
                    <MenuItem value={40}>40%</MenuItem>
                    <MenuItem value={50}>50%</MenuItem>
                    <MenuItem value={60}>60%</MenuItem>
                    <MenuItem value={70}>70%</MenuItem>
                    <MenuItem value={80}>80%</MenuItem>
                    <MenuItem value={90}>90%</MenuItem>
                    <MenuItem value={100}>100%</MenuItem>
                  </Select>
                }
              />
            </FormControl>
          </Grid>
          <Grid item sm={8} xs={12}>
            <FormControl fullWidth>
              <InputLabel id="asignee-select">Phân công cho</InputLabel>
              <Controller
                name="assigneeId"
                control={control}
                defaultValue={task.assignee?.id ?? ""}
                as={
                  <Select
                    id="select-assignee"
                    label="Phân công cho"
                    labelId="assignee-select"
                    inputProps={{ placeholder: "Phân công cho" }}
                  >
                    <MenuItem value="">Không chọn</MenuItem>
                    {membersNotAssign.map(({ member }) => (
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
          <Grid item sm={12} xs={12}>
            <FormControl fullWidth>
              <Typography variant="subtitle2">Ghi chú</Typography>
              <Controller
                name="note"
                control={control}
                defaultValue=""
                render={(field) => (
                  <CustomMDEditor
                    {...field}
                    setValue={(value) => setValue("note", value)}
                  />
                )}
              />
            </FormControl>
          </Grid>
          <Grid item sm={12} xs={12}>
            <FormControl fullWidth>
              <Typography variant="subtitle2">Tệp đính kèm</Typography>
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
        }}
      >
        <LoadingButton
          variant="contained"
          sx={{ mr: 1, position: "relative" }}
          onClick={handleSubmit(onUpdated)}
          loading={updateLoading}
        >
          Cập nhật
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

DialogEditTask.propTypes = {
  open: PropTypes.bool.isRequired,
  setOpen: PropTypes.func.isRequired,
};

export { DialogEditTask };
