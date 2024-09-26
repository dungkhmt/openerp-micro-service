/* eslint-disable react/prop-types */
import { Box, MenuItem, Pagination, Select } from "@mui/material";

const CustomPagination = (props) => {
  return (
    <Box sx={{ display: "flex", gap: 2, mr: 4 }}>
      <Select
        size="small"
        variant="standard"
        value={props.rowsPerPage}
        onChange={(e) => props.onRowsPerPageChange(e.target.value)}
      >
        {props.pageSizeOptions?.map((size) => (
          <MenuItem key={size} value={size}>
            {size}
          </MenuItem>
        ))}
      </Select>
      <Pagination
        size="small"
        color="primary"
        page={props.page}
        onChange={props.onPageChange}
        {...props}
      />
    </Box>
  );
};

export { CustomPagination };
