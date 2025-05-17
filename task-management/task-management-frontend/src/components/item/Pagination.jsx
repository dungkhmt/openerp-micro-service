import React, { useEffect } from "react";
import { Select, MenuItem } from "@mui/material";
import "@/assets/css/Pagination.css";

const Pagination = ({
                      currentPage,
                      pageCount,
                      itemsPerPage,
                      onPageChange,
                      onItemsPerPageChange,
                    }) => {
  const [tempPage, setTempPage] = React.useState(currentPage + 1);

  useEffect(() => {
    setTempPage(currentPage + 1);
  }, [currentPage]);

  const handlePageInputKeyDown = (e) => {
    if (e.key === "Enter") {
      const target = parseInt(tempPage, 10) - 1;
      if (target >= 0 && target < pageCount) {
        onPageChange(target);
      }
    }
  };

  return (
    <div className="pagination">
      <div className="page-controls">
        <input
          type="number"
          min={1}
          max={pageCount}
          value={tempPage}
          onChange={(e) => setTempPage(e.target.value)}
          onKeyDown={handlePageInputKeyDown}
          className="page-input"
        />
        <span>of {pageCount} pages</span>
        <button
          onClick={() => onPageChange(currentPage - 1)}
          disabled={currentPage === 0}
          className="page-button"
          style={{fontSize: "22px", width: "45px", height: "32px", lineHeight: "30px", padding: 0, textAlign: "center"}}
        >
          &lsaquo;
        </button>
        <button
          onClick={() => onPageChange(currentPage + 1)}
          disabled={currentPage >= pageCount - 1}
          className="page-button"
          style={{fontSize: "22px", width: "45px", height: "32px", lineHeight: "30px", padding: 0, textAlign: "center"}}
        >
          &rsaquo;
        </button>
      </div>
      <div className="items-per-page">
        <Select
          value={itemsPerPage}
          onChange={(e) => onItemsPerPageChange(e.target.value)}
          displayEmpty
          className="items-per-page-select"
        >
          {[5, 10, 15, 20, 50].map((n) => (
            <MenuItem key={n} value={n}>
              {n}
            </MenuItem>
          ))}
        </Select>
        <span>items per page</span>
      </div>
    </div>
  );
};

export default Pagination;
