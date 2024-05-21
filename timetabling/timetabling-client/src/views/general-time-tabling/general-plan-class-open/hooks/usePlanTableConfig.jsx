import { SaveAlt } from "@mui/icons-material";
import Delete from "@mui/icons-material/Delete";
import { Autocomplete, Button, TextField } from "@mui/material";
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
      const updatedClass = {
        ...params.row,
        [params.field]: e.target.value !== "" ? e.target.value : null,
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
    {
      headerName: "Mã lớp tạm thời",
      field: "id",
      width: 100,
    },
    {
      headerName: "Tổng số lớp",
      field: "numberOfClasses",
      width: 120,
    },
    {
      headerName: "Max SV/LT",
      field: "lectureMaxQuantity",
      width: 120,
      renderCell: (params) => (
        <TextField
          type="number"
          value={params.value}
          onChange={(e) => handleOnCellChange(e, params)}
        />
      ),
    },
    {
      headerName: "Max SV/BT",
      field: "exerciseMaxQuantity",
      width: 120,
      renderCell: (params) => (
        <TextField
          type="number"
          value={params.value}
          onChange={(e) => handleOnCellChange(e, params)}
        />
      ),
    },
    {
      headerName: "Max SV/LT+BT",
      field: "lectureExerciseMaxQuantity",
      width: 120,
      renderCell: (params) => (
        <TextField
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
        <TextField
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
      renderCell: (params) => (
        <Autocomplete
          {...params}
          options={["LT", "BT", "LT+BT"]}
          onChange={(e, option) => handleOnCellSelect(e, params, option)}
          renderInput={(option) => {
            return <TextField disableUnderline={false} {...option} />;
          }}
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
        <TextField
          value={params.value}
          onChange={(e) => handleOnCellChange(e, params)}
        />
      ),
    },
    {
      headerName: "Kíp",
      field: "crew",
      width: 200,
      renderCell: (params) => (
        <Autocomplete
          {...params}
          options={["S", "C"]}
          onChange={(e, option) => handleOnCellSelect(e, params, option)}
          renderInput={(option) => {
            return <TextField disableUnderline={false} {...option} />;
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
      with: 100,
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
      with: 100,
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
