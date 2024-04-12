import React, { useEffect, useState } from "react";
import { Button } from "@mui/material";
import AddWeekAcademicSemesterDialog from "./components/AddWeekAcademicSemesterDialog";
import WeekAcademicTable from "./components/WeekAcademicTable";
import GeneralSemesterAutoComplete from "views/general-time-tabling/common-components/GeneralSemesterAutoComplete";
import { toast } from "react-toastify";
import { useAcademicWeeks } from "./hooks/useAcademicWeeks";
import { request } from "api";
import { FacebookCircularProgress } from "components/common/progressBar/CustomizedCircularProgress";

const WeekAcademicScreen = () => {
  const [selectedSemester, setSelectedSemester] = useState(null);
  const [isOpenWeekDialog, setOpenWeekDialog] = useState(false);
  const { isLoading, error, weeks, setWeeks } = useAcademicWeeks(
    selectedSemester?.semester
  );
  const [deleteLoading, setDeleteLoading] = useState(false);

  useEffect(() => {
    if (error !== null) {
      console.log(error);
      toast.error("Có lỗi khi tìm kiếm danh sách tuần học!");
    }
  }, [error]);

  const handleDeleteAcademicWeeks = () => {
    setDeleteLoading(true);
    request(
      "delete",
      `/academic-weeks/?semester=${selectedSemester?.semester}`,
      (res) => {
        setWeeks([]);
        setDeleteLoading(false);
      },
      (error) => {
        toast.error("Có lỗi khi xóa danh sách tuần học!");
        setDeleteLoading(false);
        console.log(error);
      }
    );
  };

  return (
    <div className="flex flex-col justify-end gap-2 items-end">
      <GeneralSemesterAutoComplete
        selectedSemester={selectedSemester}
        setSelectedSemester={setSelectedSemester}
      />
      <AddWeekAcademicSemesterDialog
        open={isOpenWeekDialog}
        setUpdateSelectedSemester={setSelectedSemester}
        setOpen={setOpenWeekDialog}
        setWeeks={setWeeks}
      />
      <div className="flex gap-2 ">
        {deleteLoading && <FacebookCircularProgress />}

        <Button
          variant="contained"
          sx={{ width: "200px" }}
          color="error"
          disabled={deleteLoading || selectedSemester === null}
          onClick={handleDeleteAcademicWeeks}
        >
          Xóa danh sách tuần học của kì
        </Button>
        <Button
          disabled={isLoading}
          sx={{ width: "200px" }}
          variant="contained"
          onClick={() => {
            setOpenWeekDialog(true);
          }}
        >
          Thêm danh sách tuần học
        </Button>
      </div>
      <WeekAcademicTable
        isLoading={isLoading}
        weeks={weeks}
        selectedSemester={selectedSemester}
      />
    </div>
  );
};

export default WeekAcademicScreen;
