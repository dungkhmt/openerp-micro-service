import SearchIcon from "@mui/icons-material/Search";
const { styled, InputBase } = require("@mui/material");

const Search = styled("div")(({ theme }) => ({
  // backgroundColor: AppColors.secondary,
  borderRadius: "10px",
  borderStyle: "solid",
  borderLeftWidth: "0px",
  borderRightWidth: "0px",
  borderTopWidth: "0px",
  borderBottomWidth: "1px",
  borderBottomRightRadius: 0,
  borderBottomLeftRadius: 0,
  borderColor: "gray",
  position: "relative",
  width: "100%",
  // [theme.breakpoints.up("sm")]: {
  //   width: "30%",
  // },
  // [theme.breakpoints.up("md")]: {
  //   width: "30%",
  // },
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
    // padding: theme.spacing(1, 1, 1, 0),
    paddingLeft: `calc(1em + ${theme.spacing(4)})`,
    transition: theme.transitions.create("width"),
  },
}));
const SearchBox = ({ value, setValue, sx }) => {
  return (
    <Search sx={sx}>
      <SearchIconWrapper>
        <SearchIcon sx={{ color: "grey" }} />
      </SearchIconWrapper>
      <Input
        placeholder="Tìm kiếm"
        value={value}
        onChange={(e) => setValue(e.target.value)}
        inputProps={{
          "aria-label": "search",
        }}
        sx={
          {
            // color: "white",
          }
        }
      />
    </Search>
  );
};
export default SearchBox;
