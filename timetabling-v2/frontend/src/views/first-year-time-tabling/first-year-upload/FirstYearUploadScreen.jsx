import { useState } from "react";
import { useGeneralSchedule } from "services/useGeneralScheduleData";
import { LoadingProvider } from "./contexts/LoadingContext";
import FirstYearSemesterAutoComplete from "../common-components/FirstYearSemesterAutoComplete";
import InputFileUpload from "./components/InputFileUpload";
import { Button } from "@mui/material";
import { FacebookCircularProgress } from "components/common/progressBar/CustomizedCircularProgress";
import FirstYearUploadTable from "./components/FirstYearUploadTable";

const FirstYearUploadScreen = () => {
  const [selectedFile, setSelectedFile] = useState(null);
  const { states, setters, handlers } = useGeneralSchedule();

  return (
    <LoadingProvider>
      <div className="flex flex-col gap-2">
        <div className="flex flex-col gap-2 items-end">
          <FirstYearSemesterAutoComplete
            selectedSemester={states.selectedSemester}
            setSelectedSemester={setters.setSelectedSemester}
          />
          <div className="flex flex-col gap-2 items-end">
            <InputFileUpload
              isUploading={states.isUploading}
              selectedSemester={states.selectedSemester}
              selectedFile={selectedFile}
              setSelectedFile={setSelectedFile}
              setClasses={setters.setClasses}
              submitHandler={() => handlers.handleUploadFile(selectedFile)}
            />
            <div className="flex">
              <Button
                startIcon={states.isDeleting ? <FacebookCircularProgress /> : null}
                sx={{ width: 290 }}
                disabled={states.isDeleting || !states.selectedSemester}
                onClick={handlers.handleDeleteClasses}
                variant="contained"
                color="error"
              >
                Xóa danh sách theo kỳ
              </Button>
            </div>
          </div>
        </div>
        <FirstYearUploadTable 
          classes={states.classes} 
          dataLoading={states.loading} 
        />
      </div>
    </LoadingProvider>
  );
};

export default FirstYearUploadScreen;
