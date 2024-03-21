import { Box, Grid, LinearProgress, Tooltip, Typography } from "@mui/material";
import { useTaskContext } from "../../hooks/useTaskContext";
import { useProjectContext } from "../../hooks/useProjectContext";
import CustomChip from "../../components/mui/chip";
import {
  getProgressColor,
  getRandomColorSkin,
  getStatusColor,
} from "../../utils/color.util";
import CustomAvatar from "../../components/mui/avatar/CustomAvatar";
import dayjs from "dayjs";
import { Icon } from "@iconify/react";
import { Link } from "react-router-dom";

const TaskViewHierarchy = () => {
  const { task } = useTaskContext();
  const { statuses, members, project } = useProjectContext();
  const hierarchies = task.hierarchies;

  if (!hierarchies || hierarchies.length === 1) {
    return null;
  }

  return hierarchies.map((hierarchy, index) => {
    const status = statuses.find(
      (status) => status.statusId === hierarchy.statusId
    );
    const assignee = members.find(
      (member) => member.member.id === hierarchy.assigneeId
    )?.member;
    const fullName = `${assignee?.firstName ?? ""} ${assignee?.lastName ?? ""}`;

    return (
      <Grid container spacing={2} key={hierarchy.id ?? index} sx={{ mb: 2 }}>
        <Grid item xl={5} lg={5} md={5} sm={12}>
          <Box
            sx={{
              display: "flex",
              alignItems: "center",
              ml: 6 * (hierarchy.level ?? 0),
              gap: 0.5,
            }}
          >
            <Icon
              icon={
                hierarchy.level !== 0
                  ? "akar-icons:chevron-right"
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
              <CustomChip
                size="small"
                skin="light"
                label={status.description}
                color={getStatusColor(status.statusId)}
              />
            </Grid>
            <Grid
              item
              sm={2}
              sx={{ display: "flex", justifyContent: "center" }}
            >
              {assignee && (
                <Tooltip title={fullName}>
                  <CustomAvatar
                    skin="light"
                    color={getRandomColorSkin(assignee.id)}
                    sx={{
                      width: 30,
                      height: 30,
                      fontSize: ".8rem",
                      cursor: "pointer",
                    }}
                  >
                    {`${assignee.firstName?.charAt(0) ?? ""}${
                      assignee.lastName?.charAt(0) ?? ""
                    }`}
                  </CustomAvatar>
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
