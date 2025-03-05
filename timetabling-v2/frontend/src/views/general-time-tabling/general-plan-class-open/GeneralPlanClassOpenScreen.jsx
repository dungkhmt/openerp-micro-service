import { useEffect, useState } from "react";
import { request } from "api";
import { toast } from "react-toastify";
import GeneralSemesterAutoComplete from "../common-components/GeneralSemesterAutoComplete";
import { Button } from "@mui/material";
import InputFileUpload from "../general-upload/components/InputFileUpload";
import ClassOpenPlanTable from "./components/ClassOpenPlanTable";

const GeneralPlanClassOpenScreen = () => {
  const [selectedSemester, setSelectedSemester] = useState(null);
  const [planClasses, setPlanClasses] = useState([]);
  const [selectedFile, setSelectedFile] = useState(null);
  const [isOpenDialog, setOpenDialog] = useState(false);
  const [isImportLoading, setImportLoading] = useState(false);


  function getPlanClass(){
    //setImportLoading(true);
    request(
      "get",
      `/plan-general-classes/?semester=${selectedSemester.semester}`,
      (res) => {
        setPlanClasses(res.data);
        //setImportLoading(false);
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
  useEffect(() => {
    if (selectedSemester) {

      getPlanClass();
    }
  }, [selectedSemester]);

  const handleImportExcel = () => {
    if (selectedFile) {
      setImportLoading(true);
      const formData = new FormData();
      formData.append("file", selectedFile);
      
      const config = {
        headers: {
          'Content-Type': 'multipart/form-data',
          'Accept': 'application/json'
        }
      };

      request(
        "post",
        `/excel/upload-plan?semester=${selectedSemester?.semester}&createclass=T`,
        (res) => {
          setImportLoading(false);
          toast.success("Upload file thành công!");
          console.log(res?.data);
          setPlanClasses(res?.data);
        },
        (err) => {
          if(err.response?.status === 410) {
            toast.error(err.response.data);
          } else {
            toast.error("Có lỗi khi upload file!");
          }
          setImportLoading(false);
          console.log(err);
        },
        formData,
        config
      );
    }
  };

  function clearPlan(){
    let body = {
      semesterId: selectedSemester.semester
    }
    setImportLoading(true);
    request(
      "post",
      `/plan-general-classes/clear-plan`,
      (res) => {
        toast.success("Xóa kế hoạch mở lớp thành công!");
        console.log(res?.data);
        getPlanClass();
        setImportLoading(false);
      },
      (err) => {
        if(err.response.status === 410) {
          toast.error(err.response.data);
        } else {
          toast.error("Có lỗi khi clear");
        }
        
        console.log(err);
      },
      body,
      
    );
  }
  return (
    <div>
      <div className="flex flex-row justify-between mb-4">
        <GeneralSemesterAutoComplete
          selectedSemester={selectedSemester}
          setSelectedSemester={setSelectedSemester}
        />
        <div className="flex flex-col justify-end gap-2 ">
          <div className="flex flex-row gap-2">
            {/* <Button
              color="primary"
              disabled={selectedSemester === null}
              variant="contained"
            >
              Thêm lớp kế hoạch mới
            </Button> */}
          </div>
          <div>
            <Button
              color="primary"
              disabled={selectedSemester === null}
              variant="contained"
              onClick={clearPlan}
            >
              XÓA
            </Button> 
          </div>
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
        setClasses={setPlanClasses}
      />
    </div>
  );
};

export default GeneralPlanClassOpenScreen;
