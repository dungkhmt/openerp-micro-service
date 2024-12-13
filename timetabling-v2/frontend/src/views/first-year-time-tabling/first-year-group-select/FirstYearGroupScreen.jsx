import React from "react";
import { useGeneralSchedule } from "services/useGeneralScheduleData";
import AddNewGroupDialogue from "./components/AddNewGroupDialogue";
import AddCreatedGroupDialogue from "./components/AddCreatedGroupDialogue";
import FirstYearSemesterAutoComplete from "../common-components/FirstYearSemesterAutoComplete";
import { Button } from "@mui/material";
import FirstYearGroupTable from "./components/FirstYearGroupTable";

const FirstYearGroupScreen = () => {
  const { states, setters, handlers } = useGeneralSchedule();
  const { selectedSemester, classes, loading, selectedRows } = states;
  const { setSelectedSemester, setSelectedRows } = setters;
  const { handleRefreshClasses } = handlers;

  const [openCreatedGroupDialouge, setOpenCreatedGroupDialouge] =
    React.useState(false);
  const [openNewGroupDialouge, setOpenNewGroupDialouge] = React.useState(false);

  const handleSelectionModelChange = (selectionModel) => {
    setSelectedRows(selectionModel);
  };

  return (
    <div className="flex flex-col gap-2">
      <AddNewGroupDialogue
        open={openNewGroupDialouge}
        selectedClasses={selectedRows}
        setOpen={setOpenNewGroupDialouge}
        onSuccess={handleRefreshClasses}
      />
      <AddCreatedGroupDialogue
        selectedClasses={selectedRows}
        open={openCreatedGroupDialouge}
        setOpen={setOpenCreatedGroupDialouge}
        onSuccess={handleRefreshClasses}
      />
      <div className="flex flex-row gap-2 justify-between">
        <FirstYearSemesterAutoComplete
          selectedSemester={selectedSemester}
          setSelectedSemester={setSelectedSemester}
        />
        <div className="flex flex-row gap-2">
          <Button
            onClick={() => {
              setOpenNewGroupDialouge(true);
            }}
            disabled={selectedRows.length === 0}
            sx={{ width: "200px" }}
            variant="outlined"
          >
            Thêm vào nhóm mới
          </Button>

          <Button
            onClick={() => {
              setOpenCreatedGroupDialouge(true);
            }}
            disabled={selectedRows.length === 0}
            sx={{ width: "200px" }}
            variant="outlined"
          >
            Thêm vào nhóm đã tạo
          </Button>
        </div>
      </div>
      <FirstYearGroupTable
        handleSelectionModelChange={handleSelectionModelChange}
        classes={classes}
        dataLoading={loading}
      />
    </div>
  );
};

export default FirstYearGroupScreen;
