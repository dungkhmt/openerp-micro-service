import React, { useState } from "react";
import GeneralSemesterAutoComplete from "../common-components/GeneralSemesterAutoComplete";
import GeneralUploadTable from "./components/GeneralUploadTable";
import Button from "@mui/material/Button";
import InputFileUpload from "./components/InputFileUpload";
import { LoadingProvider } from "./contexts/LoadingContext";
import { useClasses } from "../hooks/useClasses";

const GeneralUploadScreen = () => {
  const [selectedFile, setSelectedFile] = useState(null);
  const [selectedSemester, setSelectedSemester] = useState(null);
  const { loading, error, classes, setClasses } = useClasses(null, selectedSemester);

  return (
    <LoadingProvider>
      <div className="flex flex-col gap-2">
        <div className="flex flex-col gap-2 items-end">
          <GeneralSemesterAutoComplete
            selectedSemester={selectedSemester}
            setSelectedSemester={setSelectedSemester}
          />
          <div className="flex flex-col gap-2 items-end">
            <Button
              disabled={selectedSemester === null}
              onClick={() => {}}
              variant="contained"
              color="error"
            >
              Xóa danh sách theo kỳ
            </Button>
            <InputFileUpload
              selectedSemester={selectedSemester}
              selectedFile={selectedFile}
              setSelectedFile={setSelectedFile}
            />
          </div>
        </div>
        <GeneralUploadTable classes={classes} dataLoading={loading}/>
      </div>
    </LoadingProvider>
  );
};

export default GeneralUploadScreen;
