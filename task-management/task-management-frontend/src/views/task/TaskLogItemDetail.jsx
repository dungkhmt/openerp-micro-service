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
import PropTypes from "prop-types";
import CustomChip from "../../components/mui/chip";
import { useProjectContext } from "../../hooks/useProjectContext";
import {
  getCategoryColor,
  getPriorityColor,
  getStatusColor,
} from "../../utils/color.util";
import { parseLogItemDetail } from "../../utils/text-parse.util";
import { Icon } from "@iconify/react";
import MDEditor from "@uiw/react-md-editor";
import { useState } from "react";
import { FileService } from "../../services/api/file.service";
import toast from "react-hot-toast";
import { Link } from "react-router-dom";
import { useTaskContext } from "../../hooks/useTaskContext";

const TextHighlight = styled(Typography)(({ theme }) => ({
  color: theme.palette.secondary.dark,
  fontWeight: 600,
  cursor: "pointer",
  fontSize: "0.9rem",
}));

const TextProperty = styled(Typography)({
  fontWeight: 600,
  fontSize: "0.9rem",
});

const TextState = styled(Typography)({
  fontStyle: "italic",
  fontSize: "0.9rem",
});

const FieldComponent = ({ field, value, isOld = false }) => {
  const { statuses, categories, priorities, members, project } =
    useProjectContext();
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
      category = categories.find((c) => c.categoryId === value);
      return (
        category && (
          <CustomChip
            size="small"
            skin="light"
            label={category.categoryName}
            color={getCategoryColor(category.categoryId)}
            sx={{ textDecoration: isOld ? "line-through" : "none" }}
          />
        )
      );
    case "priorityId":
      priority = priorities.find((p) => p.priorityId === value);
      return (
        priority && (
          <CustomChip
            size="small"
            skin="light"
            label={priority.priorityName}
            color={getPriorityColor(priority.priorityId)}
            sx={{ textDecoration: isOld ? "line-through" : "none" }}
          />
        )
      );
    case "statusId":
      status = statuses.find((s) => s.statusId === value);
      return (
        status && (
          <CustomChip
            size="small"
            skin="light"
            label={status.description}
            color={getStatusColor(status.statusId)}
            sx={{ textDecoration: isOld ? "line-through" : "none" }}
          />
        )
      );
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
        <TextHighlight
          sx={{
            textDecoration: "underline",
            "&:hover": {
              cursor: "pointer",
              color: "primary.dark",
              alignContent: "center",
            },
          }}
          component={Link}
          to={`/project/${project.id}/task/${value}`}
        >
          {subTask?.name ?? " - "}
        </TextHighlight>
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
