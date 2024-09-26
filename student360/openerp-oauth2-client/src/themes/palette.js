import { alpha } from '@mui/material';

// SETUP COLORS
const GREY = {
  0: '#FFFFFF',
  100: '#FAFAFA',
  200: '#F0F0F0',
  300: '#E6E6E6',
  400: '#D1D1D1',
  500: '#BEBEBE',
  600: '#969696',
  700: '#6F6F6F',
  800: '#464646',
  900: '#1E1E1E',
};

const PRIMARY = {
  light: '#00e0fc',
  main: '#00acc1',
  dark: '#00899a',
  contrastText: GREY[100],
};

const SECONDARY = {
  light: '#322846',
  main: '#281E3C',
  dark: '#1E1432',
  contrastText: GREY[100],
};

const INFO = {
  light: '#648CDC',
  main: '#4C79D3',
  contrastText: GREY[100],
};

const SUCCESS = {
  light: '#36C081',
  main: '#32B478',
  contrastText: GREY[800],
};

const WARNING = {
  light: '#FFDC6C',
  main: '#FACD3C',
  contrastText: GREY[800],
};

const ERROR = {
  light: '#FA6464',
  main: '#FA5050',
  contrastText: GREY[100],
};

const CHART = {
  1: '#D21E46',
  2: '#DE542C',
  3: '#EF7E32',
  4: '#EABD38',
  5: '#F7F4BF',
  6: '#C7F9EE',
  7: '#60FDD2',
  8: '#1DE4BD',
  9: '#1AC9E6',
  10: '#19AADE',
  11: '#176BA0',
  12: '#29066B',
  13: '#7D3AC1',
  14: '#AF4BCE',
  15: '#DB4CB2',
  16: '#EB548C',
  17: '#EA7369',
  18: '#F0A58F',
  19: '#FFCEAE',
  20: '#FFBCC6',
  21: '#FA6464',
  22: '#E61E50',
};

const ADDITIONAL = {
  pink: {
    light: '#FBECEE',
    main: '#FDDBE0',
    medium: '#FFBCC6',
  },
  lilac: {
    light: '#F2E8F4',
    main: '#F0D6F5',
    medium: '#E6B2F0',
  },
  lavender: {
    light: '#EAEBF5',
    main: '#DBDDF7',
    medium: '#BBC0F4',
  },
  sky: {
    light: '#E7F2FA',
    main: '#D0E8F9',
    medium: '#A6D6F9',
  },
  mint: {
    light: '#E4F2F1',
    main: '#C7EBE8',
    medium: '#93DCD6',
  },
  green: {
    light: '#F1F6EB',
    main: '#E1EFD2',
    medium: '#C9E3AA',
  },
  yellow: {
    light: '#FBF6E5',
    main: '#FDE9CD',
    medium: '#FFD99F',
  },
};

const palette = {
  common: { black: '#000000', white: '#FFFFFF' },
  primary: PRIMARY,
  secondary: SECONDARY,
  info: INFO,
  success: SUCCESS,
  warning: WARNING,
  error: ERROR,
  active: PRIMARY,
  grey: GREY,
  divider: alpha(GREY[500], 0.24),
  chart: CHART,
  additional: ADDITIONAL,

  text: {
    primary: GREY[800],
    secondary: GREY[700],
    disabled: GREY[500],
    active: PRIMARY.main,
  },

  background: {
    paper: GREY[100],
    default: GREY[200],
  },

  action: {
    active: GREY[600],
    hover: alpha(GREY[500], 0.08),
    selected: alpha(GREY[500], 0.16),
    disabled: alpha(GREY[500], 0.8),
    disabledBackground: alpha(GREY[500], 0.24),
    focus: alpha(GREY[500], 0.24),
    hoverOpacity: 0.08,
    disabledOpacity: 0.48,
  },
};

export default palette;
