import React, { useState } from "react";
import GeneralScheduleTable from "./components/GeneralScheduleTable";
import { Autocomplete, Button, Input, TextField } from "@mui/material";
import { Search } from "@mui/icons-material";
import GeneralGroupAutoComplete from "../common-components/GeneralGroupAutoComplete";
import GeneralSemesterAutoComplete from "../common-components/GeneralSemesterAutoComplete";
import { request } from "api";

const GeneralScheduleScreen = () => {
  const [group, setGroup] = useState([]);
  const [selectedSemester, setSelectedSemester] = useState(null);

  

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
      </div>
      <GeneralScheduleTable group={group} semester={selectedSemester} />
    </div>
  );
};

export default GeneralScheduleScreen;
