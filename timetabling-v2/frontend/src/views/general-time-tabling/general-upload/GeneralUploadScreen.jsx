import { useState } from "react";
import { LoadingProvider } from "./contexts/LoadingContext";
import GeneralSemesterAutoComplete from "../common-components/GeneralSemesterAutoComplete";
import InputFileUpload from "./components/InputFileUpload";
import { Button } from "@mui/material";
import { FacebookCircularProgress } from "components/common/progressBar/CustomizedCircularProgress";
import GeneralUploadTable from "./components/GeneralUploadTable";
import { useGeneralSchedule } from "services/useGeneralScheduleData";

const GeneralUploadScreen = () => {
  const [selectedFile, setSelectedFile] = useState(null);
  
  const { 
    states: { 
      selectedSemester,
      classesNoSchedule,
      isClassesNoScheduleLoading,
      isDeletingBySemester,
      isUploading 
    },
    setters: { 
      setSelectedSemester,
      setClassesNoSchedule
    },
    handlers: {
      handleDeleteBySemester,
      handleUploadFile
    }
  } = useGeneralSchedule();

  const handleSubmitFile = async () => {
    if (selectedFile) {
      await handleUploadFile(selectedFile);
      setSelectedFile(null);
    }
  };

  return (
    <LoadingProvider>
      <div className="flex flex-col gap-2">
        <div className="flex flex-row gap-2 justify-between">
          <GeneralSemesterAutoComplete
            selectedSemester={selectedSemester}
            setSelectedSemester={setSelectedSemester}
          />
          <div className="flex flex-col gap-2 items-end">
            <InputFileUpload
              isUploading={isUploading}
              selectedSemester={selectedSemester}
              selectedFile={selectedFile}
              setSelectedFile={setSelectedFile}
              submitHandler={handleSubmitFile}
            />
            <div className="flex">
              <Button
                startIcon={isDeletingBySemester ? <FacebookCircularProgress /> : null}
                sx={{ width: 290 }}
                disabled={isDeletingBySemester || !selectedSemester}
                onClick={handleDeleteBySemester}
                variant="contained"
                color="error"
              >
                Xóa danh sách theo kỳ
              </Button>
            </div>
          </div>
        </div>
        <GeneralUploadTable 
          setClasses={setClassesNoSchedule}
          classes={classesNoSchedule} 
          dataLoading={isClassesNoScheduleLoading} 
        />
      </div>
    </LoadingProvider>
  );
};

export default GeneralUploadScreen;
