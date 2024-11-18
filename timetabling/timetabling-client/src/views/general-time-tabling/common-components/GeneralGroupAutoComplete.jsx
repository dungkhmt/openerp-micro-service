import { Autocomplete, TextField } from "@mui/material";
import React, { useState } from "react";
import { useGroups } from "../hooks/useGroups";

const GeneralGroupAutoComplete = ({ selectedGroup, setSelectedGroup }) => {
  const { loading: groupsLoading, error: groupsError, groups } = useGroups();

  return (
    <Autocomplete
      disablePortal
      loadingText="Loading..."
      getOptionLabel={(option) => option && option.groupName}
      onChange={(e, group) => {
        console.log(group);
        setSelectedGroup(group);
      }}
      value={selectedGroup}
      options={groups}
      sx={{ width: 200 }}
      renderInput={(params) => <TextField {...params} label="Chọn nhóm" />}
      PopperProps={{
        popperOptions: {
          modifiers: [
            {
              name: "preventOverflow",
              enabled: true,
              options: {
                altAxis: true,
              },
            },
          ],
        },
      }}
    />
  );
};

export default GeneralGroupAutoComplete;
