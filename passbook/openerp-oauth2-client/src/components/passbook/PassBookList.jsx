import React, { useEffect, useState } from "react";
import { request } from "../../api";
import { StandardTable } from "erp-hust/lib/StandardTable";
import { toFormattedDateTime } from "../../utils/dateutils";
import IconButton from "@mui/material/IconButton";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import { Button } from "@mui/material";
export default function PassBookList() {
  const columns = [
    { title: "PassBookName", field: "passBookName" },
    { title: "userId", field: "userId" },
    { title: "duration", field: "duration" },
    { title: "startDate", field: "startDate" },
    { title: "EndDate", field: "endDate" },
    
    { title: "amountMoney", field: "amountMoney" },
    { title: "rate", field: "rate" },
  ];
  const [passBooks, setPassBooks] = useState([]);

  useEffect(() => {
    request("get", "/get-passbook-list", (res) => {
      let D = res.data;
      const data = D.map((c) => ({
        ...c,
        startDate: toFormattedDateTime(c.startDate),
        endDate:toFormattedDateTime(c.endDate),
      }));
      console.log("passbooks data = ", data);
      setPassBooks(data);
    }).then();
  }, []);
  return (
    <div>
      <StandardTable
        title="User List"
        columns={columns}
        data={passBooks}
        // hideCommandBar
        options={{
          selection: true,
          pageSize: 20,
          search: true,
          sorting: true,
        }}
      />
    </div>
  );
}
