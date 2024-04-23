import React, { useEffect, useState } from "react";
import GeneralScheduleTable from "./components/GeneralScheduleTable";
import { Autocomplete, Button, Input, TextField } from "@mui/material";
import { Search } from "@mui/icons-material";
import GeneralGroupAutoComplete from "../common-components/GeneralGroupAutoComplete";
import GeneralSemesterAutoComplete from "../common-components/GeneralSemesterAutoComplete";
import { request } from "api";
import { FacebookCircularProgress } from "components/common/progressBar/CustomizedCircularProgress";
import { toast } from "react-toastify";
import { useClasses } from "../hooks/useClasses";

const GeneralScheduleScreen = () => {
  const [selectedSemester, setSelectedSemester] = useState(null);
  const [selectedGroup, setSelectedGroup] = useState(null);
  const [isResetLoading, setResetLoading] = useState(false);
  const [selectedRows, setSelectedRows] = useState([]);
  const { loading, error, classes, setClasses, setLoading } = useClasses(
    selectedGroup,
    selectedSemester
  );

  

  const handleResetTimeTabling = () => {
    setResetLoading(true);
    request(
      "post",
      `/general-classes/reset-schedule?semester=${selectedSemester?.semester}`,
      (res) => {
        let generalClasses = [];
        res.data?.forEach((classObj) => {
          if (classObj?.classCode !== null && classObj?.timeSlots) {
            classObj.timeSlots.forEach((timeSlot, index) => {
              const cloneObj = JSON.parse(
                JSON.stringify({
                  ...classObj,
                  ...timeSlot,
                  classCode: classObj.classCode,
                  id: classObj.id + `-${index + 1}`,
                  roomReservationId: timeSlot.id,
                })
              );
              delete cloneObj.timeSlots;
              generalClasses.push(cloneObj);
            });
          }
        });
        setClasses(generalClasses);
        setSelectedRows([]);
        setResetLoading(false);
        toast.success("Reset thời khóa biểu thành công!");
      },
      (error) => {
        console.log(error);
        toast.error("Có lỗi khi reset thời khóa biểu!");
        setResetLoading(false);
      },
      { ids: selectedRows }
    );
  };

  return (
    <div className="flex flex-col gap-4 w-full h-[700px]">
      <div className="flex flex-row justify-between">
        <div className="flex flex-row gap-2">
          <GeneralSemesterAutoComplete
            selectedSemester={selectedSemester}
            setSelectedSemester={setSelectedSemester}
          />
          <GeneralGroupAutoComplete
            selectedGroup={selectedGroup}
            setSelectedGroup={setSelectedGroup}
          />
        </div>
        <Button
          disabled={selectedRows.length === 0}
          startIcon={FacebookCircularProgress}
          variant="contained"
          color="error"
          onClick={handleResetTimeTabling}
        >
          Xóa lịch học TKB
        </Button>
      </div>
      <GeneralScheduleTable
        isLoading={isResetLoading}
        isDataLoading={loading}
        classes={classes}
        setClasses={setClasses}
        setSelectedRows={setSelectedRows}
        setLoading={setLoading}
        semester={selectedSemester}
      />
    </div>
  );
};

export default GeneralScheduleScreen;
