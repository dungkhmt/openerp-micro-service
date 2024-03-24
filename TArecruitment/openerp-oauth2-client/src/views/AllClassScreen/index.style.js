export const styles = {
  dropdown: {
    width: "10%",
    position: "absolute",
    zIndex: 1,
    margin: "0.5%",
  },
  selection: {
    maxHeight: 148,
    overflowY: "scroll",
  },
};

export const dialogStyle = {
  btn: { width: 100 },
  dialogContent: (theme) => ({ width: 550 }),
  actions: (theme) => ({ paddingRight: theme.spacing(2) }),
};
