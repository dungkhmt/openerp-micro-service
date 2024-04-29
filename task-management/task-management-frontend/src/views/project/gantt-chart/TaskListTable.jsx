import { Box, LinearProgress, Tooltip, Typography } from "@mui/material";
import PropTypes from "prop-types";
import { UserAvatar } from "../../../components/common/avatar/UserAvatar";
import { getProgressColor } from "../../../utils/color.util";
import { Link } from "react-router-dom";
import { useSelector } from "react-redux";

const TaskListTable = ({
  rowHeight,
  rowWidth,
  tasks,
  fontFamily,
  fontSize,
  onExpanderClick,
}) => {
  const { project } = useSelector((state) => state.project);
  return (
    <Box
      sx={{
        fontFamily: fontFamily,
        fontSize: fontSize,
        borderBottom: (theme) => `1px solid ${theme.palette.divider}`,
        borderRight: (theme) => `1px solid ${theme.palette.divider}`,
      }}
    >
      {tasks.map((t, i) => {
        let expanderSymbol = "";
        if (t.hideChildren === false) {
          expanderSymbol = "▼";
        } else if (t.hideChildren === true) {
          expanderSymbol = "▶";
        }

        return (
          <Box
            sx={{
              display: "flex",
              height: rowHeight,
              borderLeft: (theme) => `1px solid ${theme.palette.divider}`,
              p: 1,
              backgroundColor: i % 2 === 0 ? "#fff" : "#f5f5f5",
            }}
            key={`${t.id}-row`}
          >
            <Box
              sx={{
                maxHeight: rowHeight,
                width: rowWidth,
                display: "flex",
                alignItems: "center",
                gap: 1,
              }}
            >
              <Box
                sx={{ cursor: "pointer" }}
                onClick={() => onExpanderClick(t)}
              >
                {expanderSymbol}
              </Box>
              <Tooltip title={t.name}>
                <Typography
                  noWrap
                  component={Link}
                  to={`/project/${project.id}/task/${t.id}`}
                  sx={{ textDecoration: "none" }}
                >
                  {t.name}
                </Typography>
              </Tooltip>
            </Box>

            <Box
              sx={{
                maxHeight: rowHeight,
                width: "80px",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
              }}
            >
              {t.assignee && <UserAvatar user={t.assignee} />}
            </Box>

            {t.progress !== undefined && (
              <Box
                sx={{
                  maxHeight: rowHeight,
                  width: rowWidth,
                }}
              >
                <Typography variant="body2" sx={{ color: "text.primary" }}>
                  {t.progress}%
                </Typography>
                <LinearProgress
                  variant="determinate"
                  value={t.progress ?? 0}
                  color={getProgressColor(t.progress)}
                  sx={{ height: 6, borderRadius: "5px" }}
                />
              </Box>
            )}
          </Box>
        );
      })}
    </Box>
  );
};

TaskListTable.propTypes = {
  rowHeight: PropTypes.number.isRequired,
  rowWidth: PropTypes.number.isRequired,
  tasks: PropTypes.array.isRequired,
  fontFamily: PropTypes.string.isRequired,
  fontSize: PropTypes.number.isRequired,
  onExpanderClick: PropTypes.func.isRequired,
};

export { TaskListTable };
