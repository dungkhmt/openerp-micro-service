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
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router";
import { UserAvatar } from "../../../components/common/avatar/UserAvatar";
import { CustomMDEditor } from "../../../components/editor/md-editor/CustomMDEditor";
import { FileUploader } from "../../../components/file-uploader";
import { LoadingButton } from "../../../components/mui/button/LoadingButton";
import { TaskCategory } from "../../../components/task/category";
import { TaskPriority } from "../../../components/task/priority";
import { TaskStatus } from "../../../components/task/status";
import { FileService } from "../../../services/api/file.service";
import { TaskService } from "../../../services/api/task.service";
import { clearCache } from "../../../store/project/tasks";
import { SkillChip } from "../../../components/task/skill";
import ItemSelector from "../../../components/mui/dialog/ItemSelector";
import { fetchEvents } from "../../../store/project/events";
import { removeDiacritics } from "../../../utils/stringUtils.js";

const Transition = forwardRef(function Transition(props, ref) {
  return <Fade ref={ref} {...props} />;
});

const DialogAddTask = ({ open, setOpen, defaultEvent }) => {
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const { members, project } = useSelector((state) => state.project);
  const { events } = useSelector((state) => state.events);
  const { category, priority, status, skill } = useSelector((state) => state);
  const [files, setFiles] = useState([]);
  const [createLoading, setCreateLoading] = useState(false);
  const { register, handleSubmit, errors, setValue, control } = useForm();

  const [selectedSkills, setSelectedSkills] = useState([]);
  const [filteredSkills, setFilteredSkills] = useState(skill.skills);

  const [selectedEvents, setSelectedEvents] = useState(
    defaultEvent ? [defaultEvent] : []
  );
  const [filteredEvents, setFilteredEvents] = useState(events);

  const [selectedAssignees, setSelectedAssignees] = useState([]);
  const [filteredAssignees, setFilteredAssignees] = useState(members);

  const handleSkillSearch = (search) => {
    setFilteredSkills(
      skill.skills.filter((item) =>
        removeDiacritics(item.name)
          .toLowerCase()
          .includes(removeDiacritics(search).toLowerCase())
      )
    );
  };

  const handleSkillChange = (skill) => {
    setSelectedSkills((prevSelectedSkills) =>
      prevSelectedSkills.includes(skill)
        ? prevSelectedSkills.filter(
            (selected) => selected.skillId !== skill.skillId
          )
        : [...prevSelectedSkills, skill]
    );
  };

  const handleEventSearch = (search) => {
    setFilteredEvents(
      events.filter((item) =>
        removeDiacritics(item.name)
          .toLowerCase()
          .includes(removeDiacritics(search).toLowerCase())
      )
    );
  };

  const handleEventChange = (event) => {
    setSelectedEvents((prevSelectedEvents) =>
      prevSelectedEvents.includes(event)
        ? prevSelectedEvents.filter(
            (selected) => selected.eventId !== event.eventId
          )
        : [event]
    );
  };

  const handleAssigneeSearch = (search) => {
    setFilteredAssignees(
      members.filter(({ member }) => {
        if (!member.firstName && !member.lastName) return false;

        const fullName = `${member.firstName || ""} ${
          member.lastName || ""
        }`.toLowerCase();
        return fullName.includes(search.toLowerCase());
      })
    );
  };

  const handleAssigneeChange = (member) => {
    setSelectedAssignees((prevSelectedAssignees) =>
      prevSelectedAssignees.includes(member)
        ? prevSelectedAssignees.filter((selected) => selected.id !== member.id)
        : [member]
    );
  };

  const handleClose = () => {
    setOpen(false);
    setSelectedSkills([]);
    setSelectedEvents([]);
    setSelectedAssignees([]);
  };

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
        assigneeId:
          selectedAssignees.length > 0 ? selectedAssignees[0].id : null,
        eventId: selectedEvents.length > 0 ? selectedEvents[0].id : null,
        fromDate: data.fromDate ? data.fromDate.toISOString() : null,
      });

      const skillIdList = selectedSkills.map((skill) => skill.skillId);
      await TaskService.updateTaskSkills(res.id, skillIdList);
      dispatch(clearCache());
      toast.success("Thêm nhiệm vụ thành công");
      navigate(`/project/${project.id}/task/${res.id}`);
      if (selectedEvents.length > 0) await dispatch(fetchEvents(project.id));
    } catch (e) {
      console.error(e);
      toast.error("Thêm nhiệm vụ thất bại. Vui lòng thử lại sau.");
    } finally {
      setOpen(false);
      setCreateLoading(false);
      setSelectedSkills([]);
      setSelectedAssignees([]);
      setSelectedEvents([]);
    }
  };

  return (
    <Dialog
      fullWidth
      open={open}
      maxWidth="md"
      scroll="paper"
      onClose={handleClose}
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
            Thêm mới nhiệm vụ
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
                autoFocus
              />
            </FormControl>
          </Grid>
          <Grid item sm={12} xs={12}>
            <Typography>Mô tả</Typography>
            <Controller
              name="description"
              control={control}
              defaultValue={null}
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
                {status.taskStatuses.map((status) => (
                  <MenuItem key={status.statusId} value={status.statusId}>
                    <TaskStatus status={status} />
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
                defaultValue={category.categories[0]?.categoryId ?? ""}
                rules={{ required: true }}
                as={
                  <Select
                    fullWidth
                    id="select-category"
                    label="Danh mục"
                    labelId="role-category"
                    inputProps={{ placeholder: "Danh mục" }}
                  >
                    {category.categories.map((category) => (
                      <MenuItem
                        key={category.categoryId}
                        value={category.categoryId}
                      >
                        <TaskCategory category={category} />
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
                defaultValue={priority.priorities[0]?.priorityId ?? ""}
                rules={{ required: true }}
                as={
                  <Select
                    fullWidth
                    id="select-priority"
                    label="Ưu tiên"
                    labelId="priority-select"
                    inputProps={{ placeholder: "Ưu tiên" }}
                  >
                    {priority.priorities.map((priority) => (
                      <MenuItem
                        key={priority.priorityId}
                        value={priority.priorityId}
                      >
                        <TaskPriority priority={priority} showText />
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

          <Grid item sm={12} xs={12}>
            <Box
              sx={{
                display: "flex",
                gap: 3,
                flexWrap: "wrap",
              }}
            >
              <ItemSelector
                items={filteredAssignees.map(({ member }) => member)}
                selectedItems={selectedAssignees}
                onSelectChange={handleAssigneeChange}
                handleSearch={handleAssigneeSearch}
                renderItem={(item) => (
                  <Box sx={{ display: "flex", gap: 2, alignItems: "center" }}>
                    <UserAvatar user={item} />
                    <Typography variant="subtitle2">{`${item.firstName ?? ""} ${
                      item.lastName ?? ""
                    }`}</Typography>
                  </Box>
                )}
                renderSelectedItem={(items) => (
                  <Box>
                    {items.map((member) => (
                      <Box
                        key={member.id}
                        sx={{
                          display: "flex",
                          gap: 2,
                          alignItems: "center",
                        }}
                      >
                        <UserAvatar user={member} />
                        <Typography variant="subtitle2">{`${
                          member.firstName ?? ""
                        } ${member.lastName ?? ""}`}</Typography>
                      </Box>
                    ))}
                  </Box>
                )}
                placeholder="Tìm kiếm thành viên"
                label="Phân công cho"
                startIcon={<Icon icon="ci:user-02" />}
                idPopover="assignee-popover"
              />
              <ItemSelector
                items={filteredEvents}
                selectedItems={selectedEvents}
                onSelectChange={handleEventChange}
                handleSearch={handleEventSearch}
                renderItem={(item) => (
                  <Typography
                    sx={{
                      whiteSpace: "nowrap",
                      overflow: "hidden",
                      textOverflow: "ellipsis",
                    }}
                  >
                    {item.name}
                  </Typography>
                )}
                renderSelectedItem={(items) => (
                  <Box sx={{ maxWidth: 400 }}>
                    {items.map((item) => (
                      <Typography
                        key={item.id}
                        sx={{
                          whiteSpace: "nowrap",
                          overflow: "hidden",
                          textOverflow: "ellipsis",
                        }}
                      >
                        {item.name}
                      </Typography>
                    ))}
                  </Box>
                )}
                placeholder="Tìm kiếm sự kiện"
                label="Sự kiện"
                startIcon={<Icon icon="pixelarticons:group" />}
                idPopover="event-popover"
              />
              <ItemSelector
                items={filteredSkills}
                selectedItems={selectedSkills}
                onSelectChange={handleSkillChange}
                handleSearch={handleSkillSearch}
                renderItem={(item) => (
                  <Typography
                    sx={{
                      whiteSpace: "nowrap",
                      overflow: "hidden",
                      textOverflow: "ellipsis",
                    }}
                  >
                    {item.name}
                  </Typography>
                )}
                renderSelectedItem={(items) => (
                  <Box
                    sx={{
                      display: "flex",
                      gap: "5px",
                    }}
                  >
                    {items.map((value) => (
                      <SkillChip key={value.skillId} skill={value} />
                    ))}
                  </Box>
                )}
                placeholder="Tìm kiếm kỹ năng"
                label="Kỹ năng"
                startIcon={<Icon icon="gravity-ui:gear" />}
                idPopover="skill-popover"
              />
            </Box>
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
        <Button variant="outlined" color="secondary" onClick={handleClose}>
          Hủy
        </Button>
      </DialogActions>
    </Dialog>
  );
};

DialogAddTask.propTypes = {
  open: PropTypes.bool.isRequired,
  setOpen: PropTypes.func.isRequired,
  defaultEvent: PropTypes.object,
};

export { DialogAddTask };
