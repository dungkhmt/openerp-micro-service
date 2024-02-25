import LocalFireDepartmentIcon from "@mui/icons-material/LocalFireDepartment";
import { Tooltip, Typography } from "@mui/material";
import { request } from "../../api";
import CategoryElement from "../common/CategoryElement";
import StandardTable from "../table/StandardTable";
import { LimitString } from "../utils/helpers";
import { Link } from "react-router-dom";

const ListAssignedTasks = () => {
  const columns = [
    {
      title: "Tag",
      align: "left",
      render: (rowData) => (
        <CategoryElement
          categoryId={rowData.taskCategory.categoryId}
          value={rowData.taskCategory.categoryName}
        />
      ),
    },
    {
      title: "Tiêu đề",
      align: "left",
      render: (rowData) => (
        <Link to={`/tasks/${rowData.id}`}>
          <Tooltip title={rowData.name}>
            <b>{LimitString(50, rowData.name)}</b>
          </Tooltip>
        </Link>
      ),
    },
    {
      title: "Dự án",
      align: "left",
      render: (rowData) => (
        <Tooltip title={rowData.project.name}>
          <b>{LimitString(50, rowData.project.name)}</b>
        </Tooltip>
      ),
    },
    {
      title: "Thời hạn",
      align: "center",
      render: (rowData) => (
        <>
          <div>{rowData.dueDate}</div>
          {rowData.outOfDate && <LocalFireDepartmentIcon color="error" />}
        </>
      ),
    },
    {
      title: "Trạng thái",
      align: "center",
      render: (rowData) => (
        <Typography variant="caption" sx={{ color: "red" }}>
          {rowData.timeRemaining}
        </Typography>
      ),
    },
  ];

  return (
    <StandardTable
      title="Danh sách các nhiệm vụ được giao"
      hideCommandBar
      columns={columns}
      onRowClick={(_, rowData) => {
        navigate(`/tasks/${rowData.id}`);
      }}
      data={({ page, pageSize, search }) => {
        search = search.replace(/ /g, "%1F");
        return new Promise((resolve, reject) => {
          request(
            "get",
            `/assigned-tasks-user-login?page=${page}&size=${pageSize}${
              search === ""
                ? ""
                : `&search=name:*${search}* OR description:*${search}*`
            }`,
            (res) => {
              resolve({
                data: res.data.content,
                page,
                totalCount: res.data.totalElements,
              });
            },
            (err) => {
              console.log(err);
              reject(err);
            }
          );
        });
      }}
      options={{
        selection: false,
        pageSize: 5,
      }}
    />
  );
};

export default ListAssignedTasks;
