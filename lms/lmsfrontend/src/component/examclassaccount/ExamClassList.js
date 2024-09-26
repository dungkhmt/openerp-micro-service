
import React, { useEffect, useState } from "react";
import { Link, useHistory } from "react-router-dom";
import { Button } from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import StandardTable from "../table/StandardTable";
import { errorNoti } from "../../utils/notification";
import { request } from "../../api";

function ExamClassList(){
    const [examClasses, setExamClasses] = useState([]);
    const history = useHistory();
    const columns = [
        {
            title: "ID",
            field: "id",
            render: (examClass) => (
              <Link to={`/exam-class/detail/${examClass.id}`}>{examClass.id}</Link>
            ),
          },
          { title: "Exam Class", field: "name" },
          { title: "Date", field: "execute_date" },
    ];
    const CreateExamClassButton = (
        <Button
          color="primary"
          variant="contained"
          onClick={() => history.push(`create`)}
        >
          <AddIcon /> Thêm mới
        </Button>
      );
    const actions = [{ icon: () => CreateExamClassButton, isFreeAction: true }];
    
    function getExamClassList(){
        let successHandler = (res) => setExamClasses(res.data);
    let errorHandlers = {
      onError: () => errorNoti("Đã xảy ra lỗi khi tải dữ liệu", true),
    };
    request("GET", "/get-all-exam-class", successHandler, errorHandlers);
    }

    useEffect(getExamClassList, []);

    return (
        <><StandardTable
        title="Exam Class List"
        columns={columns}
        data={examClasses}
        hideCommandBar
        options={{
          selection: false,
          search: true,
          sorting: true,
        }}
        actions={actions}
      />
      </>
    );
}

export default ExamClassList;