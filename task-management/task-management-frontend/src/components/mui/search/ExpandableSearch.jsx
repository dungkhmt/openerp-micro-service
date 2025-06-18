import { useState, useRef, useEffect } from "react";
import { Box, TextField, IconButton } from "@mui/material";
import { Icon } from "@iconify/react";
import PropTypes from "prop-types";
import { useDebounce } from "../../../hooks/useDebounce";

const ExpandableSearch = ({
  initialExpanded = false,
  placeholder = "Tìm kiếm...",
  width = "200px",
  onSearchChange = () => {},
  onSearchSubmit = () => {},
  onClear = () => {},
  iconSize = 20,
  inputSx = {},
  containerSx = {},
  debounceDelay = 1000,
}) => {
  const [isExpanded, setIsExpanded] = useState(initialExpanded);
  const [searchTerm, setSearchTerm] = useState("");
  const debouncedSearchTerm = useDebounce(searchTerm, debounceDelay);
  const inputRef = useRef(null);
  const containerRef = useRef(null);

  useEffect(() => {
    onSearchChange(debouncedSearchTerm);
  }, [debouncedSearchTerm, onSearchChange]);

  useEffect(() => {
    if (isExpanded && inputRef.current) {
      inputRef.current.focus();
    }
  }, [isExpanded]);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (
        containerRef.current &&
        !containerRef.current.contains(event.target) &&
        !searchTerm
      ) {
        setIsExpanded(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [searchTerm]);

  const clearSearch = () => {
    setSearchTerm("");
    onClear();
    if (inputRef.current) {
      inputRef.current.focus();
    }
  };

  const handleChange = (event) => {
    const newSearchTerm = event.target.value;
    setSearchTerm(newSearchTerm);
    if (newSearchTerm && !isExpanded) {
      setIsExpanded(true);
    }
  };

  const handleKeyPress = (event) => {
    if (event.key === "Enter") {
      onSearchSubmit(searchTerm);
    }
  };

  const handleBlur = () => {
    if (!searchTerm) {
      setIsExpanded(false);
    }
  };

  return (
    <Box
      ref={containerRef}
      display="flex"
      alignItems="center"
      justifyContent="space-between"
      sx={{ ...containerSx }}
    >
      {isExpanded ? (
        <Box
          sx={{
            width: typeof width === "object" ? width : { xs: width },
          }}
        >
          <TextField
            fullWidth
            placeholder={placeholder}
            variant="outlined"
            value={searchTerm}
            onChange={handleChange}
            onKeyPress={handleKeyPress}
            onBlur={handleBlur}
            inputRef={inputRef}
            autoComplete="off"
            InputProps={{
              startAdornment: (
                <Icon
                  icon="mdi:search"
                  fontSize={iconSize}
                  style={{ ml: -2 }}
                />
              ),
              sx: {
                height: 36,
                gap: 1.5,
                borderRadius: 5,
                ...inputSx,
              },
              endAdornment: searchTerm && (
                <IconButton
                  onClick={clearSearch}
                  sx={{ padding: 0, marginRight: "-4px" }}
                >
                  <Icon icon="mdi:close" fontSize={iconSize} />
                </IconButton>
              ),
            }}
          />
        </Box>
      ) : (
        <IconButton
          onClick={() => setIsExpanded(true)}
          sx={{
            bgcolor: "grey.100",
            "&:hover": { bgcolor: "grey.300" },
          }}
        >
          <Icon icon="mdi:search" fontSize={iconSize} />
        </IconButton>
      )}
    </Box>
  );
};

ExpandableSearch.propTypes = {
  initialExpanded: PropTypes.bool,
  placeholder: PropTypes.string,
  width: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.number,
    PropTypes.object,
  ]),
  onSearchChange: PropTypes.func,
  onSearchSubmit: PropTypes.func,
  onClear: PropTypes.func,
  iconSize: PropTypes.number,
  inputSx: PropTypes.object,
  containerSx: PropTypes.object,
  debounceDelay: PropTypes.number,
};

export default ExpandableSearch;
