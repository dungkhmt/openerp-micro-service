import React, { useState } from "react";
import GeneralScheduleTable from "./components/general-table/GeneralScheduleTable";
import { Autocomplete, Button, Input, TextField } from "@mui/material";
import { Search } from "@mui/icons-material";
import GeneralGroupAutoComplete from "./components/general-group-choice-box/GeneralGroupAutoComplete";
import GeneralSemesterAutoComplete from "./components/general-semester-choice-box/GeneralSemesterAutoComplete";
import { request } from "api";

const GeneralScheduleScreen = () => {
  const [selectedFile, setSelectedFile] = useState(null);
  const [group, setGroup] = useState([]);
  const [semeter, setSemeter] = useState([]);

  const handleFileChange = (event) => {
    setSelectedFile(event.target.files[0]);
    if (selectedFile) {
      const formData = new FormData();
      formData.append('file', selectedFile);
      request("post", "/excel/upload-general", (res)=> {
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
        <GeneralSemesterAutoComplete />
      </div>
      <div className="flex flex-row justify-between">
        <div className="flex flex-row justify-start items-center gap-2">
          <GeneralGroupAutoComplete />
          <Button>
            <Search />
          </Button>
        </div>
        <div className="flex flex-row justify-end gap-2">
          <Button variant="outlined">Tự động xếp</Button>
          <Button variant="outlined" color="green">
            Xuất Excel
          </Button>
          {/* <input type="file" name onChange={handleFileChange}/> */}
        </div>
      </div>
      <GeneralScheduleTable group={group} semeter={semeter} />
    </div>
  );
};

export default GeneralScheduleScreen;
