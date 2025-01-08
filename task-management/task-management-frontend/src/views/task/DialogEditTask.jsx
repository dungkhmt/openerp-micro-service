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
  ListItemText,
} from "@mui/material";
import { DateTimePicker } from "@mui/x-date-pickers";
import dayjs from "dayjs";
import PropTypes from "prop-types";
import { forwardRef, useEffect, useState } from "react";
import { Controller, useForm } from "react-hook-form";
import toast from "react-hot-toast";
import { useSelector } from "react-redux";
import { UserAvatar } from "../../components/common/avatar/UserAvatar";
import { CustomMDEditor } from "../../components/editor/md-editor/CustomMDEditor";
import { FileUploader } from "../../components/file-uploader";
import { LoadingButton } from "../../components/mui/button/LoadingButton";
import { TaskCategory } from "../../components/task/category";
import { TaskPriority } from "../../components/task/priority";
import { TaskStatus } from "../../components/task/status";
import { useTaskContext } from "../../hooks/useTaskContext";
import { FileService } from "../../services/api/file.service";
import { TaskService } from "../../services/api/task.service";
import { SkillChip } from "../../components/task/skill";
import ItemSelector from "../../components/mui/dialog/ItemSelector";
import { useDispatch } from "react-redux";
import { fetchEvents } from "../../store/project/events";
import { useParams } from "react-router";

const Transition = forwardRef(function Transition(props, ref) {
  return <Fade ref={ref} {...props} />;
});

const DialogEditTask = ({ open, setOpen }) => {
  const dispatch = useDispatch();
  const { id } = useParams();
  const { task, isUpdate, setIsUpdate, taskSkills } = useTaskContext();
  const { members } = useSelector((state) => state.project);
  const { events } = useSelector((state) => state.events);
  const { category, priority, status, skill } = useSelector((state) => state);

  const [toggleEditDesc, setToggleEditDesc] = useState(false);
  const [files, setFiles] = useState(null);
  const [updateLoading, setUpdateLoading] = useState(false);
  const { register, handleSubmit, errors, setValue, control } = useForm();

  const [selectedSkills, setSelectedSkills] = useState([]);
  const [filteredSkills, setFilteredSkills] = useState(skill.skills);

  const handleSkillSearch = (search) => {
    setFilteredSkills(
      skill.skills.filter((item) =>
        item.name.toLowerCase().includes(search.toLowerCase())
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

  const [selectedEvents, setSelectedEvents] = useState();
  const [filteredEvents, setFilteredEvents] = useState(events);

  const handleEventSearch = (search) => {
    setFilteredEvents(
      events.filter((item) =>
        item.name.toLowerCase().includes(search.toLowerCase())
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

  const [selectedAssignees, setSelectedAssignees] = useState();
  const [filteredAssignees, setFilteredAssignees] = useState(members);

  const handleAssigneeSearch = (search) => {
    setFilteredAssignees(
      members.filter(
        ({ member }) =>
          member.firstName.toLowerCase().includes(search.toLowerCase()) ||
          member.lastName.toLowerCase().includes(search.toLowerCase())
      )
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

      const updatedAssigneeId =
        selectedAssignees.length > 0 ? selectedAssignees[0].id : null;
      const updatedEventId =
        selectedEvents.length > 0 ? selectedEvents[0].id : null;
      await TaskService.updateTask(task.id, {
        ...data,
        attachmentPaths,
        assigneeId: updatedAssigneeId,
        eventId: updatedEventId,
      });

      const skillIdList = selectedSkills.map((skill) => skill.skillId);
      await TaskService.updateTaskSkills(task.id, skillIdList);

      const originalEventId = task.event ? task.event.id : null;
      if (updatedEventId !== originalEventId) await dispatch(fetchEvents(id));

      toast.success("Cập nhật nhiệm vụ thành công");
      setIsUpdate(!isUpdate);
      setOpen(false);
    } catch (e) {
      console.error(e);
      toast.error("Cập nhật nhiệm vụ thất bại. Vui lòng thử lại sau.");
    } finally {
      setUpdateLoading(false);
    }
  };

  useEffect(() => {
    if (open) {
      const selectedSkills = taskSkills.map((taskSkill) => {
        return (
          skill.skills.find((skill) => skill.skillId === taskSkill.skillId) ||
          null
        );
      });
      setSelectedSkills(selectedSkills);
      const selectedEvents = task.event
        ? [events.find((event) => event.id === task.event.id)]
        : [];
      setSelectedEvents(selectedEvents);
      const selectedAssignees = task.assignee
        ? [members.find(({ member }) => member.id === task.assignee.id)].map(
            ({ member }) => member
          )
        : [];
      setSelectedAssignees(selectedAssignees);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [open]);

  return (
    <Dialog
      fullWidth
      open={open}
      maxWidth="md"
      scroll="paper"
      onClose={handleClose}
      onOpen
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
                    {status.statuses.map((status) => (
                      <MenuItem key={status.statusId} value={status.statusId}>
                        <TaskStatus status={status} />
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
                renderItem={(item) => <ListItemText primary={item.name} />}
                renderSelectedItem={(items) => (
                  <Box>
                    {items.map((item) => (
                      <ListItemText key={item.skillId} primary={item.name} />
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
                renderItem={(item) => <ListItemText primary={item.name} />}
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
        <Button variant="outlined" color="secondary" onClick={handleClose}>
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
