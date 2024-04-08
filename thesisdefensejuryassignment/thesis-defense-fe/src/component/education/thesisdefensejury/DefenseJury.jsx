import { Button, Card } from "@material-ui/core/";
import MaterialTable, { MTableToolbar } from "material-table";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { request } from "../../../api";
import { StandardTable } from "erp-hust/lib/StandardTable";

function DefenseJury() {
  const navigate = useNavigate();
  const [jurys, setJurys] = useState([]);

  const columns = [
    { title: "ID", field: "id" },
    { title: "Tên HD", field: "name" },
    { title: "Người tạo", field: "userLoginId" },
    { title: "Ngày tạo", field: "createdTime" },
  ];

  async function getAllDefenseJury() {
    request(
      // token,
      // history,
      "GET",
      "/defense_jurys",
      (res) => {
        console.log(res.data);
        setJurys(res.data.DefenseJurys);
      }
    );
  }

  const handleModalOpen = () => {
    navigate(`/thesis/defense_jury/create`, {
      state: {},
    });
  };
  useEffect(() => {
    getAllDefenseJury();
  }, []);

  return (
    <Card>
      <StandardTable
        title={"Danh sách HD Bao ve"}
        data={jurys}
        columns={columns}
        options={{
          selection: false,
          pageSize: 5,
          search: true,
          sorting: true,
        }}
      />
    </Card>
  );
}

export default DefenseJury;
