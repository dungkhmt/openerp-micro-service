import { pxToRem } from "../utils/typography";

const FONT_PRIMARY = `-apple-system, "Segoe UI", BlinkMacSystemFont, "Roboto", "Oxygen",
"Ubuntu", "Cantarell", "Fira Sans", "Droid Sans", "Helvetica Neue",
sans-serif`;

const typography = {
  fontFamily: FONT_PRIMARY,
  fontWeightRegular: 500,
  fontWeightMedium: 600,
  fontWeightBold: 700,

  // Headings
  h1: {
    fontSize: pxToRem(64),
    lineHeight: 64 / 64,
    fontWeight: 600,
  },
  h2: {
    fontSize: pxToRem(48),
    lineHeight: 48 / 48,
    fontWeight: 600,
  },
  h3: {
    fontSize: pxToRem(40),
    lineHeight: 48 / 40,
    fontWeight: 600,
  },
  h4: {
    fontSize: pxToRem(32),
    lineHeight: 40 / 32,
    fontWeight: 600,
  },
  h5: {
    fontSize: pxToRem(24),
    lineHeight: 32 / 24,
    fontWeight: 600,
  },
  h6: {
    fontSize: pxToRem(16),
    lineHeight: 24 / 16,
    fontWeight: 600,
  },

  // Other
  button: {
    fontSize: pxToRem(16),
    fontWeight: 700,
    lineHeight: 24 / 16,
    textTransform: "capitalize",
  },

  // Content XL
  contentXlSemiBold: {
    fontSize: pxToRem(24),
    lineHeight: 32 / 24,
    fontWeight: 600,
  },
  contentXlRegular: {
    fontSize: pxToRem(24),
    lineHeight: 32 / 24,
    fontWeight: 500,
  },

  contentLBold: {
    fontSize: pxToRem(20),
    lineHeight: 28 / 24,
    fontWeight: 700,
  },

  contentLRegular: {
    fontSize: pxToRem(20),
    lineHeight: 28 / 24,
    fontWeight: 500,
  },

  contentMBold: {
    fontSize: pxToRem(16),
    lineHeight: 24 / 16,
    fontWeight: 700,
  },
  contentMLink: {
    fontSize: pxToRem(16),
    lineHeight: 24 / 16,
    fontWeight: 700,
    textDecoration: "underline",
  },
  contentMRegular: {
    fontSize: pxToRem(16),
    lineHeight: 24 / 16,
    fontWeight: 500,
  },

  contentSBold: {
    fontSize: pxToRem(14),
    lineHeight: 20 / 14,
    fontWeight: 700,
  },
  contentSLink: {
    fontSize: pxToRem(14),
    lineHeight: 20 / 14,
    fontWeight: 700,
    textDecoration: "underline",
  },
  contentSRegular: {
    fontSize: pxToRem(14),
    lineHeight: 20 / 14,
    fontWeight: 500,
  },

  contentXsBold: {
    fontSize: pxToRem(12),
    lineHeight: 16 / 12,
    fontWeight: 700,
  },
  contentXsSemiBold: {
    fontSize: pxToRem(12),
    lineHeight: 16 / 12,
    fontWeight: 600,
  },
  contentXsLink: {
    fontSize: pxToRem(12),
    lineHeight: 16 / 12,
    fontWeight: 700,
    textDecoration: "underline",
  },
  contentXsRegular: {
    fontSize: pxToRem(12),
    lineHeight: 16 / 12,
    fontWeight: 500,
  },

  content2xsBold: {
    fontSize: pxToRem(10),
    lineHeight: 16 / 10,
    fontWeight: 700,
  },
  content2xsRegular: {
    fontSize: pxToRem(10),
    lineHeight: 16 / 10,
    fontWeight: 500,
  },

  content3xsRegular: {
    fontSize: pxToRem(8),
    lineHeight: 12 / 8,
    fontWeight: 500,
  },

  break: {
    fontSize: "inherit",
    lineHeight: "inherit",
    fontWeight: "inherit",
  },
};

export default typography;
