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
    {
      title: "Delete",
      sorting: false,
      render: (rowData) => (
          <IconButton
              onClick={() => {
                  deletePassBook(rowData.passBookId)
              }}
              variant="contained"
              color="error"
          >
              <DeleteIcon/>
          </IconButton>
      ),
  },
  ];
  const [passBooks, setPassBooks] = useState([]);

  function deletePassBook(passBookId){
    request("get", "/remove-passbook/" + passBookId, (res) => {
      console.log("remove passbooks data = ", res.data);
      getListPassBooks();
    }).then();
  }
  function getListPassBooks(){
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

  }
  useEffect(() => {
    getListPassBooks();
      }, []);
  return (
    <div>
      <StandardTable
        title="User List"
        columns={columns}
        data={passBooks}
        // hideCommandBar
        onSelectionChange={() =>{}}

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
