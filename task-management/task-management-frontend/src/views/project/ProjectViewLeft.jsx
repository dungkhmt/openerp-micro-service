import { Icon } from "@iconify/react";
import {
  Box,
  Button,
  Card,
  CardActions,
  CardContent,
  Divider,
  Typography,
} from "@mui/material";
import { useSelector } from "react-redux";
import MuiAvatar from "@mui/material/Avatar";
import dayjs from "dayjs";
import { memo, useMemo } from "react";
import { useNavigate } from "react-router";
import CustomAvatar from "../../components/mui/avatar/CustomAvatar";

const ProjectViewLeft = () => {
  const navigate = useNavigate();
  const { project, members } = useSelector((state) => state.project);

  const randomUrl = useMemo(
    () => `/static/images/project${Math.floor(Math.random() * 5) + 1}.png`,
    []
  );

  return (
    <Card>
      <CardContent
        sx={{
          pt: 4,
          display: "flex",
          alignItems: "center",
          flexDirection: "column",
        }}
      >
        <MuiAvatar
          src={randomUrl}
          variant="rounded"
          sx={{
            width: 120,
            height: 120,
            fontWeight: 600,
            mb: 4,
            fontSize: "3rem",
          }}
        />
        <Typography variant="h6">{project.name}</Typography>
      </CardContent>

      <CardContent sx={{ my: 1 }}>
        <Box
          sx={{
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            gap: 5,
          }}
        >
          <Box sx={{ display: "flex", alignItems: "center" }}>
            <CustomAvatar
              skin="light"
              variant="rounded"
              color="primary"
              sx={{ mr: 2, width: 44, height: 44 }}
            >
              <Icon icon="mdi:check" />
            </CustomAvatar>
            <div>
              <Typography variant="h6">{project.taskCount ?? 0}</Typography>
              <Typography variant="body2">Nhiệm vụ</Typography>
            </div>
          </Box>
          <Box sx={{ display: "flex", alignItems: "center" }}>
            <CustomAvatar
              sx={{ mr: 2, width: 44, height: 44 }}
              skin="light"
              variant="rounded"
              color="primary"
            >
              <Icon icon="mdi:account-multiple" />
            </CustomAvatar>
            <div>
              <Typography variant="h6">
                {members?.length?.toLocaleString()}
              </Typography>
              <Typography variant="body2">Thành viên</Typography>
            </div>
          </Box>
        </Box>
      </CardContent>

      <CardContent>
        <Typography variant="h6">Chi tiết</Typography>
        <Divider sx={{ my: (theme) => `${theme.spacing(4)} !important` }} />
        <Box sx={{ pb: 1 }}>
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
                : "Không xác định"}
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
      <CardActions sx={{ display: "flex", justifyContent: "center" }}>
        <Button
          variant="contained"
          sx={{ mr: 2 }}
          onClick={() => {
            navigate(`/project/${project.id}/edit`);
          }}
        >
          Edit
        </Button>
      </CardActions>
    </Card>
  );
};

export default memo(ProjectViewLeft);
