import {
  Box,
  Grid,
  MenuItem,
  Select,
  Typography,
  Card,
  CardContent,
  Pagination,
  Divider,
  Tooltip,
} from "@mui/material";
import dayjs from "dayjs";
import { useEffect } from "react";
import { Link } from "react-router-dom";
import { useSelector } from "react-redux";
import { TaskStatus } from "../../../components/task/status";
import { usePreventOverflow } from "../../../hooks/usePreventOverflow";
import PropTypes from "prop-types";
import SearchField from "../../../components/mui/search/SearchField";
import { GroupedAvatars } from "../../../components/common/avatar/GroupedAvatars";
import { Icon } from "@iconify/react";
import { getDeadlineColor } from "../../../utils/color.util";
import { UserAvatar } from "../../../components/common/avatar/UserAvatar";
import { CircularProgressLoading } from "../../../components/common/loading/CircularProgressLoading";
import { isRegistrationMode } from "../../../utils/meetingUtils";

const statusCategoryOptions = [
  { value: "upcoming", label: "Sắp diễn ra" },
  { value: "over", label: "Đã kết thúc" },
];

const JoinedMeetingsListPage = ({
  search,
  setSearch,
  onPaginationModelChange,
  onStatusCategoryChange,
  statusCategory,
}) => {
  const { plansCache, totalCount, pagination, fetchLoading } = useSelector(
    (state) => state.joinedMeetings
  );
  const { ref, updateMaxHeight } = usePreventOverflow();

  const totalPages = Math.ceil(totalCount / pagination.size);

  useEffect(() => {
    updateMaxHeight();
    window.addEventListener("resize", updateMaxHeight);
    return () => window.removeEventListener("resize", updateMaxHeight);
  }, [updateMaxHeight]);

  const handlePageChange = (event, newPage) => {
    onPaginationModelChange({
      page: newPage - 1,
      pageSize: pagination.size,
    });
  };

  return (
    <Box>
      <Box
        sx={{
          mb: 2,
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
        }}
      >
        <Typography variant="h5">
          <Typography component="span" variant="h5" color="text.secondary">
            {totalCount}
          </Typography>{" "}
          cuộc họp đã tham gia
        </Typography>
      </Box>

      <Grid container spacing={2} sx={{ mb: 2, pr: 5, alignItems: "center" }}>
        <Grid item xs={8}>
          <SearchField
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            fullWidth
            placeholder="Tìm kiếm kế hoạch..."
            onClear={() => setSearch("")}
          />
        </Grid>
        <Grid item xs={4}>
          <Select
            value={statusCategory}
            onChange={(e) => onStatusCategoryChange(e.target.value)}
            size="small"
            fullWidth
          >
            {statusCategoryOptions.map((category) => (
              <MenuItem key={category.value} value={category.value}>
                <Typography variant="body1" sx={{ fontWeight: 500 }}>
                  {category.label}
                </Typography>
              </MenuItem>
            ))}
          </Select>
        </Grid>
      </Grid>

      <Box ref={ref} sx={{ overflowY: "auto" }}>
        {fetchLoading && <CircularProgressLoading />}
        <Grid container spacing={2}>
          {(plansCache[pagination.page] ?? []).map((row) => (
            <Grid item xs={12} key={row.id}>
              <Card
                sx={{
                  mr: 2,
                  borderRadius: 4,
                  boxShadow: "0 2px 8px rgba(0, 0, 0, 0.05)",
                }}
              >
                <CardContent
                  sx={{ display: "flex", alignItems: "center", gap: 2 }}
                >
                  <Box sx={{ flex: 1 }}>
                    <Box
                      sx={{
                        display: "flex",
                        justifyContent: "space-between",
                        alignItems: "center",
                        mb: 2,
                      }}
                    >
                      <Tooltip title={row.name} placement="top" arrow>
                        <Typography
                          variant="h6"
                          sx={{
                            fontWeight: 600,
                            textDecoration: "none",
                            textTransform: "capitalize",
                            display: "-webkit-box",
                            WebkitLineClamp: 1,
                            WebkitBoxOrient: "vertical",
                            overflow: "hidden",
                            mr: 3,
                          }}
                          component={Link}
                          to={`/meetings/joined-meetings/${row.id}`}
                        >
                          {row.name}
                        </Typography>
                      </Tooltip>

                      <TaskStatus
                        status={row.status}
                        sx={{
                          fontWeight: 600,
                          fontSize: "0.9rem",
                          px: 2,
                          py: 4,
                        }}
                      />
                    </Box>
                    <Box>
                      {!isRegistrationMode(row.statusId) &&
                        row.assignedSession && (
                          <Box
                            sx={{
                              display: "flex",
                              flexDirection: "row",
                              gap: 2,
                              color: getDeadlineColor(
                                row.assignedSession.startTime
                              ),
                            }}
                          >
                            <Box
                              sx={{
                                display: "flex",
                                alignItems: "center",
                                gap: 1,
                              }}
                            >
                              <Icon icon="lets-icons:date-today" />
                              <Typography
                                variant="body2"
                                sx={{
                                  fontWeight: 600,
                                  color: getDeadlineColor(
                                    row.assignedSession.startTime
                                  ),
                                }}
                              >
                                {dayjs(row.assignedSession.startTime).format(
                                  "dddd, DD MMM YYYY"
                                )}
                              </Typography>
                            </Box>

                            <Box
                              sx={{
                                display: "flex",
                                alignItems: "center",
                                gap: 1,
                              }}
                            >
                              <Icon icon="mingcute:time-line" />
                              <Typography
                                variant="body2"
                                sx={{
                                  fontWeight: 600,
                                  color: getDeadlineColor(
                                    row.assignedSession.startTime
                                  ),
                                }}
                              >
                                {dayjs(row.assignedSession.startTime).format(
                                  "HH:mm A"
                                )}{" "}
                                -{" "}
                                {dayjs(row.assignedSession.endTime).format(
                                  "HH:mm A"
                                )}
                              </Typography>
                            </Box>
                          </Box>
                        )}
                    </Box>

                    <Divider sx={{ my: 2 }} />
                    <Box
                      sx={{
                        mb: 2,
                        maxHeight: "2.8em",
                        overflow: "hidden",
                        wordBreak: "break-word",
                        whiteSpace: "pre-line",
                      }}
                    >
                      <Typography
                        variant="body1"
                        gutterBottom
                        sx={{
                          fontSize: "0.95rem",
                          color: "text.primary",
                          display: "-webkit-box",
                          WebkitLineClamp: 2,
                          WebkitBoxOrient: "vertical",
                          overflow: "hidden",
                        }}
                      >
                        {row.description || (
                          <Typography
                            variant="body2"
                            sx={{ color: "text.secondary" }}
                          >
                            Không có mô tả.
                          </Typography>
                        )}
                      </Typography>
                    </Box>
                    <Box
                      sx={{
                        display: "flex",
                        justifyContent: "space-between",
                      }}
                    >
                      <Box
                        sx={{ display: "flex", alignItems: "center", gap: 3 }}
                      >
                        <Box
                          sx={{
                            display: "flex",
                            alignItems: "center",
                            gap: 1,
                            maxWidth: "100%",
                          }}
                        >
                          <Icon icon={"mdi:location"} />
                          {row.location ? (
                            <Tooltip title={row.location} arrow>
                              <Typography
                                variant="body2"
                                color="text.primary"
                                sx={{
                                  whiteSpace: "nowrap",
                                  overflow: "hidden",
                                  textOverflow: "ellipsis",
                                  maxWidth: {
                                    xs: "100px",
                                    sm: "200px",
                                    md: "300px",
                                    lg: "400px",
                                  },
                                }}
                              >
                                {row.location}
                              </Typography>
                            </Tooltip>
                          ) : (
                            <Typography variant="body2" color="text.secondary">
                              Không có địa điểm
                            </Typography>
                          )}
                        </Box>

                        <Box
                          sx={{
                            display: "flex",
                            alignItems: "center",
                            gap: 1,
                          }}
                        >
                          <Icon icon={"mage:user-fill"} />
                          <Typography variant="body2" color="text.primary">
                            Tạo bởi{" "}
                          </Typography>
                          <UserAvatar user={row.creator} />
                        </Box>
                      </Box>
                      <GroupedAvatars users={row.members || []} />
                    </Box>
                  </Box>

                  <Box
                    sx={{ display: "flex", alignItems: "center", gap: 1 }}
                  ></Box>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>

        {!fetchLoading && plansCache[pagination.page]?.length === 0 && (
          <Box
            sx={{
              height: "100%",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
            }}
          >
            <Typography variant="body1" sx={{ textAlign: "center", my: 2 }}>
              Không có dữ liệu
            </Typography>
          </Box>
        )}

        {totalCount > 0 && (
          <Box
            sx={{
              position: "sticky",
              bottom: 0,
              width: "100%",
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
              py: 2,
              backgroundColor: "background.default",
              zIndex: 10,
            }}
          >
            <Pagination
              count={totalPages}
              page={pagination.page + 1}
              onChange={handlePageChange}
              color="primary"
              siblingCount={2}
              boundaryCount={1}
              showFirstButton={false}
              showLastButton={false}
            />
          </Box>
        )}
      </Box>
    </Box>
  );
};

JoinedMeetingsListPage.propTypes = {
  search: PropTypes.string.isRequired,
  setSearch: PropTypes.func.isRequired,
  onPaginationModelChange: PropTypes.func.isRequired,
  onStatusCategoryChange: PropTypes.func.isRequired,
  statusCategory: PropTypes.string.isRequired,
};

export default JoinedMeetingsListPage;
