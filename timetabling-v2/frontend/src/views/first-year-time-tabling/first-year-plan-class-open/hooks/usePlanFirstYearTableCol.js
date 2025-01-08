import { SaveAlt } from "@mui/icons-material";
import { Autocomplete, Button, TextField } from "@mui/material";
import { request } from "api";
import { toast } from "react-toastify";

export const usePlanFirstYearTableCol = (setFirstYearClasses) => {

  const handleOnCellChange = (e, params) => {
    setFirstYearClasses((prevClasses) => {
      const updatedClass = {
        ...params.row,
        [params.field]: e.target.value,
      };
      const newClasses = prevClasses?.map((prevClass) => {
        if (prevClass?.id === params?.id) {
          return updatedClass;
        } else {
          return prevClass;
        }
      });

      return newClasses;
    });
  };

  const handleSaveClass = (firstYearClass) => {
    console.log(firstYearClass);
    request(
      "post",
      "/plan-general-classes/update-general-class",
      (res) => {
        toast.success("Lưu lớp thành công");
      },
      (err) => {
        toast.error("Lưu lớp thất bại");
      },
      { firstYearClass },
      null,
      null
    );
  };

  const handleOnCellSelect = (e, params, option) => {
    setFirstYearClasses((prevClasses) => {
      const updatedClass = {
        ...params.row,
        [params.field]: option,
      };
      const newClasses = prevClasses?.map((prevClass) => {
        console.log(prevClass?.id, params?.id);
        if (prevClass?.id === params?.id) {
          return updatedClass;
        } else {
          return prevClass;
        }
      });

      return newClasses;
    });
  };

  return [
    // {
    //   headerName: "Mã lớp",
    //   field: "classCode",
    //   width: 100,
    // },
    // {
    //   headerName: "Lớp học",
    //   field: "studyClass",
    //   width: 150,
    // },
    // {
    //   headerName: "Nhóm",
    //   field: "groupName",
    //   editable: true,
    //   width: 120,
    // },
    // {
    //   headerName: "Tuần học",
    //   field: "learningWeeks",
    //   width: 120,
    // },
    {
      headerName: "Mã học phần",
      field: "moduleCode",
      width: 100,
    },
    {
      headerName: "Tên học phần",
      field: "moduleName",
      width: 100,
    },
    // {
    //   headerName: "SL thực",
    //   field: "quantity",
    //   editable: true,
    //   width: 100,
    // },
    {
      headerName: "SL MAX",
      field: "quantityMax",
      width: 120,
      renderCell: (params) => (
        <TextField
          value={params.value}
          onChange={(e) => handleOnCellChange(e, params)}
        />
      ),
    },
    {
      headerName: "Thời lượng",
      field: "mass",
      width: 100,
    },
    // {
    //   headerName: "Trạng thái",
    //   field: "state",
    //   width: 100,
    // },
    // {
    //   headerName: "Kíp",
    //   field: "crew",
    //   width: 100,
    // },
    {
      headerName: "Mã lớp cha",
      field: "parentClassId",
      width: 120,
      renderCell: (params) => (
        <TextField
          value={params.value}
          onChange={(e) => handleOnCellChange(e, params)}
        />
      ),
    },
    {
      headerName: "Mã lớp tạm thời",
      field: "id",
      width: 100,
    },
    {
      headerName: "Mã lớp tham chiếu",
      field: "refClassId",
      width: 100,
    },
    {
      headerName: "Loại lớp",
      field: "classType",
      width: 120,
      editable: true,
      renderCell: (params) => (
        <Autocomplete
          {...params}
          options={["LT", "BT", "LT+BT"]}
          onChange={(e, option) => handleOnCellSelect(e, params, option)}
          renderInput={(option) => {
            console.log(option);
            return <TextField disableUnderline={false} {...option} />;
          }}
        />
      ),
    },
    {
      headerName: "Lưu",
      field: "saveButton",
      with: 100,
      renderCell: (params) => (
        <Button onClick={(e) => handleSaveClass(params.row)}>
          <SaveAlt />
        </Button>
      ),
    },
    // {
    //   headerName: "Khóa",
    //   field: "course",
    //   width: 100,
    // }
  ];
};
