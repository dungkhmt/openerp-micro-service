import { Box, Typography, styled } from "@mui/material";
import PropTypes from "prop-types";

const TaskListWrapper = styled(Box)(({ theme }) => ({
  border: `1px solid ${theme.palette.divider}`,
  display: "flex",
  backgroundColor: theme.palette.background.paper,

  "& .task-list-header": {
    display: "flex",
    alignItems: "center",
    justifyContent: "flex-start",

    "& .task-list-header-cell": {
      fontWeight: "bold",
      paddingLeft: theme.spacing(2),
    },

    "& .task-list-header-cell-separator": {
      borderRight: `2px solid ${theme.palette.divider}`,
      opacity: 1,
      marginLeft: "-2px",
    },
  },
}));

const TaskListHeader = ({ headerHeight, rowWidth, fontFamily, fontSize }) => {
  return (
    <TaskListWrapper
      sx={{
        fontFamily: fontFamily,
        fontSize: fontSize,
      }}
    >
      <Box
        sx={{
          height: headerHeight - 2,
          minWidth: rowWidth,
        }}
        className="task-list-header"
      >
        <Typography noWrap className="task-list-header-cell">
          Tên
        </Typography>
      </Box>

      <Box
        sx={{
          height: headerHeight - 2,
          minWidth: "80px",
        }}
        className="task-list-header"
      >
        <Box
          sx={{
            height: headerHeight * 0.5,
          }}
          className="task-list-header-cell-separator"
        />
        <Typography noWrap className="task-list-header-cell">
          Assign
        </Typography>
      </Box>

      <Box
        sx={{
          height: headerHeight - 2,
          minWidth: rowWidth,
        }}
        className="task-list-header"
      >
        <Box
          sx={{
            height: headerHeight * 0.5,
          }}
          className="task-list-header-cell-separator"
        />
        <Typography noWrap className="task-list-header-cell">
          Tiến độ
        </Typography>
      </Box>
    </TaskListWrapper>
  );
};

TaskListHeader.propTypes = {
  headerHeight: PropTypes.number.isRequired,
  rowWidth: PropTypes.number.isRequired,
  fontFamily: PropTypes.string.isRequired,
  fontSize: PropTypes.number.isRequired,
};

export { TaskListHeader };
