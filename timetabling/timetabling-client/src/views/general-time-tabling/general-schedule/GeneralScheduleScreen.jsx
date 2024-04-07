import React, { useState } from "react";
import GeneralScheduleTable from "./components/GeneralScheduleTable";
import { Autocomplete, Button, Input, TextField } from "@mui/material";
import { Search } from "@mui/icons-material";
import GeneralGroupAutoComplete from "../common-components/GeneralGroupAutoComplete";
import GeneralSemesterAutoComplete from "../common-components/GeneralSemesterAutoComplete";
import { request } from "api";

const GeneralScheduleScreen = () => {
  const [selectedFile, setSelectedFile] = useState(null);
  const [group, setGroup] = useState([]);
  const [selectedSemester, setSelectedSemester] = useState(null);

  const handleFileChange = (event) => {
    setSelectedFile(event.target.files[0]);
    if (selectedFile) {
      const formData = new FormData();
      formData.append('file', selectedFile);
      request("post", `/excel/upload-general?semester=20221`, (res)=> {
        console.log(res);
      }, (err)=>{
        console.log(err);
      }, formData, {
        "Content-Type": "multipart/form-data",
      });
    }
  };

  return (
    <div className="flex flex-col gap-4 w-full h-[700px]">
      <p className="flex justify-center text-lg text-blue-500">
        Xếp thời khóa biểu cho chương trình đại trà
      </p>
      <div className="">
        <GeneralSemesterAutoComplete selectedSemester={selectedSemester} setSelectedSemester={setSelectedSemester}/>
      </div>
      <div className="flex flex-row justify-between">
        <div className="flex flex-row justify-start items-center gap-2">
          <GeneralGroupAutoComplete />
          <Button>
            <Search />
          </Button>
        </div>
        <div className="flex flex-row justify-end gap-2">
          <input type="file" onChange={handleFileChange}/>
        </div>
      </div>
      <GeneralScheduleTable group={group} semester={selectedSemester} />
    </div>
  );
};

export default GeneralScheduleScreen;
