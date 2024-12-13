import { useState } from "react";
import { useClassesNoSchedule } from "../hooks/useClassesNoSchedule";
import AddNewGroupDialogue from "./components/AddNewGroupDialogue";
import AddCreatedGroupDialogue from "./components/AddCreatedGroupDialogue";
import GeneralSemesterAutoComplete from "../common-components/GeneralSemesterAutoComplete";
import { Button } from "@mui/material";
import GeneralGroupTable from "./components/GeneralGroupTable";

const GeneralGroupScreen = () => {
  const [selectedSemester, setSelectedSemester] = useState(null);
  const { loading, classes, setClasses } = useClassesNoSchedule(
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
        <GeneralSemesterAutoComplete
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
      <GeneralGroupTable
        handleSelectionModelChange={handleSelectionModelChange}
        classes={classes}
        dataLoading={loading}
      />
    </div>
  );
};

export default GeneralGroupScreen;
