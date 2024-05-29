import { Delete, SaveAlt } from "@mui/icons-material";
import { Autocomplete, Button, Input, TextField } from "@mui/material";
import { request } from "api";
import { toast } from "react-toastify";

export const usePlanGeneralTableCol = (setGeneralClasses) => {
  const handleOnCellChange = (e, params) => {
    setGeneralClasses((prevClasses) => {
      const index = prevClasses?.findIndex(
        (prevClass) => prevClass?.id === params?.id
      );
      if (index === -1) return prevClasses;
      return [
        ...prevClasses.slice(0, index),
        {...prevClasses[index], [params.field]: e.target.value},
        ...prevClasses.slice(index + 1),
      ]
    });
  };

  const handleSaveClass = (generalClass) => {
    console.log(generalClass);
    request(
      "post",
      "/plan-general-classes/update-general-class",
      (res) => {
        toast.success("Lưu lớp thành công");
      },
      (error) => {
        if (error.response.status == 410) {
          toast.error(error.response.data);
        } else {
          toast.error("Cập nhật lớp thất bại");
        }
      },
      { generalClass },
      null,
      null
    );
  };

  const handleDeleteClass = (generalClass) => {
    request(
      "delete",
      `/general-classes/?generalClassId=${generalClass?.id}`,
      (res) => {
        setGeneralClasses((prevClasses) => {
          return prevClasses.filter(
            (prevClass) => prevClass?.id !== res.data?.id
          );
        });
        toast.success("Xóa lớp thành công!");
      },
      (error) => {
        if (error.response.status == 410) {
          toast.error(error.response.data);
        } else {
          toast.error("Xóa lớp thất bại");
        }
      }
    );
  };

  const handleOnCellSelect = (e, params, option) => {
    setGeneralClasses((prevClasses) => {
      const index = prevClasses?.findIndex(
        (prevClass) => prevClass?.id === params?.id
      );
      if (index === -1) return prevClasses;
      return [
        ...prevClasses.slice(0, index),
        {...prevClasses[index], [params.field]: option},
        ...prevClasses.slice(index + 1),
      ]
    });
  };

  return [
    {
      headerName: "Mã lớp",
      field: "classCode",
      width: 120,
      renderCell: (params) => (
        <Input
          type="number"
          value={params.value}
          onChange={(e) => handleOnCellChange(e, params)}
        />
      ),
    },
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
    {
      headerName: "Tuần học",
      field: "learningWeeks",
      width: 120,
      renderCell: (params) => (
        <Input
          value={params.value}
          onChange={(e) => handleOnCellChange(e, params)}
        />
      ),
    },
    {
      headerName: "Mã học phần",
      field: "moduleCode",
      width: 100,
    },

    {
      headerName: "Số tiết",
      field: "duration",
      width: 100,
      renderCell: (params) => (
        <Input
          type="number"
          value={params.value}
          onChange={(e) => handleOnCellChange(e, params)}
        />
      ),
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
        <Input
          type="number"
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
    {
      headerName: "Kíp",
      field: "crew",
      width: 100,
      renderCell: (params) => (
        <Autocomplete
          {...params}
          options={["S", "C"]}
          onChange={(e, option) => handleOnCellSelect(e, params, option)}
          renderInput={(option) => {
            return (
              <TextField
                variant="standard"
                disableUnderline={false}
                {...option}
                sx={{ width: 80 }}
              />
            );
          }}
        />
      ),
    },
    {
      headerName: "Mã lớp cha",
      field: "parentClassId",
      width: 120,
      renderCell: (params) => (
        <Input
          value={params.value}
          onChange={(e) => handleOnCellChange(e, params)}
        />
      ),
    },
    // {
    //   headerName: "Mã lớp tạm thời",
    //   field: "id",
    //   width: 100,
    // },
    {
      headerName: "Mã lớp tham chiếu",
      field: "refClassId",
      width: 100,
      renderCell: (params) => (
        <Input
          type="number"
          value={params.value}
          onChange={(e) => handleOnCellChange(e, params)}
        />
      ),
    },
    {
      headerName: "Loại lớp",
      field: "classType",
      width: 100,
      editable: true,
      renderCell: (params) => (
        <Autocomplete
          {...params}
          options={["LT", "BT", "LT+BT"]}
          onChange={(e, option) => handleOnCellSelect(e, params, option)}
          renderInput={(option) => {
            return (
              <TextField
                variant="standard"
                disableUnderline={false}
                {...option}
                sx={{ width: 80 }}
              />
            );
          }}
        />
      ),
    },
    {
      headerName: "Lưu",
      field: "saveButton",
      with: 80,
      renderCell: (params) => (
        <div className="flex gap-2">
          <Button onClick={(e) => handleSaveClass(params.row)}>
            <SaveAlt />
          </Button>
        </div>
      ),
    },
    {
      headerName: "Xóa",
      field: "deleteButton",
      with: 80,
      renderCell: (params) => (
        <div className="flex gap-2">
          <Button onClick={(e) => handleDeleteClass(params.row)}>
            <Delete />
          </Button>
        </div>
      ),
    },

    // {
    //   headerName: "Khóa",
    //   field: "course",
    //   width: 100,
    // }
  ];
};
