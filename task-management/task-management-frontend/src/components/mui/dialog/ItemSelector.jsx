import { useState, useEffect, useRef } from "react";
import {
  Box,
  Button,
  List,
  ListItemButton,
  Popover,
  TextField,
  Typography,
  InputAdornment,
  IconButton,
} from "@mui/material";
import { Icon } from "@iconify/react";
import PropTypes from "prop-types";

const ItemSelector = ({
  items,
  selectedItems,
  onSelectChange,
  handleSearch,
  renderItem,
  renderSelectedItem,
  placeholder,
  label,
  startIcon,
  idPopover,
  maxHeight = 200,
  maxWidth = 400,
  showCheckbox = true,
}) => {
  const [anchorEl, setAnchorEl] = useState(null);
  const [search, setSearch] = useState("");
  const [position, setPosition] = useState({
    anchorOrigin: { vertical: "bottom", horizontal: "left" },
    transformOrigin: { vertical: "top", horizontal: "left" },
  });

  const containerRef = useRef(null);

  const handleOpen = (event) => {
    setAnchorEl(event.currentTarget);
    const container = containerRef.current.getBoundingClientRect();
    const availableSpaceBelow = window.innerHeight - container.bottom;
    const availableSpaceAbove = container.top;

    if (
      availableSpaceBelow < maxHeight &&
      availableSpaceAbove > availableSpaceBelow
    ) {
      setPosition({
        anchorOrigin: { vertical: "top", horizontal: "left" },
        transformOrigin: { vertical: "bottom", horizontal: "left" },
      });
    } else {
      setPosition({
        anchorOrigin: { vertical: "bottom", horizontal: "left" },
        transformOrigin: { vertical: "top", horizontal: "left" },
      });
    }
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const open = Boolean(anchorEl);

  useEffect(() => {
    handleSearch(search);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [search]);

  return (
    <Box ref={containerRef}>
      <Box
        sx={{ display: "flex", justifyContent: "flex-start", height: "50px" }}
      >
        <Button
          variant="outlined"
          onClick={handleOpen}
          startIcon={startIcon}
          sx={{
            display: "flex",
            alignItems: "center",
            justifyContent: "flex-start",
            width: "auto",
            maxWidth: "100%",
            px: 4,
            py: 2,
            textTransform: "none",
            color: (theme) => theme.palette.text.secondary,
            borderRadius: 1,
            borderColor: "grey.400",
            "&:hover": {
              borderColor: "grey.200",
              backgroundColor: "grey.200",
              color: (theme) => theme.palette.text.primary,
              "& .hover-typography": {
                color: (theme) => theme.palette.text.primary,
              },
              cursor: "pointer",
            },
          }}
        >
          {selectedItems?.length > 0 ? (
            <Box sx={{ display: "flex", alignItems: "flex-start", gap: 1 }}>
              {renderSelectedItem(selectedItems)}
            </Box>
          ) : (
            <Typography
              className="hover-typography"
              sx={{
                fontSize: "1rem",
                color: (theme) => theme.palette.text.secondary,
              }}
            >
              {label}
            </Typography>
          )}
        </Button>
      </Box>

      <Popover
        id={idPopover}
        open={open}
        anchorEl={anchorEl}
        onClose={handleClose}
        anchorOrigin={position.anchorOrigin}
        transformOrigin={position.transformOrigin}
      >
        <Box sx={{ p: 2, height: 40, mb: 3 }}>
          <TextField
            autoFocus
            autoComplete="off"
            fullWidth
            placeholder={placeholder}
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <Icon icon="ri:search-line" fontSize={20} />
                </InputAdornment>
              ),
              sx: { height: 40 },
              endAdornment: search && (
                <IconButton
                  onClick={() => setSearch("")}
                  sx={{ padding: 0, marginRight: "-4px" }}
                >
                  <Icon icon="mdi:close" fontSize={20} />
                </IconButton>
              ),
            }}
          />
        </Box>

        {selectedItems.length > 0 && (
          <Typography
            variant="subtitle2"
            color="text.secondary"
            sx={{ pl: 5, pb: 1, fontSize: "0.9rem" }}
          >
            Đã chọn <strong>{selectedItems.length}</strong> mục
          </Typography>
        )}

        <List
          sx={{ maxHeight: maxHeight, overflowY: "auto", maxWidth: maxWidth }}
        >
          {items.length === 0 ? (
            <Box sx={{ p: 2 }}>
              <Typography
                variant="body2"
                color="text.secondary"
                sx={{ fontStyle: "italic", pl: 2 }}
              >
                Không tìm thấy kết quả nào
              </Typography>
            </Box>
          ) : (
            items.map((item) => {
              const isSelected = selectedItems?.includes(item);
              return (
                <ListItemButton
                  key={item.id}
                  onClick={() => onSelectChange(item)}
                  sx={{
                    height: 50,
                    bgcolor: isSelected ? "primary.background" : "transparent",
                    gap: 2,
                  }}
                >
                  {showCheckbox && (
                    <IconButton
                      sx={{ color: isSelected ? "primary.main" : "grey.500" }}
                    >
                      <Icon
                        icon={
                          isSelected
                            ? "ci:checkbox-check"
                            : "fluent:checkbox-unchecked-16-filled"
                        }
                        fontSize={24}
                      />
                    </IconButton>
                  )}
                  {renderItem(item)}
                </ListItemButton>
              );
            })
          )}
        </List>
      </Popover>
    </Box>
  );
};

ItemSelector.propTypes = {
  items: PropTypes.arrayOf(PropTypes.object),
  selectedItems: PropTypes.arrayOf(PropTypes.object),
  onSelectChange: PropTypes.func,
  handleSearch: PropTypes.func,
  renderItem: PropTypes.func,
  renderSelectedItem: PropTypes.func,
  placeholder: PropTypes.string,
  label: PropTypes.string,
  startIcon: PropTypes.element,
  idPopover: PropTypes.string,
  maxHeight: PropTypes.number,
  maxWidth: PropTypes.number,
  showCheckbox: PropTypes.bool,
};

export default ItemSelector;
