import { Icon } from "@iconify/react";
import {
  Box,
  Card,
  CardContent,
  Divider,
  Grid,
  IconButton,
  Tooltip,
  Typography,
  styled,
} from "@mui/material";
import MDEditor from "@uiw/react-md-editor";
import dayjs from "dayjs";
import { useState } from "react";
import toast from "react-hot-toast";
import { UserAvatar } from "../../components/common/avatar/UserAvatar";
import { useTaskContext } from "../../hooks/useTaskContext";
import { FileService } from "../../services/api/file.service";
import { getDiffDateWithCurrent } from "../../utils/date.util";
import { DialogEditTask } from "./DialogEditTask";
import CommentSection from "./comment/CommentSection";
import { DialogAddSubTask } from "./hierarchy/DialogAddSubTask";
import { TaskViewHierarchy } from "./hierarchy/TaskViewHierarchy";
import TaskViewLog from "./log/TaskViewLog";
import { SkillChip } from "../../components/task/skill";

const TextHighlight = styled(Typography)(({ theme }) => ({
  color: theme.palette.primary.main,
  fontWeight: 600,
  cursor: "pointer",
  fontSize: "0.8rem",
}));

export const TitleWrapper = styled(Box)(({ theme }) => ({
  display: "flex",
  alignItems: "center",
  gap: 4,
  "& svg": {
    color: theme.palette.primary.main,
  },
}));

const TaskViewLeft = () => {
  const { task, taskSkills } = useTaskContext();

  const [openEdit, setOpenEdit] = useState(false);
  const [openAddSubTask, setOpenAddSubTask] = useState(false);

  const [fileName, fileId] = task.attachmentPaths?.split(",") ?? [];

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

  return (
    <Grid container spacing={6}>
      <Grid item xs={12} md={12}>
        <Card sx={{ p: 2 }}>
          {/* Header */}
          <CardContent>
            <Grid container spacing={6}>
              <Grid item xs={12} md={1} sm={0.75} xl={0.75}>
                <UserAvatar
                  user={task.creator}
                  width={70}
                  height={70}
                  fontSize="2rem"
                />
              </Grid>

              <Grid item xs={12} md={10} xl={10.5}>
                <Box
                  sx={{
                    display: "flex",
                    flexDirection: "column",
                    gap: 2,
                    ml: 4,
                  }}
                >
                  <Typography
                    variant="h5"
                    sx={{
                      textTransform: "capitalize",
                      wordBreak: "break-word",
                      whiteSpace: "pre-wrap",
                    }}
                  >
                    {task.name}
                  </Typography>
                  <Typography
                    variant="subtitle2"
                    sx={{ fontSize: "0.75rem !important" }}
                  >
                    Thêm bởi
                    <TextHighlight component="span">{` ${
                      task.creator?.firstName ?? ""
                    } ${task.creator?.lastName ?? ""} `}</TextHighlight>
                    cách đây
                    <Tooltip
                      title={dayjs(task.createdStamp).format(
                        "DD/MM/YYYY HH:mm:ss"
                      )}
                    >
                      <TextHighlight component="span">{` ${getDiffDateWithCurrent(
                        task.createdStamp
                      ).join(" ")} `}</TextHighlight>
                    </Tooltip>
                    trước.
                    {task.lastUpdatedStamp && (
                      <>
                        <Typography
                          component="span"
                          variant="subtitle2"
                          sx={{ fontSize: "0.75rem !important" }}
                        >{` Cập nhật lần cuối cách đây `}</Typography>
                        <Tooltip
                          title={dayjs(task.lastUpdatedStamp).format(
                            "DD/MM/YYYY HH:mm:ss"
                          )}
                        >
                          <TextHighlight component="span">
                            {getDiffDateWithCurrent(task.lastUpdatedStamp).join(
                              " "
                            )}
                          </TextHighlight>
                        </Tooltip>
                      </>
                    )}
                  </Typography>
                </Box>
              </Grid>

              <Grid item xs={12} md={1} xl={0.75}>
                <Box>
                  <Tooltip title="Chỉnh sửa">
                    <IconButton
                      onClick={() => {
                        setOpenEdit(true);
                      }}
                      sx={{
                        "& svg": {
                          color: (theme) => theme.palette.primary.main,
                        },
                      }}
                    >
                      <Icon icon="mdi:edit-outline" />
                    </IconButton>
                  </Tooltip>
                </Box>
              </Grid>
            </Grid>
          </CardContent>

          {/* required skills */}
          <CardContent>
            <Divider sx={{ mb: (theme) => `${theme.spacing(4)} !important` }} />
            <TitleWrapper>
              <Icon icon="gravity-ui:gear"></Icon>
              <Typography sx={{ fontWeight: 600, fontSize: "1rem" }}>
                Kỹ năng yêu cầu
              </Typography>
            </TitleWrapper>
            {taskSkills?.length > 0 ? (
              <Box
                sx={{
                  display: "flex",
                  flexWrap: "wrap",
                  paddingTop: 3,
                  rowGap: 2,
                  columnGap: 2,
                }}
              >
                {taskSkills.map((skill) => (
                  <SkillChip key={skill.skillId} skill={skill} />
                ))}
              </Box>
            ) : (
              <Typography variant="body2" sx={{ fontStyle: "italic" }}>
                Không có kỹ năng yêu cầu
              </Typography>
            )}
          </CardContent>

          {/* description */}
          <CardContent>
            <Divider sx={{ mb: (theme) => `${theme.spacing(4)} !important` }} />
            <TitleWrapper>
              <Icon icon="material-symbols:description-outline"></Icon>
              <Typography sx={{ fontWeight: 600, fontSize: "1rem" }}>
                Mô tả
              </Typography>
            </TitleWrapper>
            {task.description ? (
              <MDEditor.Markdown source={task.description} />
            ) : (
              <Typography variant="body2" sx={{ fontStyle: "italic" }}>
                Không có mô tả
              </Typography>
            )}
          </CardContent>

          {/* Attachment */}
          <CardContent>
            <Divider sx={{ mb: (theme) => `${theme.spacing(4)} !important` }} />
            <TitleWrapper>
              <Icon icon="carbon:attachment"></Icon>
              <Typography sx={{ fontWeight: 600, fontSize: "1rem" }}>
                Tài liệu đính kèm
              </Typography>
            </TitleWrapper>
            {fileName && fileId ? (
              <>
                <Typography
                  component="span"
                  variant="body2"
                  sx={{ fontStyle: "italic", mr: 2 }}
                >
                  {fileName}
                </Typography>
                <IconButton
                  aria-label="add an alarm"
                  sx={{
                    "& svg": {
                      color: (theme) => theme.palette.primary.main,
                    },
                  }}
                  onClick={onDownloadFile}
                >
                  <Icon icon="mdi:download-outline" />
                </IconButton>
              </>
            ) : (
              <Typography variant="body2" sx={{ fontStyle: "italic" }}>
                Không có tài liệu đính kèm
              </Typography>
            )}
          </CardContent>

          {/* Hierarchy */}
          <CardContent>
            <Divider sx={{ mb: (theme) => `${theme.spacing(4)} !important` }} />
            <Box
              sx={{
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between",
                mb: 2,
              }}
            >
              <TitleWrapper>
                <Icon icon="carbon:tree-view"></Icon>
                <Typography sx={{ fontWeight: 600, fontSize: "1rem" }}>
                  Nhiệm vụ con
                </Typography>
              </TitleWrapper>
              <Typography
                variant="subtitle2"
                sx={{
                  fontStyle: "italic",
                  fontSize: "0.9rem",
                  cursor: "pointer",
                  color: "primary.main",
                  "&:hover": {
                    color: "info.main",
                    textDecoration: "underline",
                  },
                  display: "flex",
                  alignItems: "center",
                }}
                onClick={() => setOpenAddSubTask(true)}
              >
                <Icon icon="mdi:plus" />
                <span>Thêm nhiệm vụ con</span>
              </Typography>
            </Box>
            {task.hierarchies?.length > 1 ? (
              <TaskViewHierarchy />
            ) : (
              <Typography
                variant="body2"
                sx={{ fontStyle: "italic", fontSize: "1rem" }}
              >
                -
              </Typography>
            )}
          </CardContent>
        </Card>
      </Grid>

      <Grid item xs={12} md={12}>
        <CommentSection />
      </Grid>

      <Grid item xs={12} md={12}>
        <TitleWrapper>
          <Typography sx={{ fontWeight: 600, fontSize: "1rem" }}>
            Lịch sử
          </Typography>
        </TitleWrapper>
        <TaskViewLog />
      </Grid>

      <DialogEditTask open={openEdit} setOpen={setOpenEdit} />
      <DialogAddSubTask open={openAddSubTask} setOpen={setOpenAddSubTask} />
    </Grid>
  );
};

export { TaskViewLeft };
