import {
  Box,
  Card,
  CardContent,
  Divider,
  Grid,
  Tooltip,
  Typography,
} from "@mui/material";
import { useSelector } from "react-redux";
import dayjs from "dayjs";
import SessionRegistrationsView from "./SessionRegistrationsView";
import { usePreventOverflow } from "../../../hooks/usePreventOverflow";
import { useEffect } from "react";
import { TaskStatus } from "../../../components/task/status";
import { Icon } from "@iconify/react";
import { UserAvatar } from "../../../components/common/avatar/UserAvatar";
import { CircularProgressLoading } from "../../../components/common/loading/CircularProgressLoading";
import {
  getDiffDateWithCurrent,
  getTimeUntilDeadline,
} from "../../../utils/date.util";
import MeetingMembersView from "../created/MeetingMembersView";
import { getDeadlineColor } from "../../../utils/color.util";
import DescriptionText from "../../../components/common/text/DescriptionText";

const JoinedMeetingPage = () => {
  const { currentPlan } = useSelector((state) => state.meetingPlan);
  const { ref, updateMaxHeight } = usePreventOverflow();

  useEffect(() => {
    updateMaxHeight();
    window.addEventListener("resize", updateMaxHeight);
    return () => window.removeEventListener("resize", updateMaxHeight);
  }, [updateMaxHeight]);

  if (!currentPlan) return <CircularProgressLoading />;

  return (
    <Box ref={ref} sx={{ p: 3, overflowY: "auto" }}>
      <Box sx={{ mb: 5 }}>
        <Grid xs={12} md={12}>
          <Box
            sx={{
              display: "flex",
              flexDirection: "column",
              gap: 2,
              ml: 3,
              pb: { md: 0, xs: 3 },
            }}
          >
            <Typography
              variant="h4"
              sx={{
                textTransform: "capitalize",
                wordBreak: "break-word",
                whiteSpace: "pre-wrap",
              }}
            >
              {currentPlan?.name || "-"}
            </Typography>

            <Box
              sx={{
                display: "flex",
                alignItems: "center",
                flexWrap: "wrap",
                gap: 0.5,
              }}
            >
              <UserAvatar user={currentPlan?.creator} fontSize="1rem" />
              <Typography
                variant="subtitle2"
                sx={{ color: "text.primary", pr: 1, fontSize: "0.875rem" }}
              >
                {` ${currentPlan?.creator?.firstName ?? ""} ${
                  currentPlan?.creator?.lastName ?? ""
                } `}
              </Typography>
              <Typography variant="subtitle2" sx={{ fontSize: "0.875rem" }}>
                đã tạo cách đây
              </Typography>
              <Tooltip
                title={dayjs(currentPlan?.createdStamp).format(
                  "DD/MM/YYYY HH:mm:ss"
                )}
              >
                <Typography
                  sx={{
                    px: 1,
                    color: "primary.main",
                    fontWeight: 600,
                    cursor: "pointer",
                    fontSize: "0.875rem",
                  }}
                >{`${getDiffDateWithCurrent(currentPlan?.createdStamp).join(
                  " "
                )}`}</Typography>
              </Tooltip>
              <Typography variant="subtitle2" sx={{ fontSize: "0.875rem" }}>
                trước.
              </Typography>
            </Box>
          </Box>
        </Grid>
      </Box>

      <Grid container spacing={3}>
        <Grid item xs={12} md={8}>
          <Card
            sx={{
              mb: 3,
              borderRadius: 2,
              boxShadow: "0 2px 8px rgba(0, 0, 0, 0.05)",
            }}
          >
            <CardContent>
              <Typography variant="h6">Mô tả</Typography>
              <DescriptionText text={currentPlan?.description} />
            </CardContent>
          </Card>
          <SessionRegistrationsView />
        </Grid>

        <Grid item xs={12} md={4}>
          <Card
            sx={{
              mb: 3,
              borderRadius: 3,
              boxShadow: "0 2px 8px rgba(0, 0, 0, 0.05)",
            }}
          >
            <CardContent>
              <Box sx={{ mb: 3 }}>
                <Typography
                  sx={{ fontSize: "1rem", fontWeight: 600, mb: 1 }}
                  color="text.primary"
                >
                  Trạng thái
                </Typography>
                <TaskStatus
                  status={currentPlan.status}
                  sx={{
                    textTransform: "capitalize",
                    fontSize: "0.9rem",
                    fontWeight: 600,
                    py: 4,
                    px: 1,
                    ml: 2,
                  }}
                />
              </Box>

              <Box sx={{ display: "flex", justifyContent: "center" }}>
                <Divider sx={{ width: "80%" }} />
              </Box>

              <Box sx={{ mt: 2, mb: 3 }}>
                <Typography
                  sx={{ fontSize: "1rem", fontWeight: 600, mb: 1 }}
                  color="text.primary"
                >
                  Hạn đăng ký
                </Typography>
                <Box
                  sx={{ display: "flex", alignItems: "center", gap: 1, ml: 2 }}
                >
                  <Icon fontSize={16} icon="mingcute:time-line" />
                  <Tooltip
                    title={getTimeUntilDeadline(
                      currentPlan?.registrationDeadline
                    )}
                    arrow
                  >
                    <Typography
                      sx={{
                        fontSize: "0.9rem",
                        fontWeight: 500,
                        color: getDeadlineColor(
                          currentPlan?.registrationDeadline
                        ),
                        cursor: "pointer",
                      }}
                    >
                      {dayjs(currentPlan?.registrationDeadline).format(
                        "dddd, DD MMM YYYY, HH:mm A"
                      ) || "-"}
                    </Typography>
                  </Tooltip>
                </Box>
              </Box>

              <Box sx={{ display: "flex", justifyContent: "center" }}>
                <Divider sx={{ width: "80%" }} />
              </Box>

              <Box sx={{ mt: 2 }}>
                <Typography
                  sx={{ fontSize: "1rem", fontWeight: 600, mb: 1 }}
                  color="text.primary"
                >
                  Địa điểm
                </Typography>
                <Box
                  sx={{
                    display: "flex",
                    alignItems: "flex-start",
                    gap: 1,
                    ml: 2,
                  }}
                >
                  <Box
                    sx={{
                      flexShrink: 0,
                      display: "block",
                    }}
                  >
                    <Icon fontSize={16} icon="mdi:location" />
                  </Box>
                  <Typography
                    sx={{
                      fontSize: "0.9rem",
                      fontWeight: 500,
                      color: "text.primary",
                      wordBreak: "break-word",
                      overflowWrap: "break-word",
                    }}
                  >
                    {currentPlan?.location || "-"}
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>

          <MeetingMembersView />
        </Grid>
      </Grid>
    </Box>
  );
};

export default JoinedMeetingPage;
