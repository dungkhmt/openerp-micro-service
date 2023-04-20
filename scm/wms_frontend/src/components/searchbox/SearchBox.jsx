import SearchIcon from "@mui/icons-material/Search";
import { AppColors } from "../../shared/AppColors";
const { styled, InputBase } = require("@mui/material");

const Search = styled("div")(({ theme }) => ({
  backgroundColor: AppColors.secondary,
  borderRadius: "6px",
  flex: 1,
  flexDirection: "row",
  position: "relative",
  [theme.breakpoints.up("sm")]: {
    width: "auto",
  },
}));
const SearchIconWrapper = styled("div")(({ theme }) => ({
  height: "100%",
  position: "absolute",
  display: "flex",
  padding: theme.spacing(0, 2),
  alignItems: "center",
}));
const Input = styled(InputBase)(({ theme }) => ({
  width: "100%",
  color: "inherit",
  "& .MuiInputBase-input": {
    padding: theme.spacing(1, 1, 1, 0),
    paddingLeft: `calc(1em + ${theme.spacing(4)})`,
    transition: theme.transitions.create("width"),
  },
}));
const SearchBox = ({ value, setValue }) => {
  return (
    <Search>
      <SearchIconWrapper>
        <SearchIcon sx={{ color: "white" }} />
      </SearchIconWrapper>
      <Input
        placeholder="Tìm kiếm"
        value={value}
        onChange={(e) => setValue(e.target.value)}
        inputProps={{
          "aria-label": "search",
        }}
        sx={{
          color: "white",
        }}
      />
    </Search>
  );
};
export default SearchBox;
