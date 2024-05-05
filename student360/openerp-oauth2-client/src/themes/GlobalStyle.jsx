import { GlobalStyles as MUIGlobalStyles, alpha } from '@mui/material';
import palette from './palette';
import typography from './typography';

const ReactDayPickerStyles = {
  '.rdp': {
    '--rdp-cell-size': '40px',
    '--rdp-caption-font-size': '24px',
    '--rdp-accent-color': palette.primary.main,
    '--rdp-background-color': palette.background.paper,
    '--rdp-accent-color-dark': palette.primary.dark,
    '--rdp-background-color-dark': palette.grey[800],
    margin: 24,

    '&-month': { display: 'flex', flexDirection: 'column', width: '100%' },
    '&-table': { width: '100%', maxWidth: '100%' },

    '&-head_cell': {
      color: palette.grey[600],
      textTransform: 'none',
      ...typography.contentMRegular,
    },

    '&-tbody': { 'tr:not(:last-child) td': { paddingBottom: 4 } },

    'rpd-weeknumber, .rdp-day': {
      borderRadius: 8,

      '&_today': {
        borderColor: palette.primary.main,
        color: `${palette.primary.main} !important`,

        '&:hover': {
          backgroundColor: `${alpha(palette.primary.main, 0.075)} !important`,
        },
        '&.rdp-day_selected:not(.rdp-day_range_middle)': {
          color: `${palette.common.white} !important`,
        },
      },
    },

    '.rdp-day_range_middle': {
      borderRadius: 0,
      backgroundColor: palette.additional.pink.light,
      color: palette.grey[800],
    },

    '.rdp-cell': {
      ':last-of-type .rdp-day': {
        borderTopRightRadius: 8,
        borderBottomRightRadius: 8,
      },
      ':first-of-type .rdp-day': {
        borderTopLeftRadius: 8,
        borderBottomLeftRadius: 8,
      },
    },
  },
};

const GlobalStyles = () => (
  <MUIGlobalStyles
    styles={{
      '*': {
        boxSizing: 'border-box',
      },

      ':root': {
        '--toastify-color-light': palette.grey[100],
        '--toastify-color-success': palette.success.main,
      },

      ...ReactDayPickerStyles,

      html: {
        margin: 0,
        padding: 0,
        width: '100%',
        height: '100%',
        WebkitOverflowScrolling: 'touch',
      },

      body: {
        padding: 0,
        width: '100%',
        height: '100%',
      },

      '#root': {
        width: '100%',
        height: '100%',
      },

      input: {
        '&[type=number]': {
          MozAppearance: 'textfield',
          '&::-webkit-outer-spin-button': {
            margin: 0,
            WebkitAppearance: 'none',
          },
          '&::-webkit-inner-spin-button': {
            margin: 0,
            WebkitAppearance: 'none',
          },
        },
      },

      img: {
        display: 'block',
        maxWidth: '100%',
      },

      ul: {
        margin: 0,
        padding: 0,
      },

      a: {
        textDecoration: 'none',
        color: 'inherit',
      },

      Toastify__toast: {
        borderRadius: 8,

        '&-container': {
          zIndex: 9999,
        },
      },
    }}
  />
);

export default GlobalStyles;
