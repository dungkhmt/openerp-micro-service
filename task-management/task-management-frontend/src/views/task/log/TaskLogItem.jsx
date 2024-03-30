import TimelineConnector from "@mui/lab/TimelineConnector";
import TimelineContent from "@mui/lab/TimelineContent";
import TimelineDot from "@mui/lab/TimelineDot";
import TimelineItem from "@mui/lab/TimelineItem";
import TimelineSeparator from "@mui/lab/TimelineSeparator";
import {
  Box,
  Card,
  Divider,
  Grid,
  List,
  ListItem,
  Tooltip,
  Typography,
  styled,
} from "@mui/material";
import dayjs from "dayjs";
import PropTypes from "prop-types";

import MDEditor from "@uiw/react-md-editor";
import { useMemo } from "react";
import { UserAvatar } from "../../../components/common/avatar/UserAvatar";
import { getLogItemColor } from "../../../utils/color.util";
import { getDiffDateWithCurrent } from "../../../utils/date.util";
import { TaskLogItemDetail } from "./TaskLogItemDetail";

const TextHighlight = styled(Typography)(({ theme }) => ({
  color: theme.palette.primary.main,
  fontWeight: 600,
  cursor: "pointer",
  fontSize: "0.9rem",
}));

const TaskLogItem = ({ item }) => {
  const creator = item.creator;
  const fullName = `${item.creator.firstName} ${item.creator.lastName}`;
  const dotColor = useMemo(() => getLogItemColor(item.details), [item.details]);

  return (
    <TimelineItem>
      <TimelineSeparator>
        <TimelineDot color={dotColor} />
        <TimelineConnector />
      </TimelineSeparator>

      <TimelineContent>
        <Grid container spacing={4}>
          <Grid
            item
            xs={12}
            md={0.75}
            sx={{ display: "flex", justifyContent: "center" }}
          >
            <UserAvatar user={creator} width={40} height={40} />
          </Grid>
          <Grid item xs={12} md={11}>
            <Card>
              <Box
                sx={{
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "center",
                  p: 2,
                }}
              >
                <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
                  <Typography
                    component="span"
                    sx={{ fontSize: "0.8rem", fontStyle: "italic" }}
                  >
                    Cập nhật bởi
                  </Typography>
                  <Tooltip title={fullName}>
                    <TextHighlight>{fullName}</TextHighlight>
                  </Tooltip>
                </Box>
                <Tooltip
                  title={dayjs(item.createdAt).format("DD/MM/YYYY HH:mm:ss")}
                >
                  <TextHighlight component="span">
                    {getDiffDateWithCurrent(item.createdAt).join(" ")}
                    <span>{" trước"}</span>
                  </TextHighlight>
                </Tooltip>
              </Box>

              <Divider />

              <Box>
                {item.details?.length > 0 && (
                  <List sx={{ pl: 12 }}>
                    {item.details?.map((detail) => (
                      <ListItem
                        key={detail.id}
                        sx={{
                          p: 0,
                          position: "relative",
                          "&::before": {
                            content: '""',
                            position: "absolute",
                            left: -8,
                            top: "50%",
                            transform: "translateY(-50%)",
                            width: "4px",
                            height: "4px",
                            borderRadius: "50%",
                            backgroundColor: "text.primary",
                          },
                        }}
                      >
                        <TaskLogItemDetail detail={detail} />
                      </ListItem>
                    ))}
                  </List>
                )}

                {item.comment && (
                  <Box sx={{ p: 4 }}>
                    <MDEditor.Markdown source={item.comment} />
                  </Box>
                )}
              </Box>
            </Card>
          </Grid>
        </Grid>
      </TimelineContent>
    </TimelineItem>
  );
};

TaskLogItem.propTypes = {
  item: PropTypes.object.isRequired,
};

export { TaskLogItem };
