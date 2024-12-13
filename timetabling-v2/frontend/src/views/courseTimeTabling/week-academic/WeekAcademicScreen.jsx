import { FacebookCircularProgress } from "components/common/progressBar/CustomizedCircularProgress";
import { useState } from "react";
import { useAcademicWeeks } from "services/useAcademicWeeksData";
import GeneralSemesterAutoComplete from "views/general-time-tabling/common-components/GeneralSemesterAutoComplete";
import AddWeekAcademicSemesterDialog from "./components/AddWeekAcademicSemesterDialog";
import { Button } from "@mui/material";
import WeekAcademicTable from "./components/WeekAcademicTable";

const WeekAcademicScreen = () => {
  const [selectedSemester, setSelectedSemester] = useState(null);
  const [isOpenWeekDialog, setOpenWeekDialog] = useState(false);
  const { 
    weeks, 
    isLoading, 
    deleteWeeks, 
    isDeleting 
  } = useAcademicWeeks(selectedSemester?.semester);

  const handleDeleteAcademicWeeks = async () => {
    if (!selectedSemester?.semester) return;
    await deleteWeeks(selectedSemester.semester);
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
      />
      <div className="flex gap-2 ">
        {isDeleting && <FacebookCircularProgress />}

        <Button
          variant="contained"
          sx={{ width: "200px" }}
          color="error"
          disabled={isDeleting || selectedSemester === null}
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
