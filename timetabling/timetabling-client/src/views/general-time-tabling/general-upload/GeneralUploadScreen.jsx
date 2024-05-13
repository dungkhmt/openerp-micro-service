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
  const [isUploading, setUploading] = useState(false);
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

  const handleSubmitFile = () => {
    if (selectedFile) {
      setUploading(true);
      const formData = new FormData();
      formData.append("file", selectedFile);
      request(
        "post",
        `/excel/upload-general?semester=${selectedSemester?.semester}`,
        (res) => {
          setUploading(false);
          console.log(res?.data);
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
                  })
                );
                delete cloneObj.timeSlots;
                generalClasses.push(cloneObj);
              });
            }
          });
          console.log(generalClasses);
          setClasses(generalClasses);
          toast.success("Upload file thành công!");
        },
        (err) => {
          setUploading(false);
          toast.error("Có lỗi khi upload file!");
        },
        formData,
        {
          "Content-Type": "multipart/form-data",
        }
      );
    }
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
              isUploading={isUploading}
              selectedSemester={selectedSemester}
              selectedFile={selectedFile}
              setSelectedFile={setSelectedFile}
              setClasses={setClasses}
              submitHandler={handleSubmitFile}
            />
            <div className="flex">
              <Button
                startIcon={deleteLoading ? <FacebookCircularProgress /> : null}
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
