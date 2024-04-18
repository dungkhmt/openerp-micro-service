import React, { useState } from "react";
import GeneralScheduleTable from "./components/GeneralScheduleTable";
import { Autocomplete, Button, Input, TextField } from "@mui/material";
import { Search } from "@mui/icons-material";
import GeneralGroupAutoComplete from "../common-components/GeneralGroupAutoComplete";
import GeneralSemesterAutoComplete from "../common-components/GeneralSemesterAutoComplete";
import { request } from "api";

const GeneralScheduleScreen = () => {
  const [selectedSemester, setSelectedSemester] = useState(null);
  const [selectedGroup, setSelectedGroup] = useState(null);

  return (
    <div className="flex flex-col gap-4 w-full h-[700px]">
      <p className="flex justify-center text-lg text-blue-500">
        Xếp thời khóa biểu cho chương trình đại trà
      </p>
      <div className="">
        <GeneralSemesterAutoComplete
          selectedSemester={selectedSemester}
          setSelectedSemester={setSelectedSemester}
        />
      </div>
      <div className="flex flex-row justify-between">
        <GeneralGroupAutoComplete
          selectedGroup={selectedGroup}
          setSelectedGroup={setSelectedGroup}
        />
      </div>
      <GeneralScheduleTable
        selectedGroup={selectedGroup}
        semester={selectedSemester}
      />
    </div>
  );
};

export default GeneralScheduleScreen;
