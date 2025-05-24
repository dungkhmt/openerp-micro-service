import React, { useEffect } from "react";
import { Select, MenuItem, TextField, Typography, Box, IconButton } from "@mui/material";
import NavigateBeforeIcon from '@mui/icons-material/NavigateBefore';
import NavigateNextIcon from '@mui/icons-material/NavigateNext';

const Pagination = ({
                      currentPage, // 0-indexed
                      pageCount,   // Total number of pages
                      itemsPerPage,
                      onPageChange,
                      onItemsPerPageChange,
                    }) => {
  const [tempPageInput, setTempPageInput] = React.useState(currentPage + 1);

  useEffect(() => {
    setTempPageInput(currentPage + 1);
  }, [currentPage]);

  const handlePageInputKeyDown = (e) => {
    if (e.key === "Enter") {
      const pageValue = parseInt(tempPageInput, 10);
      if (!isNaN(pageValue) && pageValue >= 1 && pageValue <= pageCount) {
        onPageChange(pageValue - 1);
      } else if (!isNaN(pageValue) && pageValue < 1) {
        onPageChange(0);
        setTempPageInput(1);
      } else if (!isNaN(pageValue) && pageValue > pageCount) {
        onPageChange(pageCount - 1);
        setTempPageInput(pageCount);
      } else {
        setTempPageInput(currentPage + 1);
      }
    }
  };

  const handleTempPageInputChange = (e) => {
    setTempPageInput(e.target.value);
  };

  // Display page count, ensuring it's at least 1 for UI text if backend sends 0
  const displayPageCount = Math.max(1, pageCount);
  const displayCurrentPage = pageCount === 0 ? 0 : currentPage + 1;

  return (
    <Box
      sx={{
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        p: { xs: 1, sm: 1.5 }, // Reduced padding slightly for a more compact feel
        flexWrap: 'wrap',
        gap: 2,
        borderTop: (theme) => `1px solid ${theme.palette.divider}`, // Adds a subtle separator
        mt: 2, // Margin top to separate from table
      }}
    >
      <Box sx={{ display: 'flex', alignItems: 'center', gap: { xs: 0.5, sm: 1 } }}>
        <Typography variant="body2" sx={{ mr: 1, color: 'text.secondary' }}>
          Rows per page:
        </Typography>
        <Select
          value={itemsPerPage}
          onChange={(e) => onItemsPerPageChange(parseInt(e.target.value, 10))}
          size="small"
          variant="outlined" // Standard variant
          sx={{ minWidth: 70 }}
        >
          {[5, 10, 15, 20, 50].map((n) => ( // Common items per page options
            <MenuItem key={n} value={n}>
              {n}
            </MenuItem>
          ))}
        </Select>
      </Box>

      <Box sx={{ display: 'flex', alignItems: 'center', gap: { xs: 0.5, sm: 1 } }}>
        <Typography variant="body2" sx={{ color: 'text.secondary' }}>
          Page
        </Typography>
        <TextField
          type="number"
          size="small"
          variant="outlined"
          value={tempPageInput}
          onChange={handleTempPageInputChange}
          onKeyDown={handlePageInputKeyDown}
          sx={{ width: '70px' }}
          inputProps={{
            min: 1,
            max: displayPageCount,
            style: { textAlign: 'center' },
          }}
          onFocus={(event) => event.target.select()}
          onWheel={(e) => e.target.blur()} // Prevent scroll-to-change on number input
        />
        <Typography variant="body2" sx={{ color: 'text.secondary' }}>
          of {displayPageCount}
        </Typography>
        <IconButton
          onClick={() => onPageChange(currentPage - 1)}
          disabled={currentPage === 0 || pageCount === 0}
          size="medium" // Standard size for better click target
          aria-label="previous page"
        >
          <NavigateBeforeIcon />
        </IconButton>
        <IconButton
          onClick={() => onPageChange(currentPage + 1)}
          disabled={currentPage >= pageCount - 1 || pageCount === 0}
          size="medium"
          aria-label="next page"
        >
          <NavigateNextIcon />
        </IconButton>
      </Box>
    </Box>
  );
};

export default Pagination;
