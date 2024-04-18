import { Icon } from "@iconify/react";
import { Box, Grid, LinearProgress, Tooltip, Typography } from "@mui/material";
import dayjs from "dayjs";
import { useSelector } from "react-redux";
import { Link } from "react-router-dom";
import { UserAvatar } from "../../../components/common/avatar/UserAvatar";
import { TaskStatus } from "../../../components/task/status";
import { useTaskContext } from "../../../hooks/useTaskContext";
import { getProgressColor } from "../../../utils/color.util";

const TaskViewHierarchy = () => {
  const { task } = useTaskContext();
  const { members, project } = useSelector((state) => state.project);
  const { status: statusStore } = useSelector((state) => state);
  const hierarchies = task.hierarchies;

  if (!hierarchies || hierarchies.length === 1) {
    return null;
  }

  return hierarchies.map((hierarchy, index) => {
    const status = statusStore.statuses.find(
      (status) => status.statusId === hierarchy.statusId
    );
    const assignee = members.find(
      (member) => member.member.id === hierarchy.assigneeId
    )?.member;
    const fullName = `${assignee?.firstName ?? ""} ${assignee?.lastName ?? ""}`;

    return (
      <Grid
        container
        spacing={2}
        key={hierarchy.id ?? index}
        sx={{
          p: 2,
          backgroundColor: (theme) =>
            task.id === hierarchy.id ? theme.palette.grey[100] : "transparent",
          borderRadius: 1,
          "&:hover":
            task.id === hierarchy.id
              ? {}
              : {
                  backgroundColor: (theme) => theme.palette.grey[50],
                },
        }}
      >
        <Grid item xl={5} lg={5} md={5} sm={12}>
          <Box
            sx={{
              display: "flex",
              alignItems: "center",
              ml: 6 * (hierarchy.level ?? 0),
              gap: 0.5,
              borderLeft: (theme) => `2px solid ${theme.palette.divider}`,
              paddingLeft: 1,
            }}
          >
            <Icon
              icon={
                hierarchy.level !== 0
                  ? "typcn:flow-children"
                  : "gis:flag-start-b-o"
              }
              width={14}
              height={14}
            />
            <Tooltip title={`Xem task - ${hierarchy.name}`}>
              <Typography
                variant="body2"
                noWrap
                color={task.id === hierarchy.id ? "error.main" : "primary.main"}
                sx={{
                  cursor: "pointer",
                  fontWeight: 550,
                  textDecoration: "none",
                  textTransform: "capitalize",
                  "&:hover": {
                    color: "info.dark",
                    textDecoration: "underline",
                  },
                }}
                component={Link}
                to={`/project/${project.id}/task/${hierarchy.id}`}
              >
                {hierarchy.name}
              </Typography>
            </Tooltip>
          </Box>
        </Grid>
        <Grid item xl={7} lg={7} md={7} sm={12}>
          <Grid container spacing={2}>
            <Grid
              item
              sm={2}
              sx={{ display: "flex", justifyContent: "center" }}
            >
              <TaskStatus status={status} />
            </Grid>
            <Grid
              item
              sm={2}
              sx={{
                display: "flex",
                justifyContent: "center",
                alignItems: "center",
              }}
            >
              {assignee && (
                <Tooltip title={fullName}>
                  <UserAvatar user={assignee} sx={{ cursor: "pointer" }} />
                </Tooltip>
              )}
            </Grid>
            <Grid
              item
              sm={3}
              sx={{ display: "flex", justifyContent: "center" }}
            >
              {hierarchy.fromDate
                ? dayjs(hierarchy.fromDate).format("DD/MM/YYYY")
                : " - "}
            </Grid>
            <Grid item sm={5}>
              <Box sx={{ width: "100%" }}>
                <LinearProgress
                  variant="determinate"
                  value={hierarchy.progress ?? 0}
                  color={getProgressColor(hierarchy.progress)}
                  sx={{ height: 6, borderRadius: "5px" }}
                />
                <Typography variant="body2" sx={{ color: "text.primary" }}>
                  {hierarchy.progress ?? 0}%
                </Typography>
              </Box>
            </Grid>
          </Grid>
        </Grid>
      </Grid>
    );
  });
};

export { TaskViewHierarchy };

