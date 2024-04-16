import { Box, CardContent, Typography } from "@mui/material";
import dayjs from "dayjs";
import PropTypes from "prop-types";
import { forwardRef } from "react";
import { useSelector } from "react-redux";
import { DashboardCard } from "../../../../components/card/DashboardCard";

const ProjectInfo = forwardRef(function ProjectInfo(
  { style, className, onMouseDown, onMouseUp, onTouchEnd, children, ...props },
  ref
) {
  const { project } = useSelector((state) => state.project);

  return (
    <DashboardCard
      title={"Thông tin dự án"}
      {...props}
      style={style}
      className={className}
      onMouseDown={onMouseDown}
      onMouseUp={onMouseUp}
      onTouchEnd={onTouchEnd}
      isDraggable={false}
      isRefreshable={false}
      isExpandable={false}
      ref={ref}
    >
      <CardContent>
        <Box sx={{ py: 3 }}>
          <Box sx={{ display: "flex", mb: 2 }}>
            <Typography sx={{ mr: 2, fontWeight: 600, fontSize: "0.875rem" }}>
              Mã dự án:
            </Typography>
            <Typography variant="body2">{project.code}</Typography>
          </Box>
          <Box sx={{ display: "flex", mb: 2 }}>
            <Typography sx={{ mr: 2, fontWeight: 600, fontSize: "0.875rem" }}>
              Tên dự án:
            </Typography>
            <Typography variant="body2">{project.name}</Typography>
          </Box>
          <Box sx={{ display: "flex", mb: 2 }}>
            <Typography sx={{ mr: 2, fontWeight: 600, fontSize: "0.875rem" }}>
              Người tạo:
            </Typography>
            <Typography variant="body2">
              {project.creator
                ? `${project.creator.firstName} ${project.creator.lastName}`
                : " - "}
            </Typography>
          </Box>
          <Box sx={{ display: "flex", mb: 2 }}>
            <Typography sx={{ mr: 2, fontWeight: 600, fontSize: "0.875rem" }}>
              Thời gian tạo:
            </Typography>
            <Typography variant="body2" sx={{ textTransform: "capitalize" }}>
              {dayjs(project.createdStamp).format("DD/MM/YYYY")}
            </Typography>
          </Box>
        </Box>
      </CardContent>
      {children}
    </DashboardCard>
  );
});

ProjectInfo.propTypes = {
  className: PropTypes.string,
  style: PropTypes.object,
  onMouseDown: PropTypes.func,
  onMouseUp: PropTypes.func,
  onTouchEnd: PropTypes.func,
  children: PropTypes.node,
};

export { ProjectInfo };
