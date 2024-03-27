import { Card, CardHeader, Tooltip, Typography } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import { useCallback, useEffect, useState } from "react";
import toast from "react-hot-toast";
import TableToolbar from "../../components/table/Toolbar";
import { ProjectService } from "../../services/api/project.service";
import { useDebounce } from "../../hooks/useDebounce";
import { Link } from "react-router-dom";
import CustomAvatar from "../../components/mui/avatar/CustomAvatar";
import { getRandomColorSkin } from "../../utils/color.util";

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
  },
  {
    flex: 0.15,
    field: "creator",
    headerName: "Người tạo",
    align: "center",
    headerAlign: "center",
    sortable: false,
    fiterable: false,
    renderCell: ({ row }) =>
      row.creator ? (
        <Tooltip
          title={`${row.creator.firstName ?? ""} ${row.creator.lastName ?? ""}`}
        >
          <CustomAvatar
            skin="light"
            color={getRandomColorSkin(row.creator.id)}
            sx={{
              width: 30,
              height: 30,
              fontSize: ".875rem",
            }}
          >
            {`${row.creator?.firstName?.charAt(0) ?? ""}${
              row?.creator?.lastName?.charAt(0) ?? ""
            }`}
          </CustomAvatar>
        </Tooltip>
      ) : (
        <Typography variant="body2" sx={{ color: "text.primary" }}>
          Không xác định
        </Typography>
      ),
  },
  {
    flex: 0.2,
    headerName: "Ngày tạo",
    align: "center",
    headerAlign: "center",
    field: "createdStamp",
    filterable: false,
    renderCell: (params) => (
      <Typography variant="body2" sx={{ color: "text.primary" }}>
        {new Date(params.row.createdStamp).toLocaleDateString()}
      </Typography>
    ),
  },
];

const DEFAULT_SORT_MODE = "desc";
const DEFAULT_SORT_COLUMN = "createdStamp";

const Projects = () => {
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
  const searchDebounce = useDebounce(searchValue, 1000);

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
        const projectPagination = await ProjectService.getProjects({
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
    [paginationModel]
  );

  useEffect(() => {
    fetchProjects(sort, searchDebounce, sortColumn);
  }, [fetchProjects, searchDebounce, sort, sortColumn]);

  return (
    <Card sx={{ height: "85vh", boxShadow: (theme) => theme.shadows[1] }}>
      <CardHeader title="Danh sách dự án" />
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
        slots={{ toolbar: TableToolbar }}
        loading={loading}
        sx={{ height: "88%" }}
        slotProps={{
          baseButton: {
            variant: "outlined",
          },
          toolbar: {
            value: searchValue,
            clearSearch: () => setSearchValue(""),
            onChange: (event) => setSearchValue(event.target.value),
          },
        }}
      />
    </Card>
  );
};

export default Projects;
