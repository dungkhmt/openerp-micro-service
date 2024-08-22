import { useState, useMemo } from "react";

import { styled } from "@mui/material/styles";
import { Button, Menu, MenuItem, Rating } from "@mui/material";

import { MdOutlineStar } from "react-icons/md";
import { useTheme } from "@emotion/react";

const KeyboardArrowDownIcon = () => (
  <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
    <path
      fillRule="evenodd"
      clipRule="evenodd"
      d="M4.79289 8.54289C5.18342 8.15237 5.81658 8.15237 6.20711 8.54289L12 14.3358L17.7929 8.54289C18.1834 8.15237 18.8166 8.15237 19.2071 8.54289C19.5976 8.93341 19.5976 9.56658 19.2071 9.9571L13.4142 15.75C12.6332 16.531 11.3668 16.531 10.5858 15.75L4.79289 9.95711C4.40237 9.56658 4.40237 8.93342 4.79289 8.54289Z"
      fill="#464646"
    />
  </svg>
);

const StyledMenu = styled((props) => (
  <Menu
    anchorOrigin={{
      vertical: "bottom",
      horizontal: "right",
    }}
    transformOrigin={{
      vertical: "top",
      horizontal: "right",
    }}
    {...props}
  />
))(({ theme }) => ({
  "& .MuiPaper-root": {
    marginTop: theme.spacing(1),
    width: 203,
    "& .MuiMenuItem-root": {
      color: "",
      "& .MuiSvgIcon-root": {},
    },
  },
}));

const RatingIndexMenu = ({ value = 4.0, handleChangeRating, isDisable }) => {
  const priceNav = useMemo(() => [4.5, 4.0, 3.5, 3.0], []);

  const [anchorEl, setAnchorEl] = useState(null);
  const [selected, setSelected] = useState(value);

  const open = Boolean(anchorEl);

  const handleClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = (event) => {
    setAnchorEl(null);
  };
  const theme = useTheme();

  return (
    <div>
      <Button
        id="demo-customized-button"
        aria-controls={open ? "demo-customized-menu" : undefined}
        aria-haspopup="true"
        aria-expanded={open ? "true" : undefined}
        disableElevation
        onClick={handleClick}
        startIcon={<MdOutlineStar fill="#faaf00" />}
        endIcon={
          <KeyboardArrowDownIcon style={{ color: theme.palette.grey[600] }} />
        }
        disabled={isDisable}
        sx={{
          backgroundColor: theme.palette.grey[100],
          color: theme.palette.grey[800],
          minWidth: { xs: "164px", sm: "203px" },
          padding: { xs: "4px 16px", sm: "12px 16px" },
          gap: "8px",
          typography: { xs: "contentSRegular", sm: "contentMBold" },
          justifyContent: "space-between",
          border: `2px solid ${theme.palette.grey[600]}`,
        }}
      >
        {selected.toFixed(1)} & up
      </Button>
      <StyledMenu
        id="demo-customized-menu"
        MenuListProps={{
          "aria-labelledby": "demo-customized-button",
        }}
        anchorEl={anchorEl}
        open={open}
        onClose={handleClose}
      >
        {priceNav.map((item, index) => (
          <MenuItem
            key={index}
            onClick={(e) => {
              setSelected(item);
              handleClose(e);
              handleChangeRating(item);
            }}
          >
            <Rating size="small" value={item} precision={0.1} readOnly />
            <span style={{ marginLeft: "8px" }}>{item.toFixed(1)} & up</span>
          </MenuItem>
        ))}
      </StyledMenu>
    </div>
  );
};

export default RatingIndexMenu;
