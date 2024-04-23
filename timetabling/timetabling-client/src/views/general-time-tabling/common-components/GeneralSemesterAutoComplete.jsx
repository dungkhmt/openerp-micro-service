import { Autocomplete, TextField } from '@mui/material'
import React, { useState } from 'react'
import { useSemesters } from '../hooks/useSemester';

const GeneralSemesterAutoComplete = ({selectedSemester, setSelectedSemester}) => {
    const { loading: semestersLoading, error: semestersError, semesters } = useSemesters();
    console.log(semesters);
    return (
      <div>
        <Autocomplete
          disablePortal
          loadingText="Loading..."
          getOptionLabel={(option) => option && option.semester}
          onChange={(e, semester) => {
            setSelectedSemester(semester);
          }}
          value={selectedSemester}
          options={semesters}
          sx={{ width: 200 }}
          renderInput={(params) => <TextField {...params} label="Chọn kỳ" />}
        />
      </div>
    );
}

export default GeneralSemesterAutoComplete