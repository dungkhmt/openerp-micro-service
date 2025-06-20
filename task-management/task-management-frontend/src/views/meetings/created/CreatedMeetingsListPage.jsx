import { Icon } from "@iconify/react";
import {
  Box,
  Button,
  Grid,
  MenuItem,
  Select,
  Tooltip,
  Typography,
} from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import dayjs from "dayjs";
import { useEffect, useState } from "react";
import toast from "react-hot-toast";
import { Link, useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { createMeetingPlan } from "../../../store/created-meetings";
import { TaskStatus } from "../../../components/task/status";
import { usePreventOverflow } from "../../../hooks/usePreventOverflow";
import { getDeadlineColor } from "../../../utils/color.util";
import { getTimeUntilDeadline } from "../../../utils/date.util";
import MeetingPlanDialog from "../../../components/meetings/MeetingPlanDialog";
import PropTypes from "prop-types";
import SearchField from "../../../components/mui/search/SearchField";

const columns = [
  {
    field: "name",
    headerName: "Tên",
    flex: 0.25,
    filterable: false,
    renderCell: ({ row }) => (
      <Tooltip title={row.name}>
        <Typography
          variant="subtitle2"
          component={Link}
          to={`/meetings/created-meetings/${row.id}`}
          sx={{
            color: "text.primary",
            fontWeight: 650,
            fontSize: "1rem",
            textDecoration: "none",
            "&:hover": { textDecoration: "underline", color: "primary.main" },
          }}
        >
          {row.name}
        </Typography>
      </Tooltip>
    ),
  },
  {
    field: "description",
    headerName: "Mô tả",
    flex: 0.25,
    filterable: false,
    display: "flex",
    renderCell: ({ row }) => (
      <Typography variant="body2" sx={{ color: "text.primary" }}>
        {row.description || "-"}
      </Typography>
    ),
  },
  {
    field: "location",
    headerName: "Địa điểm",
    flex: 0.15,
    filterable: false,
    display: "flex",
    renderCell: ({ row }) => (
      <Typography variant="body2" sx={{ color: "text.primary" }}>
        {row.location || "-"}
      </Typography>
    ),
  },
  {
    field: "status",
    headerName: "Trạng thái",
    flex: 0.15,
    align: "center",
    headerAlign: "center",
    sortable: false,
    filterable: false,
    renderCell: ({ row }) => {
      const status = row.status;
      return status ? (
        <TaskStatus status={status} />
      ) : (
        <Typography variant="body2" sx={{ color: "text.primary" }}>
          -
        </Typography>
      );
    },
  },
  {
    field: "registrationDeadline",
    headerName: "Hạn đăng ký",
    display: "flex",
    flex: 0.15,
    minWidth: 150,
    align: "center",
    headerAlign: "center",
    filterable: false,
    renderCell: ({ row }) => (
      <Tooltip title={getTimeUntilDeadline(row?.registrationDeadline)} arrow>
        <Typography
          variant="body2"
          sx={{
            color: getDeadlineColor(row?.registrationDeadline),
          }}
        >
          {dayjs(row?.registrationDeadline).format("DD/MM/YYYY, HH:mm") || "-"}
        </Typography>
      </Tooltip>
    ),
  },
  {
    field: "createdStamp",
    headerName: "Ngày tạo",
    display: "flex",
    flex: 0.1,
    minWidth: 100,
    filterable: false,
    renderCell: ({ row }) => (
      <Typography variant="body2" sx={{ color: "text.primary" }}>
        {dayjs(row.createdStamp).format("DD/MM/YYYY") ?? ""}
      </Typography>
    ),
  },
];

const statusCategoryOptions = [
  { value: "upcoming", label: "Sắp diễn ra" },
  { value: "over", label: "Đã kết thúc" },
];

const CreatedMeetingsListPage = ({
  search,
  setSearch,
  onPaginationModelChange,
  onSortModelChange,
  statusCategory,
  onStatusCategoryChange,
}) => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { plansCache, totalCount, pagination, fetchLoading } = useSelector(
    (state) => state.createdMeetings
  );
  const [open, setOpen] = useState(false);
  const { ref, updateHeight } = usePreventOverflow();

  const onSubmit = async (data) => {
    try {
      const payload = { ...data, statusId: "PLAN_DRAFT" };
      const res = await dispatch(createMeetingPlan(payload)).unwrap();
      toast.success("Thêm cuộc họp thành công!");
      navigate(`/meetings/created-meetings/${res.id}`);
    } catch (error) {
      console.error(error);
    }
  };

  useEffect(() => {
    updateHeight(10);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [window.innerHeight]);

  return (
    <Box sx={{ pr: 2}}>
      <Box
        sx={{
          mb: 2,
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
        }}
      >
        <Typography variant="h5">
          <Typography
            component="span"
            variant="h5"
            color="text.secondary"
            sx={{ fontWeight: 600 }}
          >
            {totalCount}
          </Typography>{" "}
          cuộc họp đã tạo
        </Typography>

        <Button
          variant="contained"
          color="primary"
          onClick={() => setOpen(true)}
          startIcon={<Icon icon="stash:plus-solid" />}
          sx={{ textTransform: "capitalize" }}
        >
          Thêm cuộc họp
        </Button>
      </Box>

      <Grid container spacing={2} sx={{ mb: 2, alignItems: "center" }}>
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

      <Box ref={ref}>
        <DataGrid
          disableRowSelectionOnClick
          rows={plansCache[pagination.page] ?? []}
          columns={columns}
          loading={fetchLoading}
          rowCount={totalCount}
          pageSizeOptions={[10, 25, 50]}
          pagination
          paginationMode="server"
          paginationModel={{
            page: pagination.page,
            pageSize: pagination.size,
          }}
          onPaginationModelChange={onPaginationModelChange}
          onSortModelChange={onSortModelChange}
          getRowId={(row) => row.id}
          localeText={{
            noRowsLabel: "Không có dữ liệu",
          }}
        />
      </Box>

      <MeetingPlanDialog
        onSubmit={onSubmit}
        openDialog={open}
        onClose={() => setOpen(false)}
      />
    </Box>
  );
};

CreatedMeetingsListPage.propTypes = {
  search: PropTypes.string.isRequired,
  setSearch: PropTypes.func.isRequired,
  onPaginationModelChange: PropTypes.func.isRequired,
  onSortModelChange: PropTypes.func.isRequired,
  statusCategory: PropTypes.string.isRequired,
  onStatusCategoryChange: PropTypes.func.isRequired,
};

export default CreatedMeetingsListPage;
