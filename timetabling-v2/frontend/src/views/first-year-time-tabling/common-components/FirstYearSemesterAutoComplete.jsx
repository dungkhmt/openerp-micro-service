import { Autocomplete, TextField } from '@mui/material';
import { useSemesters } from '../hooks/useSemester';

const FirstYearSemesterAutoComplete = ({selectedSemester, setSelectedSemester}) => {
    const { semesters } = useSemesters();
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

export default FirstYearSemesterAutoComplete