import DeleteRoundedIcon from "@mui/icons-material/DeleteRounded";
import EditIcon from "@mui/icons-material/Edit";
import PublishRoundedIcon from "@mui/icons-material/PublishRounded";
import IconButton from "@mui/material/IconButton";
import {styled} from "@mui/material/styles";
import Typography from "@mui/material/Typography";
import {request} from "api";
import TertiaryButton from "component/button/TertiaryButton";
import StandardTable from "component/table/StandardTable";
import React, {useEffect, useState} from "react";
import {errorNoti, processingNoti, updateErrorNoti, updateSuccessNoti,} from "utils/notification";
import UpdateClassForAssignmentDialog from "../UpdateClassForAssignmentDialog";

const cellStyles = { headerStyle: { padding: 8 }, cellStyle: { padding: 8 } };
const alignRightCellStyles = {
  headerStyle: { padding: 8, textAlign: "right" },
  cellStyle: { padding: 8, textAlign: "right" },
};

export const commandBarStyles = {
  position: "sticky",
  top: 124,
  zIndex: 11,
  mt: -3,
  mb: 3,
};

export const NumSelectedRows = ({ numSelected }) => (
  <Typography
    component="span"
    style={{ marginLeft: "auto", marginRight: 32 }}
  >{`Đã chọn ${numSelected} mục`}</Typography>
);

export const Input = styled("input")({
  display: "none",
});

export const CommandBarButton = (props) => (
  <TertiaryButton
    sx={{
      fontWeight: (theme) => theme.typography.fontWeightLight,
      "&:hover": {
        color: "primary.main",
      },
    }}
    color="inherit"
    {...props}
  >
    {props.children}
  </TertiaryButton>
);

function ClassInPlan({ planId }) {
  //
  const toastId = React.useRef(null);

  // Command delete button
  const [selectedRows, setSelectedRows] = useState([]);

  //
  const [classList, setClassList] = useState([]);

  //
  const [selectedClassId, setSelectedClassId] = useState(null);
  const [open, setOpen] = React.useState(false);
  // const [openModelExcel, setOpenModelExcel] = React.useState(false);

  // Table
  const columns = [
    { title: "Mã lớp", field: "classId", ...cellStyles },
    { title: "Mã học phần", field: "courseId", ...cellStyles },
    { title: "Tên học phần", field: "className", ...cellStyles },
    { title: "Loại lớp", field: "classType", ...cellStyles },
    { title: "Thời khoá biểu", field: "lesson", sorting: false, ...cellStyles },
    { title: "Chương trình", field: "program", ...cellStyles },
    {
      sorting: false,
      title: "Giờ quy đổi",
      field: "hourLoad",
      ...alignRightCellStyles,
    },
    {
      sorting: false,
      title: "Số GV",
      field: "numberPossibleTeachers",
      ...alignRightCellStyles,
    },
    {
      sorting: false,
      title: "Số GV trong KH",
      field: "numberPossibleTeachersInPlan",
      ...alignRightCellStyles,
    },
    {
      title: "",
      sorting: false,
      render: (rowData) => (
        <IconButton
          aria-label="edit"
          color="primary"
          onClick={() => {
            onUpdateHourLoad(rowData["classId"]);
          }}
        >
          <EditIcon />
        </IconButton>
      ),
      ...cellStyles,
    },
  ];

  // Funcs
  function onUpdateHourLoad(classId) {
    setSelectedClassId(classId);
    handleModalOpen();
  }

  const getClasses = () => {
    request("GET", `edu/teaching-assignment/plan/${planId}/class`, (res) => {
      setClassList(res.data);
    });
  };

  function uploadExcel(e) {
    const selectedFile = e.target.files[0];
    console.log("upload file " + selectedFile.name);

    const data = new FormData();
    data.append("file", selectedFile);

    processingNoti(toastId, false);
    request(
      "POST",
      `edu/teaching-assignment/plan/${planId}/class/upload-excel`,
      (res) => {
        e.target.value = "";

        if (res.data === true) {
          updateSuccessNoti(toastId, "Đã tải lên.", false);

          getClasses();
        } else {
          updateErrorNoti(
            toastId,
            "Đã xảy ra lỗi với yêu cầu này. Vui lòng kiểm tra định dạng file excel và thử lại."
          );
        }
      },
      {
        onError: (error) => {
          e.target.value = "";
          console.error(error);
          errorNoti("Đã có lỗi xảy ra.");
        },
      },
      data
    );
  }

  const onUpload = (e) => {
    uploadExcel(e);
    // handleModalCloseModelExcel();
  };

  // OK
  const onUpdateInfo = (hourLoad) => {
    let data = {
      hourLoad: hourLoad,
    };

    request(
      "PUT",
      `/edu/teaching-assignment/plan/${planId}/class/${selectedClassId}`,
      (res) => {
        const index = classList.findIndex((c) => c.classId === selectedClassId);

        const updatedClassList = classList.map((c, i) => {
          if (i === index) c.hourLoad = hourLoad;
          return c;
        });

        setClassList(updatedClassList);
      },
      { 401: () => {} },
      data
    );

    handleModalClose();
  };

  const handleModalOpen = () => {
    setOpen(true);
  };

  const handleModalClose = () => {
    setOpen(false);
  };

  // const handleModalOpenModelExcel = () => {
  //   setOpenModelExcel(true);
  // };

  // const handleModalCloseModelExcel = () => {
  //   setOpenModelExcel(false);
  // };

  // OK
  const removeClassesFromAssignmentPlan = () => {
    if (selectedRows.length > 0) {
      let data = selectedRows.map((row) => row.classId);

      request(
        "DELETE",
        `edu/teaching-assignment/plan/${planId}/class`,
        (res) => {
          const toRemove = new Set(selectedRows.map((row) => row.classId));
          const difference = classList.filter(
            (row) => !toRemove.has(row.classId)
          );

          setClassList(difference);
        },
        {},
        data
      );
    }
  };

  // OK
  useEffect(() => {
    getClasses();
  }, []);

  return (
    <>
      <StandardTable
        title={"Danh sách lớp trong kế hoạch"}
        columns={columns}
        data={classList}
        sx={{
          commandBar: commandBarStyles,
        }}
        onSelectionChange={(selectedRows) => setSelectedRows(selectedRows)}
        commandBarComponents={
          <>
            {selectedRows.length === 0 ? (
              <label htmlFor="upload-excel-class-in-plan">
                <Input
                  type="file"
                  accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                  id="upload-excel-class-in-plan"
                  onChange={onUpload}
                />
                <CommandBarButton
                  component="span"
                  startIcon={<PublishRoundedIcon />}
                  // onClick={handleModalOpenModelExcel}
                >
                  Tải lên Excel
                </CommandBarButton>
              </label>
            ) : (
              <>
                <CommandBarButton
                  startIcon={<DeleteRoundedIcon />}
                  onClick={removeClassesFromAssignmentPlan}
                >
                  Xoá
                </CommandBarButton>
                <NumSelectedRows numSelected={selectedRows.length} />
              </>
            )}
          </>
        }
      />

      {/* <UploadExcelClassForTeacherAssignmentModel
        open={openModelExcel}
        onClose={handleModalCloseModelExcel}
        onUpload={onUpload}
      /> */}

      <UpdateClassForAssignmentDialog
        open={open}
        onClose={handleModalClose}
        onUpdateInfo={onUpdateInfo}
        selectedClassId={selectedClassId}
      />
    </>
  );
}

export default ClassInPlan;
