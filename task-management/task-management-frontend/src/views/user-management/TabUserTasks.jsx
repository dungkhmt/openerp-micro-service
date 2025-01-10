import { Icon } from "@iconify/react";
import { Box, IconButton, TextField, Tooltip, Typography } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import { useCallback, useEffect, useState } from "react";
import toast from "react-hot-toast";
import { Link } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import {
  setSort,
  resetSort,
  fetchAssignedTasksForUser,
  clearCache,
  setPagination,
  resetPagination,
  setSearch as setSearchAction,
  resetTasksData,
} from "../../store/user-management";
import { TaskStatus } from "../../components/task/status";
import { useDebounce } from "../../hooks/useDebounce";
import { usePreventOverflow } from "../../hooks/usePreventOverflow";

const TabUserTasks = (user) => {
  const { firstName, lastName, id } = user.user;
  const fullName =
    firstName || lastName ? `${firstName ?? ""} ${lastName ?? ""}` : " - ";
  const {
    tasksCache,
    totalCount,
    search: searchStore,
    pagination,
    sort,
    fetchLoading,
  } = useSelector((state) => state.userManagement);
  const { statuses } = useSelector((state) => state.status);
  const dispatch = useDispatch();

  const [rows, setRows] = useState(tasksCache[pagination.page] ?? []);
  const [search, setSearch] = useState("");
  const searchDebounce = useDebounce(search, 100);

  const { ref, updateHeight } = usePreventOverflow();

  const [isFirstFetch, setIsFirstFetch] = useState(true);

  /**
   * @type {import("@mui/x-data-grid").GridColDef[]}
   */
  const columns = [
    {
      flex: 0.3,
      field: "name",
      headerName: "Tên",
      filterable: false,
      renderCell: ({ row }) => (
        <Tooltip title={row.name}>
          <Typography
            noWrap
            variant="subtitle2"
            component={Link}
            to={`/project/${row.project.id}/task/${row.id}`}
            sx={{
              color: "text.primary",
              fontWeight: 650,
              textDecoration: "none",
              "&:hover": {
                textDecoration: "underline",
                color: "primary.main",
              },
            }}
          >
            {row.name}
          </Typography>
        </Tooltip>
      ),
      display: "flex",
    },
    {
      flex: 0.2,
      field: "project",
      headerName: "Dự án",
      sortable: false,
      filterable: false,
      renderCell: ({ row }) =>
        row.project && (
          <Tooltip title={row.project.name}>
            <Typography
              noWrap
              variant="body2"
              sx={{
                color: "text.primary",
                fontWeight: 600,
                textDecoration: "none",
                "&:hover": {
                  textDecoration: "underline",
                  color: "primary.light",
                },
              }}
              component={Link}
              to={`/project/${row.project.id}/overview`}
            >
              {row.project.name}
            </Typography>
          </Tooltip>
        ),
      display: "flex",
    },
    {
      flex: 0.1,
      field: "status",
      headerName: "Trạng thái",
      align: "center",
      headerAlign: "center",
      sortable: false,
      renderCell: ({ row }) => {
        const status = statuses?.find((s) => s.statusId === row.statusId);
        return status && <TaskStatus status={status} />;
      },
      display: "flex",
    },
  ];

  const handleSortModel = (newModel) => {
    if (newModel.length > 0) {
      dispatch(setSort({ field: newModel[0].field, sort: newModel[0].sort }));
    } else {
      dispatch(resetSort());
    }
  };

  const handlePaginationModel = (newModel) => {
    if (
      newModel.page === pagination.page &&
      newModel.pageSize !== pagination.size
    ) {
      dispatch(clearCache());
    }
    dispatch(
      setPagination({
        page: newModel.page,
        size: newModel.pageSize,
      })
    );
  };

  const buildQueryString = useCallback(() => {
    const builder = [];
    const encodedSearch = encodeURIComponent(searchStore).replace(
      /%20/g,
      "%1F"
    );
    builder.push(
      encodedSearch
        ? `( name:*${encodedSearch}* OR projectName:*${encodedSearch}* OR description:*${encodedSearch}* )`
        : ""
    );
    return builder.filter((s) => s !== "").join(" AND ");
  }, [searchStore]);

  const getTasks = useCallback(async () => {
    if (tasksCache[pagination.page]) return;
    try {
      await dispatch(
        fetchAssignedTasksForUser({
          id: id,
          filters: {
            ...pagination,
            search: buildQueryString(),
            sort: `${sort.field},${sort.sort}`,
          },
        })
      );
    } catch (error) {
      toast.error("Lỗi khi lấy dữ liệu");
      console.log(error);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [pagination, sort, tasksCache, buildQueryString, dispatch]);

  const onSearch = async () => {
    dispatch(resetPagination());
    dispatch(clearCache());
    dispatch(setSearchAction(searchDebounce));
  };

  useEffect(() => {
    dispatch(resetTasksData());
    setSearch("");
  }, [dispatch, id]);

  useEffect(() => {
    getTasks();
  }, [getTasks]);

  useEffect(() => {
    if (tasksCache[pagination.page]) {
      setRows(tasksCache[pagination.page]);
    }
  }, [tasksCache, pagination.page]);

  useEffect(() => {
    if (isFirstFetch) {
      setIsFirstFetch(false);
    } else {
      onSearch();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [searchDebounce]);

  useEffect(() => {
    updateHeight(10);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [window.innerHeight]);

  return (
    <>
      <Box
        sx={{
          display: "flex",
          justifyContent: "space-between",
          mb: 3,
          px: 2,
        }}
      >
        <Typography variant="h6">{fullName}</Typography>
        <Box
          sx={{
            display: "flex",
            flexWrap: "wrap",
            alignItems: "center",
            gap: 3,
          }}
        >
          <TextField
            size="small"
            value={search}
            sx={{
              "& .MuiInputBase-root": {
                height: "34px",
                fontSize: "14px",
                borderRadius:"20px"
              },
            }}
            placeholder="Search Task"
            onChange={(e) => setSearch(e.target.value)}
            InputProps={{
              endAdornment: (
                <IconButton
                  size="small"
                  title="Clear"
                  aria-label="Clear"
                  onClick={() => setSearch("")}
                >
                  <Icon icon="mdi:close" fontSize={20} />
                </IconButton>
              ),
            }}
          />
        </Box>
      </Box>
      <Box ref={ref}>
        <DataGrid
          rows={rows}
          loading={fetchLoading}
          rowCount={totalCount}
          columns={columns}
          pageSizeOptions={[10, 25, 50]}
          pagination
          paginationMode="server"
          paginationModel={{
            page: pagination.page,
            pageSize: pagination.size,
          }}
          onPaginationModelChange={handlePaginationModel}
          onSortModelChange={handleSortModel}
          localeText={{
            noRowsLabel: "Không có dữ liệu",
          }}
        />
      </Box>
    </>
  );
};

export { TabUserTasks };
