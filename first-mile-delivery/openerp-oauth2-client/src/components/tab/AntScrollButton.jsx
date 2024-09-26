import { TabScrollButton } from "@mui/material";
import { grey } from "@mui/material/colors";
// import { withStyles } from "@mui/material/styles";
import { styled } from "@mui/material/styles";

export const AntScrollButton = styled(TabScrollButton)(({ theme }) => ({
  opacity: 1,
  position: "absolute",
  height: 36,
  width: 36,
  marginTop: "6px",
  borderRadius: "50%",
  backgroundColor: grey[200],
  overflow: "hidden",
  "&.MuiTabs-scrollButtons": {
    "&:hover": { backgroundColor: "#ffffff" },
    "&:first-of-type": { zIndex: 1 },
    "&:last-of-type": { right: 0 },
  },

  // Another way to fix scroll button
  // transition: "width 0.5s",
  // "&.Mui-disabled": {
  //   width: 0,
  // },
}));

// export const AntScrollButton = withStyles((theme) => ({
//   root: {
//     opacity: 1,
//     position: "absolute",
//     height: 36,
//     width: 36,
//     marginTop: 6,
//     borderRadius: "50%",
//     backgroundColor: grey[200],
//     overflow: "hidden",
//     "&.MuiTabs-scrollButtons": {
//       "&:hover": { backgroundColor: "#ffffff" },
//       "&:first-of-type": { zIndex: 1 },
//       "&:last-of-type": { right: 0 },
//     },

//     // Another way to fix scroll button
//     // transition: "width 0.5s",
//     // "&.Mui-disabled": {
//     //   width: 0,
//     // },
//   },
// }))(TabScrollButton);
