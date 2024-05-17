import React, { useState } from "react";
import FirstYearSemesterAutoComplete from "../common-components/FirstYearSemesterAutoComplete";
import { Button } from "@mui/material";
import FirstYearGroupTable from "./components/FirstYearGroupTable";
import { useClasses } from "../hooks/useClasses";
import AddCreatedGroupDialogue from "./components/AddCreatedGroupDialogue";
import AddNewGroupDialogue from "./components/AddNewGroupDialogue";

const FirstYearGroupScreen = () => {
  const [selectedSemester, setSelectedSemester] = useState(null);
  const { loading, error, classes, setClasses } = useClasses(
    null,
    selectedSemester
  );
  const [selectedClasses, setSelectedClasses] = useState([]);
  const [openCreatedGroupDialouge, setOpenCreatedGroupDialouge] =
    useState(false);
  const [openNewGroupDialouge, setOpenNewGroupDialouge] = useState(false);

  const handleSelectionModelChange = (selectionModel) => {
    setSelectedClasses(selectionModel);
  };

  return (
    <div className="flex flex-col gap-2">
      <AddNewGroupDialogue
        open={openNewGroupDialouge}
        selectedClasses={selectedClasses}
        setOpen={setOpenNewGroupDialouge}
        setClasses={setClasses}
      />
      <AddCreatedGroupDialogue
        setClasses={setClasses}
        selectedClasses={selectedClasses}
        open={openCreatedGroupDialouge}
        setOpen={setOpenCreatedGroupDialouge}
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
            disabled={selectedClasses.length === 0}
            sx={{ width: "200px" }}
            variant="outlined"
          >
            Thêm vào nhóm mới
          </Button>

          <Button
            onClick={() => {
              setOpenCreatedGroupDialouge(true);
            }}
            disabled={selectedClasses.length === 0}
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
