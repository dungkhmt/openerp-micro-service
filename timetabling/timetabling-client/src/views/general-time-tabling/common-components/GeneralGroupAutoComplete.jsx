import { Autocomplete, TextField } from "@mui/material";
import React, { useState } from "react";
import { useGroups } from "../hooks/useGroups";

const GeneralGroupAutoComplete = () => {
  const [selectedGroup, setSelectedGroup] = useState(null);
  const { loading: groupsLoading, error: groupsError, groups } = useGroups();

  return (
    <div>
      <Autocomplete
        disablePortal
        loadingText="Loading..."
        getOptionLabel={(option) => option && option.groupName}
        onChange={(e, group) => {
          setSelectedGroup(group);
        }}
        value={selectedGroup}
        options={groups}
        sx={{ width: 200 }}
        renderInput={(params) => <TextField {...params} label="Chọn nhóm" />}
      />
    </div>
  );
};

export default GeneralGroupAutoComplete;
