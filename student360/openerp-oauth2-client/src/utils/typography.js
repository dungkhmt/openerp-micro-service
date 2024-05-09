export const remToPx = (value) => Math.round(value * 16);

export const pxToRem = (value) => `${value / 16}rem`;

export const responsiveFontSizes = ({ sm, md, lg }) => ({
  '@media (min-width:600px)': {
    fontSize: pxToRem(sm),
  },
  '@media (min-width:900px)': {
    fontSize: pxToRem(md),
  },
  '@media (min-width:1200px)': {
    fontSize: pxToRem(lg),
  },
});
