import { Box, Tooltip, Typography, IconButton, TextField } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import { useCallback, useEffect, useState } from "react";
import toast from "react-hot-toast";
import { Link } from "react-router-dom";
import { useDebounce } from "../../hooks/useDebounce";
import { usePreventOverflow } from "../../hooks/usePreventOverflow";
import { ProjectService } from "../../services/api/project.service";
import { Icon } from "@iconify/react";

/**
 * @type {import("@mui/x-data-grid").GridColDef[]}
 */
const columns = [
  {
    flex: 0.2,
    field: "code",
    headerName: "Mã dự án",
    renderCell: (params) => (
      <Typography variant="body2" sx={{ color: "text.primary" }}>
        {params.row.code}
      </Typography>
    ),
    description: "Mã dự án là duy nhất và không thể thay đổi",
    display: "flex",
  },
  {
    flex: 0.4,
    field: "name",
    headerName: "Tên dự án",
    renderCell: (params) => (
      <Typography
        variant="body2"
        sx={{
          color: "text.primary",
          textDecoration: "none",
          "&:hover": { textDecoration: "underline" },
        }}
        component={Link}
        to={`/project/${params.row.id}/overview`}
      >
        <Tooltip title={params.row.name}>
          <b>{params.row.name}</b>
        </Tooltip>
      </Typography>
    ),
    display: "flex",
  },
  {
    flex: 0.1,
    field: "role",
    headerName: "Vai trò",
    align: "center",
    headerAlign: "center",
    renderCell: (params) => (
      <Typography variant="body2" sx={{ color: "text.primary" }}>
        {params.row.role}
      </Typography>
    ),
    display: "flex",
  },
];

const DEFAULT_SORT_MODE = "desc";
const DEFAULT_SORT_COLUMN = "createdStamp";

const TabUserProjects = (user) => {
  const { firstName, lastName, id } = user.user;
  const fullName =
    firstName || lastName ? `${firstName ?? ""} ${lastName ?? ""}` : " - ";

  const [total, setTotal] = useState(0);
  const [sort, setSort] = useState(DEFAULT_SORT_MODE);
  const [sortColumn, setSortColumn] = useState(DEFAULT_SORT_COLUMN);
  const [rows, setRows] = useState([]);
  const [searchValue, setSearchValue] = useState("");
  const [paginationModel, setPaginationModel] = useState({
    page: 0,
    pageSize: 10,
  });
  const [loading, setLoading] = useState(true);
  const searchDebounce = useDebounce(searchValue, 100);

  const { ref, updateHeight } = usePreventOverflow();

  const handleSortModel = (newModel) => {
    if (newModel.length) {
      setSort(newModel[0].sort);
      setSortColumn(newModel[0].field);
    } else {
      setSort(DEFAULT_SORT_COLUMN);
      setSortColumn(DEFAULT_SORT_COLUMN);
    }
  };

  const fetchProjects = useCallback(
    async (sort, query, column) => {
      setLoading(true);
      const encodedQuery = encodeURIComponent(query).replace(/%20/g, "%1F"); // replace the ' ' by '%1F' to avoid the error when searching
      try {
        const projectPagination = await ProjectService.getProjectsForUser(id, {
          page: paginationModel.page,
          size: paginationModel.pageSize,
          search: encodedQuery
            ? `code:*${encodedQuery}* OR name:*${encodedQuery}*`
            : "",
          sort: `${column},${sort}`,
        });
        setRows(projectPagination.data);
        setTotal(projectPagination.totalElements);
      } catch (error) {
        console.error(error);
        toast.error("Lỗi không thể lấy dữ liệu dự án");
      } finally {
        setLoading(false);
      }
    },
    [id, paginationModel]
  );

  useEffect(() => {
    setSearchValue("");
    setSort(DEFAULT_SORT_MODE);
    setSortColumn(DEFAULT_SORT_COLUMN);
    setPaginationModel({
      page: 0,
      pageSize: 10,
    });
  }, [id]);

  useEffect(() => {
    fetchProjects(sort, searchDebounce, sortColumn);
  }, [fetchProjects, searchDebounce, sort, sortColumn]);

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
            value={searchValue}
            sx={{
              "& .MuiInputBase-root": {
                height: "34px",
                fontSize: "14px",
                borderRadius:"20px"
              },
            }}
            placeholder="Search Project"
            onChange={(e) => setSearchValue(e.target.value)}
            InputProps={{
              endAdornment: (
                <IconButton
                  size="small"
                  title="Clear"
                  aria-label="Clear"
                  onClick={() => setSearchValue("")}
                  sx={{ padding: 0, marginRight: "-4px" }} 
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
          pagination
          rows={rows}
          rowCount={total}
          columns={columns}
          sortingMode="server"
          paginationMode="server"
          pageSizeOptions={[10, 20, 50]}
          onSortModelChange={handleSortModel}
          onPaginationModelChange={setPaginationModel}
          paginationModel={paginationModel}
          loading={loading}
          disableRowSelectionOnClick
        />
      </Box>
    </>
  );
};

export { TabUserProjects };
