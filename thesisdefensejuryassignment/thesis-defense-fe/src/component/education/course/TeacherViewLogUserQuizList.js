import {Box} from "@material-ui/core";
import MaterialTable from "material-table";
import React, {useRef} from "react";
import {request} from "../../../api";
import {toFormattedDateTime} from "../../../utils/dateutils";
import changePageSize, {components, localization,} from "../../../utils/MaterialTableUtils";

const cellStyles = { headerStyle: { padding: 8 }, cellStyle: { padding: 8 } };
const alignRightCellStyles = {
  headerStyle: { padding: 8, textAlign: "right" },
  cellStyle: { padding: 8, textAlign: "right" },
};

const columns = [
  { title: "User", field: "userLoginId", ...cellStyles },
  { title: "Name", field: "fullName", ...cellStyles },
  { title: "Affiliations", field: "affiliations", ...cellStyles },
  { title: "QuestionId", field: "questionId", ...cellStyles },
  { title: "Course", field: "courseName", ...cellStyles },
  { title: "Topic", field: "topicName", ...cellStyles },
  { title: "Grade", field: "grade", ...alignRightCellStyles },
  { title: "Date", field: "createdStamp", ...alignRightCellStyles },
];

function TeacherViewLogUserQuizList(props) {
  const { classId } = props;
  const tableRef = useRef();

  return (
    <Box position="relative">
      <MaterialTable
        title={null}
        tableRef={tableRef}
        columns={columns}
        data={(query) =>
          new Promise((resolve, reject) => {
            request(
              "get",
              `/edu/class/${classId}/user-quiz/log?page=${query.page}&size=${query.pageSize}`,
              (res) => {
                const data = res.data;
                const logs = data.content.map((log) => ({
                  ...log,
                  createdStamp: toFormattedDateTime(log.createdStamp),
                }));

                resolve({
                  data: logs, // your data array
                  page: data.number, // current page number
                  totalCount: data.totalElements, // total row number
                });
              },
              {
                onError: (e) => {
                  changePageSize(5, tableRef);
                  reject({
                    message:
                      "Đã có lỗi xảy ra trong quá trình tải dữ liệu. Thử lại ",
                    errorCause: "query",
                  });
                },
              }
            );
          })
        }
        localization={{
          ...localization,
        }}
        options={{
          search: false,
          sorting: false,
          pageSize: 20,
          headerStyle: {
            backgroundColor: "transparent",
          },
        }}
        components={{
          ...components,
        }}
      />
    </Box>
  );
}

export default TeacherViewLogUserQuizList;
