import { SaveAlt } from "@mui/icons-material";
import { Autocomplete, Button, TextField } from "@mui/material";
import { request } from "api";
import { toast } from "react-toastify";

export const useUploadTableConfig = (handleOnChangeCell, handleOnCellSelect) => {

  const handleSaveClass = (classData) => {
    request("post", "/general-classes/update-class", (res) => {
      console.log(res);
      toast.success("Cập nhật lớp học thành công!");
    }, (err) => {
      console.log(err);
      toast.error("Có lỗi khi cập nhật lớp học!");
    }, {
      generalClass: {...classData}
    })
  }
  return [
    {
      headerName: "Mã lớp",
      field: "classCode",
      width: 100,
    },
    {
      headerName: "Lớp học",
      field: "studyClass",
      width: 200,
      renderCell: (params) => (
        <TextField variant='standard' value={params.row.studyClass} onChange={(e) => handleOnChangeCell(e,params)}/>
      ),
    },
    {
      headerName: "Tuần học",
      field: "learningWeeks",
      width: 120,
    },
    {
      headerName: "Mã học phần",
      field: "moduleCode",
      width: 80,
      renderCell: (params) => (
        <TextField variant='standard' value={params.row.moduleCode} onChange={(e) => handleOnChangeCell(e,params)}/>
      ),
    },
    {
      headerName: "Tên học phần",
      field: "moduleName",
      width: 200,
      renderCell: (params) => (
        <TextField variant='standard' value={params.row.moduleName} onChange={(e) => handleOnChangeCell(e,params)}/>
      ),
    },
    {
      headerName: "SL MAX",
      field: "quantityMax",
      width: 100,
    },
    {
      headerName: "Loại lớp",
      field: "classType",
      width: 100,
    },

    {
      headerName: "Thời lượng",
      field: "mass",
      width: 100,
      renderCell: (params) => (
        <TextField variant='standard' value={params.row.mass} onChange={(e) => handleOnChangeCell(e,params)}/>
      ),
    },
    {
      headerName: "Kíp",
      field: "crew",
      width: 80,
      renderCell: (params) => (
        <Autocomplete
          {...params}
          options={["S", "C"]}
          onChange={(e, option) => handleOnCellSelect(e, params, option)}
          renderInput={(option) => {
            return (
              <TextField variant="standard" {...option} sx={{ width: 80 }} />
            );
          }}
        />
      ),
    },
    {
      headerName: "Đợt",
      field: "openBatch",
      width: 80,
      renderCell: (params) => (
        <Autocomplete
          {...params}
          options={["Chẵn", "Lẻ", "A", "B", "AB"]}
          onChange={(e, option) => handleOnCellSelect(e, params, option)}
          renderInput={(option) => {
            return (
              <TextField variant="standard" {...option} sx={{ width: 80 }} />
            );
          }}
        />
      ),
    },
    {
      headerName: "Khóa",
      field: "course",
      width: 40,
      renderCell: (params) => (
        <TextField variant='standard' value={params.row.course} onChange={(e) => handleOnChangeCell(e,params)}/>
      ),
    },
    {
      headerName: "Lưu",
      field: "saveBtn",
      width: 100,
      renderCell: (params) => (
        <Button onClick={(e) => handleSaveClass(params.row)}><SaveAlt/></Button>
      ),
    },
  ];
};
