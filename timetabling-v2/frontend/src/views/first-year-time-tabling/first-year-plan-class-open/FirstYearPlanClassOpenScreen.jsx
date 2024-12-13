import { useEffect, useState } from "react";
import { request } from "api";
import { toast } from "react-toastify";
import FirstYearSemesterAutoComplete from "../common-components/FirstYearSemesterAutoComplete";
import InputFileUpload from "../first-year-upload/components/InputFileUpload";
import ClassOpenPlanTable from "./components/ClassOpenPlanTable";

const FirstYearPlanClassOpenScreen = () => {
  const [selectedSemester, setSelectedSemester] = useState(null);
  const [planClasses, setPlanClasses] = useState([]);
  const [selectedFile, setSelectedFile] = useState(null);
  const [isOpenDialog, setOpenDialog] = useState(false);
  const [isImportLoading, setImportLoading] = useState(false);

  useEffect(() => {
    if (selectedSemester) {
      request(
        "get",
        `/plan-general-classes/?semester=${selectedSemester.semester}`,
        (res) => {
          setPlanClasses(res.data);
          toast.success("Truy vấn kế hoạch học tập thành công!");
        },
        (err) => {
          toast.error("Có lỗi khi truy vấn kế hoạch học tập");
        },
        null,
        null,
        null
      );
    }
  }, [selectedSemester]);

  const handleImportExcel = () => {
    if (selectedFile) {
      setImportLoading(true);
      const formData = new FormData();
      formData.append("file", selectedFile);
      request(
        "post",
        `/excel/upload-plan?semester=${selectedSemester?.semester}`,
        (res) => {
          setImportLoading(false);
          toast.success("Upload file thành công!");
          console.log(res?.data);
          setPlanClasses(res?.data);
        },
        (err) => {
          setImportLoading(false);
          toast.success("Có lỗi khi upload file!");
          console.log(err);
        },
        formData,
        {
          "Content-Type": "multipart/form-data",
        }
      );
    }
  };

  return (
    <div>
      <div className="flex flex-row justify-between mb-4">
        <FirstYearSemesterAutoComplete
          selectedSemester={selectedSemester}
          setSelectedSemester={setSelectedSemester}
        />
        <div className="flex flex-col justify-end gap-2">
          {/* <div className="flex flex-row gap-2">
            <Button
              color="error"
              disabled={selectedSemester === null}
              variant="contained"
            >
              Xóa kế hoạch mở lớp
            </Button>
            <Button disabled={selectedSemester === null} variant="contained">
              Lưu thay đổi
            </Button>
          </div> */}
          <div className="flex flex-row gap-2 justify-end">
            <InputFileUpload
              isUploading={isImportLoading}
              selectedFile={selectedFile}
              setSelectedFile={setSelectedFile}
              selectedSemester={selectedSemester}
              submitHandler={handleImportExcel}
            />
          </div>
        </div>
      </div>
      <ClassOpenPlanTable
        setOpenDialog={setOpenDialog}
        isOpenDialog={isOpenDialog}
        semester={selectedSemester?.semester}
        classes={planClasses}
      />
    </div>
  );
};

export default FirstYearPlanClassOpenScreen;
