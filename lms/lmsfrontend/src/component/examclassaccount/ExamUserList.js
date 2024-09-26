
import React, { useEffect, useState } from "react";

function ExamUserList(){
    const [examClasses, setExamClasses] = useState([]);

    const columns = [
        {
            title: "ID",
            field: "id",
            render: (examClass) => (
              <Link to={`/edu/exam-class/detail/${examClass.id}`}>{examClass.id}</Link>
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
        <>ExamUserList</>
    );
}

export default ExamUserList;