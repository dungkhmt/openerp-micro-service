const DefaultPalette = (mode) => {
  const whiteColor = "#FFF";
  const blackColor = "#000";
  const lightColor = "58, 53, 65";
  const darkColor = "231, 227, 252";
  const mainColor = mode === "light" ? lightColor : darkColor;

  const defaultBgColor = () => {
    return mode === "light" ? "#F4F5FA" : "#312D4B";
  };

  return {
    customColors: {
      dark: darkColor,
      main: mainColor,
      light: lightColor,
      primaryGradient: "#9C9FA0",
      bodyBg: mode === "light" ? "#F4F4FA" : "#28243D",
      trackBg: mode === "light" ? "#F0F2F8" : "#474360",
      avatarBg: mode === "light" ? "#F0EFF0" : "#3F3B59",
      darkBg: "#312D4B",
      lightBg: whiteColor,
      tableHeaderBg: mode === "light" ? "#F9FAFC" : "#3D3759",
    },
    mode: mode,
    common: {
      black: blackColor,
      white: whiteColor,
    },
    secondary: {
      background: "#f5f5f5",
      main: "#8A8D93",
      dark: "#777B82",
      contrastText: whiteColor,
    },
    primary: {
      background: "#e3f2fd",
      light: "#1986C2",
      main: "#1976d2",
      dark: "#1876E2",
      contrastText: whiteColor,
    },
    error: {
      background: "#ffebee",
      light: "#FF6166",
      main: "#FF4C51",
      dark: "#E04347",
      contrastText: whiteColor,
    },
    warning: {
      background: "#fff8e1",
      light: "#FFCA64",
      main: "#FFB400",
      dark: "#E09E00",
      contrastText: whiteColor,
    },
    info: {
      background: "#e3f2fd",
      light: "#9E69FD",
      main: "#9155FD",
      dark: "#804BDF",
      contrastText: whiteColor,
    },
    success: {
      background: "#e8f5e9",
      light: "#6AD01F",
      main: "#56CA00",
      dark: "#4CB200",
      contrastText: whiteColor,
    },
    grey: {
      50: "#FAFAFA",
      100: "#F5F5F5",
      150: "#F0F0F0",
      200: "#EEEEEE",
      300: "#E0E0E0",
      400: "#BDBDBD",
      500: "#9E9E9E",
      600: "#757575",
      700: "#616161",
      800: "#424242",
      900: "#212121",
      A100: "#F5F5F5",
      A200: "#EEEEEE",
      A400: "#BDBDBD",
      A700: "#616161",
    },
    text: {
      primary: `rgba(${mainColor}, 0.87)`,
      secondary: `rgba(${mainColor}, 0.6)`,
      disabled: `rgba(${mainColor}, 0.38)`,
    },
    divider: `rgba(${mainColor}, 0.12)`,
    background: {
      paper: mode === "light" ? whiteColor : "#312D4B",
      default: defaultBgColor(),
    },
    action: {
      active: `rgba(${mainColor}, 0.54)`,
      hover: `rgba(${mainColor}, 0.04)`,
      selected: `rgba(${mainColor}, 0.08)`,
      disabled: `rgba(${mainColor}, 0.26)`,
      disabledBackground: `rgba(${mainColor}, 0.12)`,
      focus: `rgba(${mainColor}, 0.12)`,
    },
  };
};

export default DefaultPalette;
