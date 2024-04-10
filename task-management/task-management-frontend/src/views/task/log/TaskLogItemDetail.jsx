import { Icon } from "@iconify/react";
import {
  Box,
  Card,
  Dialog,
  DialogContent,
  Grid,
  IconButton,
  Tooltip,
  Typography,
  styled,
} from "@mui/material";
import MDEditor from "@uiw/react-md-editor";
import PropTypes from "prop-types";
import { useState } from "react";
import toast from "react-hot-toast";
import { useSelector } from "react-redux";
import { Link } from "react-router-dom";
import { TaskCategory } from "../../../components/task/category";
import { TaskPriority } from "../../../components/task/priority";
import { TaskStatus } from "../../../components/task/status";
import { useTaskContext } from "../../../hooks/useTaskContext";
import { FileService } from "../../../services/api/file.service";
import { parseLogItemDetail } from "../../../utils/text-parse.util";

const TextHighlight = styled(Typography)(({ theme }) => ({
  color: theme.palette.secondary.dark,
  fontWeight: 550,
  cursor: "pointer",
  fontSize: "0.9rem",
}));

const TextProperty = styled(Typography)({
  fontWeight: 550,
  fontSize: "0.9rem",
});

const TextState = styled(Typography)({
  fontStyle: "italic",
  fontSize: "0.9rem",
});

const FieldComponent = ({ field, value, isOld = false }) => {
  const { members, project } = useSelector((state) => state.project);
  const {
    category: categoryStore,
    priority: priorityStore,
    status: statusStore,
  } = useSelector((state) => state);
  const { task } = useTaskContext();

  let category, status, priority, assignee, fileName, fileId, subTask;

  const onDownloadFile = async () => {
    if (!fileId) {
      return;
    }
    try {
      const data = await FileService.downloadFile(fileId);
      const blob = new Blob([data], { type: "application/octet-stream" });

      if (window.navigator.msSaveOrOpenBlob) {
        window.navigator.msSaveBlob(blob, fileName);
      } else {
        const a = document.createElement("a");
        document.body.appendChild(a);
        a.href = window.URL.createObjectURL(blob);
        a.download = fileName;
        a.target = "_blank";
        a.click();
        document.body.removeChild(a);
      }
    } catch (e) {
      toast.error("Tải tài liệu thất bại");
    }
  };

  if (!value) return null;

  switch (field) {
    case "name":
      return (
        <Tooltip title={value} placement="top">
          <TextHighlight
            noWrap
            sx={{
              maxWidth: "120px",
              textDecoration: isOld ? "line-through" : "none",
            }}
          >
            {value}
          </TextHighlight>
        </Tooltip>
      );
    case "progress":
    case "estimatedTime":
    case "fromDate":
    case "dueDate":
      return (
        <TextHighlight
          noWrap
          sx={{
            textDecoration: isOld ? "line-through" : "none",
          }}
        >
          {value}
        </TextHighlight>
      );
    case "categoryId":
      category = categoryStore.categories.find((c) => c.categoryId === value);
      return category && <TaskCategory category={category} />;
    case "priorityId":
      priority = priorityStore.priorities.find((p) => p.priorityId === value);
      return priority && <TaskPriority priority={priority} />;
    case "statusId":
      status = statusStore.statuses.find((s) => s.statusId === value);
      return status && <TaskStatus status={status} />;
    case "assigneeId":
      assignee = members.find((m) => m.member.id === value);

      return (
        assignee && (
          <TextHighlight
            sx={{ textDecoration: isOld ? "line-through" : "none" }}
          >
            {`${assignee.member?.firstName ?? ""} ${
              assignee.member?.lastName ?? ""
            }`}
          </TextHighlight>
        )
      );

    case "attachmentPaths":
      [fileName, fileId] = value.split(",");
      return fileName && fileId ? (
        <TextHighlight
          sx={{
            textDecoration: "underline",
            "&:hover": {
              cursor: "pointer",
              color: "primary.dark",
              alignContent: "center",
            },
          }}
          onClick={onDownloadFile}
        >
          {fileName}
          <Icon icon="mdi:download-outline" />
        </TextHighlight>
      ) : (
        <TextState> - </TextState>
      );

    case "subtaskId":
      subTask = task.hierarchies.find((sub) => sub.id === value);
      return (
        <Tooltip title={subTask.name}>
          <TextHighlight
            sx={{
              textDecoration: "underline",
              "&:hover": {
                cursor: "pointer",
                color: "primary.dark",
                alignContent: "center",
              },
              maxWidth: "250px",
            }}
            noWrap
            component={Link}
            to={`/project/${project.id}/task/${value}`}
          >
            {subTask?.name ?? " - "}
          </TextHighlight>
        </Tooltip>
      );
  }
};

FieldComponent.propTypes = {
  field: PropTypes.string,
  value: PropTypes.string,
  isOld: PropTypes.bool,
};

const ViewDetailDiff = ({ oldValue, newValue, open, setOpen }) => {
  return (
    <Dialog
      fullWidth
      open={open}
      maxWidth="md"
      scroll="body"
      onClose={() => setOpen(false)}
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

        <Grid container spacing={6}>
          <Grid item sm={12} xs={12}>
            <Typography variant="subtitle2">Trước</Typography>
            <Card sx={{ p: 4 }}>
              <MDEditor.Markdown source={oldValue} />
            </Card>
          </Grid>
          <Grid item sm={12} xs={12}>
            <Typography variant="subtitle2">Sau</Typography>
            <Card sx={{ p: 4 }}>
              <MDEditor.Markdown source={newValue} />
            </Card>
          </Grid>
        </Grid>
      </DialogContent>
    </Dialog>
  );
};

ViewDetailDiff.propTypes = {
  oldValue: PropTypes.string,
  newValue: PropTypes.string,
  open: PropTypes.bool,
  setOpen: PropTypes.func,
};

const TaskLogItemDetail = ({ detail }) => {
  const parsed = parseLogItemDetail(detail);
  const [open, setOpen] = useState(false);

  return (
    <Box sx={{ display: "flex", gap: 1, justifyContent: "center" }}>
      <TextProperty>{parsed[0]}</TextProperty>
      <TextState>{parsed[1]}</TextState>
      {detail.field !== "description" ? (
        <>
          <FieldComponent field={detail.field} value={parsed[2]} isOld />
          <TextState>{parsed[3]}</TextState>
          <FieldComponent field={detail.field} value={parsed[4]} />
        </>
      ) : (
        <>
          <Typography
            sx={{
              fontSize: "0.9rem",
              color: "primary.light",
              "&:hover": {
                cursor: "pointer",
                color: "primary.dark",
              },
            }}
            onClick={() => setOpen(true)}
          >{`(Xem)`}</Typography>
          <ViewDetailDiff
            oldValue={detail.oldValue}
            newValue={detail.newValue}
            open={open}
            setOpen={setOpen}
          />
        </>
      )}
    </Box>
  );
};

TaskLogItemDetail.propTypes = {
  detail: PropTypes.object,
};

export { TaskLogItemDetail };
