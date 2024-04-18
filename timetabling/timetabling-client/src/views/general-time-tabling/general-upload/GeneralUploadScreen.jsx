import React, { useState } from "react";
import GeneralSemesterAutoComplete from "../common-components/GeneralSemesterAutoComplete";
import GeneralUploadTable from "./components/GeneralUploadTable";
import Button from "@mui/material/Button";
import InputFileUpload from "./components/InputFileUpload";
import { LoadingProvider } from "./contexts/LoadingContext";
import { useClasses } from "../hooks/useClasses";
import { toast } from "react-toastify";
import { request } from "api";
import { FacebookCircularProgress } from "components/common/progressBar/CustomizedCircularProgress";

const GeneralUploadScreen = () => {
  const [selectedFile, setSelectedFile] = useState(null);
  const [selectedSemester, setSelectedSemester] = useState(null);
  const { loading, error, classes, setClasses } = useClasses(
    null,
    selectedSemester
  );
  const [deleteLoading, setDeleteLoading] = useState(false);
  const handleDelete = () => {
    if (selectedSemester === null) return;
    setDeleteLoading(true);
    request(
      "delete",
      `/general-classes/?semester=${selectedSemester?.semester}`,
      (res) => {
        setDeleteLoading(false);
        setClasses([]);
        toast.info("Xóa danh sách lớp thành công");
      },
      (error) => {
        console.log(error);
        setDeleteLoading(false);
        toast.error("Xóa danh sách lớp thất bại!");
      }
    );
  };

  return (
    <LoadingProvider>
      <div className="flex flex-col gap-2">
        <div className="flex flex-col gap-2 items-end">
          <GeneralSemesterAutoComplete
            selectedSemester={selectedSemester}
            setSelectedSemester={setSelectedSemester}
          />
          <div className="flex flex-col gap-2 items-end">
            <InputFileUpload
              selectedSemester={selectedSemester}
              selectedFile={selectedFile}
              setSelectedFile={setSelectedFile}
              setClasses={setClasses}
            />
            <div className="flex">
              {deleteLoading && <FacebookCircularProgress />}
              <Button
                sx={{ width: 290 }}
                disabled={deleteLoading || selectedSemester === null}
                onClick={handleDelete}
                variant="contained"
                color="error"
              >
                Xóa danh sách theo kỳ
              </Button>
            </div>
          </div>
        </div>
        <GeneralUploadTable classes={classes} dataLoading={loading} />
      </div>
    </LoadingProvider>
  );
};

export default GeneralUploadScreen;
