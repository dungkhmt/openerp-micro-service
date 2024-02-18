import { Tooltip } from "@mui/material";
import { useNavigate } from "react-router";
import { request } from "../../api";
import StandardTable from "../table/StandardTable";
import { LimitString } from "../utils/helpers";

export default function ListProject() {
  const navigate = useNavigate();

  const columns = [
    {
      field: "code",
      title: "Mã dự án",
      align: "left",
    },
    {
      field: "name",
      title: "Tên dự án",
      align: "left",
      render: (rowData) => (
        <Tooltip title={rowData.name}>
          <b>{LimitString(50, rowData.name)}</b>
        </Tooltip>
      ),
    },
    {
      title: "Ngày tạo",
      align: "center",
      width: 120,
      render: (rowData) => (
        <span>{new Date(rowData.createdStamp).toLocaleDateString()}</span>
      ),
    },
  ];

  return (
    <StandardTable
      title="Danh sách dự án"
      hideCommandBar
      columns={columns}
      onRowClick={(_, rowData) => {
        navigate(`/project/${rowData.id}/tasks`);
      }}
      data={({ page, pageSize, search }) =>
        new Promise((resolve, reject) => {
          request(
            "get",
            `/projects?page=${page}&size=${pageSize}${
              search === ""
                ? ""
                : `&search=code:*${search}* OR name:*${search}*`
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
        })
      }
      options={{
        selection: false,
        pageSize: 5,
      }}
    />
  );
}
