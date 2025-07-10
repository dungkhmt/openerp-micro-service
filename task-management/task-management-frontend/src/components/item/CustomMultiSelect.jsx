import React, { useMemo } from 'react';
import {
  TextField,
  Chip,
  CircularProgress,
  Autocomplete,
  Checkbox,
  Typography,
  Box // Thêm Box để có thể tùy chỉnh renderOption
} from '@mui/material';

const CustomMultiSelect = ({
                             label,
                             options,
                             selectedValues,
                             onChange,
                             loading,
                             valueKey,
                             labelKey,
                             secondaryLabelKey,
                             placeholder,
                           }) => {

  const selectedOptions = useMemo(() =>
      selectedValues.map(value => options.find(option => option[valueKey] === value)).filter(Boolean)
    , [selectedValues, options, valueKey]);

  return (
    <Autocomplete
      multiple
      fullWidth
      size="small"
      disableCloseOnSelect
      options={options}
      loading={loading}
      loadingText="Đang tải..."
      noOptionsText="Không có kết quả"
      value={selectedOptions}
      isOptionEqualToValue={(option, value) => option[valueKey] === value[valueKey]}
      getOptionLabel={(option) => option[labelKey] || ''}
      onChange={(event, newValue) => {
        const newValues = newValue.map(option => option[valueKey]);
        onChange(newValues);
      }}
      renderOption={(props, option, { selected }) => (
        <li {...props} key={option[valueKey]}>
          <Checkbox checked={selected} size="small" sx={{ mr: 1 }} />
          <Box component="span" sx={{ flexGrow: 1 }}>
            <Typography variant="body2">{option[labelKey]}</Typography>
            {secondaryLabelKey && (
              <Typography variant="caption" sx={{ color: 'text.secondary', display: 'block' }}>
                ({option[secondaryLabelKey]})
              </Typography>
            )}
          </Box>
        </li>
      )}
      renderTags={(value, getTagProps) =>
        value.map((option, index) => (
          <Chip
            key={option[valueKey]}
            label={option[labelKey]}
            size="small"
            {...getTagProps({ index })}
          />
        ))
      }
      renderInput={(params) => (
        <TextField
          {...params}
          label={label}
          placeholder={selectedValues.length === 0 ? placeholder : ''}
          InputProps={{
            ...params.InputProps,
            endAdornment: (
              <>
                {loading ? <CircularProgress color="inherit" size={20} /> : null}
                {params.InputProps.endAdornment}
              </>
            ),
          }}
        />
      )}
    />
  );
};

export default CustomMultiSelect;