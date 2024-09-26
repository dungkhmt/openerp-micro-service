import {Box, Button, Card, CardContent} from "@material-ui/core";
import {MuiThemeProvider} from "@material-ui/core/styles";
import AddCircleIcon from "@material-ui/icons/AddCircle";
import MaterialTable, {MTableToolbar} from "material-table";
import React, {useEffect, useState} from "react";
import {Link, useHistory} from "react-router-dom";
import {toast} from "react-toastify";
import XLSX from "xlsx";
import {request} from "../../../api";
import {tableIcons} from "../../../utils/iconutil";
import {
  errorNoti,
  processingNoti,
  successNoti,
  updateErrorNoti,
  updateSuccessNoti,
  warningNoti,
} from "../../../utils/notification";
import UploadButton from "../UploadButton";

function CourseList() {
  const history = useHistory();

  // Snackbar
  const toastId = React.useRef(null);

  // Table.
  const [courses, setCourses] = useState([]);
  const columns = [
    {
      field: "courseId",
      title: "Mã học phần",
      render: (rowData) => (
        <Link
          to={{
            pathname: "/edu/course/detail",
            state: {
              courseId: rowData["courseId"],
            },
          }}
        >
          {rowData["courseId"]}
        </Link>
      ),
    },
    {
      field: "courseName",
      title: "Tên học phần",
    },
    {
      field: "credit",
      title: "Số tín chỉ",
    },
  ];

  // Functions.
  const getAllCourses = () => {
    request(
      "get",
      "/edu/course/all",
      (res) => {
        console.log("getAllCourses, courses ", res.data);
        setCourses(res.data);
      },
      { onError: (e) => console.log("getAllCourses, error ", e) }
    );
  };

  const onClickCreateNewButton = () => {
    history.push({
      pathname: "/edu/course/create",
      state: {},
    });
  };

  const onClickSaveButton = (files) => {
    processingNoti(toastId, false);

    let file = files[0];
    let sheetName = "courses";
    let noCols = 3;
    let dataTypeOfCol = ["s", "s", "n"];
    let dataColName = ["courseId", "courseName", "credit"];
    let excelColName = [];

    const reader = new FileReader();

    reader.onload = (event) => {
      try {
        const { result } = event.target;
        const workbook = XLSX.read(result, { type: "binary" });

        if (workbook.Sheets.hasOwnProperty(sheetName)) {
          let sheet = workbook.Sheets[sheetName];
          // console.log(sheet);

          if (!sheet.hasOwnProperty("!ref")) {
            if (toast.isActive(toastId.current)) {
              updateErrorNoti(
                toastId,
                `Sheet "${sheetName}" không chứa dữ liệu`
              );
            } else {
              errorNoti(`Sheet "${sheetName}" không chứa dữ liệu`);
            }

            return;
          }

          let range = XLSX.utils.decode_range(sheet["!ref"]);
          let startRow = range.s.r + 1;
          let endRow = range.e.r + 1;

          if (endRow === startRow) {
            if (toast.isActive(toastId.current)) {
              updateErrorNoti(
                toastId,
                `Sheet "${sheetName}" không chứa dữ liệu`
              );
            } else {
              errorNoti(`Sheet "${sheetName}" không chứa dữ liệu`);
            }

            return;
          }

          // Check number of column.
          let startCol = range.s.c; // number
          let endCol = range.e.c; // number

          if (endCol - startCol + 1 > noCols) {
            warningNoti(
              `Số cột dữ liệu nhiều hơn so với yêu cầu. Các cột không được yêu cầu sẽ bị bỏ qua. Tải xuống tệp định dạng nội dung chuẩn`
            );
          } else if (endCol - startCol + 1 < noCols) {
            if (toast.isActive(toastId.current)) {
              updateErrorNoti(
                toastId,
                `Số cột dữ liệu ít hơn so với yêu cầu. Tải xuống tệp định dạng nội dung chuẩn`
              );
            } else {
              errorNoti(
                `Số cột dữ liệu ít hơn so với yêu cầu. Tải xuống tệp định dạng nội dung chuẩn`
              );
            }

            return;
          }

          // Xem bat loi nay o day da hop ly chua hay
          if (sheet.hasOwnProperty("!merges")) {
            if (toast.isActive(toastId.current)) {
              updateErrorNoti(
                toastId,
                `Bảng có chứa ${sheet["!merges"].length} ô hợp nhất (merge cell). Tải xuống tệp định dạng nội dung chuẩn`
              );
            } else {
              errorNoti(
                `Bảng có chứa ${sheet["!merges"].length} ô hợp nhất (merge cell). Tải xuống tệp định dạng nội dung chuẩn`
              );
            }

            return;
          }

          // Round 1: validate cell.
          let processingCell;

          for (let i = 0; i < noCols; i++) {
            excelColName.push(XLSX.utils.encode_col(startCol + i));

            sheet[excelColName[i] + startRow] = {
              t: dataTypeOfCol[i],
              v: dataColName[i],
            };

            for (let j = startRow + 1; j <= endRow; j++) {
              processingCell = excelColName[i] + j;

              if (sheet.hasOwnProperty(processingCell)) {
                if (!(sheet[processingCell].t === dataTypeOfCol[i])) {
                  if (toast.isActive(toastId.current)) {
                    updateErrorNoti(
                      toastId,
                      `Kiểu dữ liệu của ô "${processingCell}" không đúng định dạng. Tải xuống tệp định dạng nội dung chuẩn`
                    );
                  } else {
                    errorNoti(
                      `Kiểu dữ liệu của ô "${processingCell}" không đúng định dạng. Tải xuống tệp định dạng nội dung chuẩn`
                    );
                  }

                  return;
                }
              } else {
                if (toast.isActive(toastId.current)) {
                  updateErrorNoti(
                    toastId,
                    `Ô "${processingCell}" không chứa dữ liệu. Tải xuống tệp định dạng nội dung chuẩn`
                  );
                } else {
                  errorNoti(
                    `Ô "${processingCell}" không chứa dữ liệu. Tải xuống tệp định dạng nội dung chuẩn`
                  );
                }

                return;
              }
            }
          }

          // Round 2: format data.
          for (let i = startRow + 1; i <= endRow; i++) {
            // Format courseId.
            processingCell = excelColName[0] + i;
            sheet[processingCell].v = sheet[processingCell].v
              .trim()
              .replace(/\s/g, "")
              .toUpperCase();

            // Format courseName.
            processingCell = excelColName[1] + i;
            sheet[processingCell].v = sheet[processingCell].v
              .trim()
              .replace(/\s+/g, " ");
          }

          // Round 3: check duplicate record.
          let duplicateCourses = new Set();

          for (let i = startRow + 1; i < endRow; i++) {
            processingCell = excelColName[0] + i;

            for (let j = i + 1; j <= endRow; j++) {
              if (sheet[processingCell].v === sheet[excelColName[0] + j].v) {
                duplicateCourses.add(sheet[processingCell].v);
              }
            }
          }

          if (duplicateCourses.size > 0) {
            let warningMess;
            let duplCoursesArr = Array.from(duplicateCourses);
            let len = duplCoursesArr.length - 1;

            if (duplCoursesArr.length === 1) {
              warningMess = `Phát hiện các bản ghi trùng nhau của học phần: `;
            } else {
              warningMess = `Phát hiện các bản ghi trùng nhau của các học phần: `;

              for (let i = 0; i < len; i++) {
                warningMess += duplCoursesArr[i] + ", ";
              }
            }

            warningMess += `${duplCoursesArr[len]}. Một bản ghi trong mỗi cặp trùng nhau sẽ bị loại bỏ`;

            warningNoti(warningMess);
          }

          // Everything is OK!.
          request(
            "post",
            "/edu/course/add-list-of-courses",
            (res) => {
              if (toast.isActive(toastId.current)) {
                updateSuccessNoti(toastId, res.data);
              } else {
                successNoti(res.data);
              }

              getAllCourses();
            },
            {
              onError: (error) => {
                if (toast.isActive(toastId.current)) {
                  updateErrorNoti(toastId, "Rất tiếc! Đã xảy ra lỗi :((");
                } else {
                  errorNoti("Rất tiếc! Đã xảy ra lỗi :((");
                }

                console.log("onClickSaveButton, error ", error);
              },
            },
            XLSX.utils.sheet_to_json(sheet)
          );
        } else {
          updateErrorNoti(toastId, `Không tìm thấy sheet "${sheetName}"`);
          return;
        }
      } catch (e) {
        console.log(e);

        if (toast.isActive(toastId.current)) {
          updateErrorNoti(toastId, "Rất tiếc! Đã xảy ra lỗi :((");
        } else {
          errorNoti("Rất tiếc! Đã xảy ra lỗi :((");
        }

        return;
      }
    };

    reader.readAsBinaryString(file);
  };

  useEffect(() => {
    getAllCourses();
  }, []);

  return (
    <div>
      <MuiThemeProvider>
        <Card>
          <CardContent>
            <MaterialTable
              title="Danh sách môn học"
              columns={columns}
              data={courses}
              icons={tableIcons}
              localization={{
                header: {
                  actions: "",
                },
                body: {
                  emptyDataSourceMessage: "Không có bản ghi nào để hiển thị",
                  filterRow: {
                    filterTooltip: "Lọc",
                  },
                },
              }}
              options={{
                search: false,
                actionsColumnIndex: -1,
              }}
              components={{
                Toolbar: (props) => (
                  <div>
                    <MTableToolbar {...props} />
                    <MuiThemeProvider>
                      <Box display="flex" justifyContent="flex-end" width="98%">
                        <Button
                          variant="contained"
                          color="primary"
                          onClick={onClickCreateNewButton}
                          startIcon={<AddCircleIcon />}
                          style={{ marginRight: 16 }}
                        >
                          Thêm mới
                        </Button>
                        <UploadButton
                          buttonTitle="Tải lên"
                          onClickSaveButton={onClickSaveButton}
                          filesLimit={1}
                        />
                      </Box>
                    </MuiThemeProvider>
                  </div>
                ),
              }}
            />
          </CardContent>
        </Card>
      </MuiThemeProvider>
    </div>
  );
}

export default CourseList;
