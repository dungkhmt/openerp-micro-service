const styles = {
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
};

export default styles;
