import React, { useState } from "react";
import { Button } from "@mui/material";
import AddWeekAcademicSemesterDialog from "./components/AddWeekAcademicSemesterDialog";
import WeekAcademicTable from "./components/WeekAcademicTable";

const WeekAcademicScreen = () => {
  const [isOpenWeekDialog, setOpenWeekDialog] = useState(false);
  return (
    <div>
      <AddWeekAcademicSemesterDialog open={isOpenWeekDialog} setOpen={setOpenWeekDialog} />
      <Button variant="contained" onClick={()=>{setOpenWeekDialog(true)}}>Thêm danh sách tuần học</Button>
      <WeekAcademicTable />
    </div>
  );
};

export default WeekAcademicScreen;
