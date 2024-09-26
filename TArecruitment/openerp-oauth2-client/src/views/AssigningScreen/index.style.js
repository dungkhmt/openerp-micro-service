const styles = {
  autoButton: {
    marginLeft: "1em",
    marginRight: "1em",
  },
  selection: {
    boxShadow: "none",
    ".MuiOutlinedInput-notchedOutline": { border: 0 },
    "&.MuiOutlinedInput-root:hover .MuiOutlinedInput-notchedOutline": {
      border: 0,
    },
    "&.MuiOutlinedInput-root.Mui-focused .MuiOutlinedInput-notchedOutline": {
      border: 0,
    },
    width: 145,
  },
  searchBox: {
    width: "20%",
  },
  tableToolBar: {
    marginLeft: "2em",
    marginBottom: "1em",
  },
  toolLine: {
    display: "flex",
    justifyContent: "space-between",
    paddingRight: "1em",
  },
  leftTool: {
    display: "flex",
    alignItems: "center",
  },
};

export default styles;
