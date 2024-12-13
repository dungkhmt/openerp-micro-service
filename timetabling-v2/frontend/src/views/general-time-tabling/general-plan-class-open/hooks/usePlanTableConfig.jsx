import { Delete, SaveAlt } from "@mui/icons-material";
import { Autocomplete, Box, Button, Input, TextField } from "@mui/material";
import { request } from "api";
import { toast } from "react-toastify";

export const usePlanTableConfig = (setPlanClasses) => {
  const handleSaveClass = (planClass) => {
    request(
      "post",
      "/plan-general-classes/update-plan-class",
      (res) => {
        toast.success("Cập nhật lớp thành công!");
        console.log(res);
      },
      (error) => {
        if (error.response.status == 410) {
          toast.error(error.response.data);
        } else {
          toast.error("Cập nhật lớp thất bại");
        }
      },
      { planClass },
      null,
      null
    );
  };

  const handleOnCellChange = (e, params) => {
    setPlanClasses((prevClasses) => {
      return prevClasses?.map((prevClass) =>
        prevClass?.id === params?.id
          ? {
              ...prevClass,
              [params.field]: e.target.value !== "" ? e.target.value : null,
            }
          : prevClass
      );
    });
  };

  const handleDeleteClass = (planClass) => {
    request(
      "delete",
      `/plan-general-classes/?planClassId=${planClass?.id}`,
      (res) => {
        setPlanClasses((prevClasses) => {
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
    setPlanClasses((prevClasses) => {
      return prevClasses?.map((prevClass) =>
        prevClass?.id === params?.id
          ? { ...prevClass, [params.field]: option }
          : prevClass
      );
    });
  };

  return [
    {
      headerName: "Mã lớp tạm thời",
      field: "id",
      width: 100,
    },
    {
      headerName: "Tổng số lớp",
      field: "numberOfClasses",
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
      headerName: "Max SV/LT",
      field: "lectureMaxQuantity",
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
      headerName: "Max SV/BT",
      field: "exerciseMaxQuantity",
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
      headerName: "Max SV/LT+BT",
      field: "lectureExerciseMaxQuantity",
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
      headerName: "SL MAX",
      field: "quantityMax",
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
      headerName: "Loại Tuần",
      field: "weekType",
      width: 100,
      renderCell: (params) => (
        <Autocomplete
          {...params}
          options={["Chẵn", "Lẻ", "Chẵn+Lẻ"]}
          onChange={(e, option) => handleOnCellSelect(e, params, option)}
          renderInput={(option) => {
            return (
              <TextField variant="standard" {...option} sx={{ width: 80 }} />
            );
          }}
          renderOption={(props, option) => (
            <Box
              component="li"
              sx={{ "& > img": { mr: 2, flexShrink: 0 } }}
              {...props}
            >
              {option}
            </Box>
          )}
        />
      ),
    },
    {
      headerName: "Thời lượng",
      field: "mass",
      width: 100,
    },
    {
      headerName: "Tuần học",
      field: "learningWeeks",
      width: 100,
      renderCell: (params) => (
        <Input
          value={params.value}
          onChange={(e) => handleOnCellChange(e, params)}
        />
      ),
    },
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
              <TextField variant="standard" {...option} sx={{ width: 80 }} />
            );
          }}
        />
      ),
    },
    {
      headerName: "Mã HP",
      field: "moduleCode",
      width: 100,
    },
    {
      headerName: "Tên HP",
      field: "moduleName",
      width: 100,
    },
    {
      headerName: "CTĐT",
      field: "programName",
      width: 100,
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
  ];
};
