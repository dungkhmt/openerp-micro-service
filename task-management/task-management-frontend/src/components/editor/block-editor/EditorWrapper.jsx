import { Box, styled } from "@mui/material";
import { BADGE_COLORS } from "./shema/style/badge";
import { BANNER_COLORS } from "./shema/block/banner";

export const EditorWrapper = styled(Box)(({ theme }) => ({
  "& .bn-container": {
    "--bn-font-family": theme.typography.fontFamily,
    fontSize: "14px",

    "& .bn-editor": {
      padding: theme.spacing(0, 5),
    },

    '& .bn-block-content[data-content-type="heading"]': {
      padding: theme.spacing(2, 0),
    },

    '& .bn-block-content[data-level="1"]': {
      "--level": "1.875rem",

      "& h1": {
        fontWeight: "650 !important",
      },
    },

    '& .bn-block-content[data-level="2"]': {
      "--level": "1.563rem",

      "& h2": {
        fontWeight: "650 !important",
      },
    },

    '& .bn-block-content[data-level="3"]': {
      "--level": "1.25rem",

      "& h3": {
        fontWeight: "600 !important",
      },
    },

    '& .bn-block-content[data-level="4"]': {
      "--level": "1.125rem",

      "& h4": {
        fontWeight: "600 !important",
      },
    },

    '& .bn-block-content[data-content-type="paragraph"]': {
      fontSize: "0.875rem",
      lineHeight: "1.2",
      backgroundColor: "transparent",

      "& p": {
        fontWeight: 400,
      },
    },
    "& p.bn-inline-content": {
      display: "inline-block",
      textAlign: "center",
      lineHeight: "normal",
    },
    ...BANNER_COLORS.reduce(
      (acc, color) => ({
        ...acc,
        [`& .bn-block-content[data-content-type="banner"][data-color="${color}"]`]:
          {
            padding: theme.spacing(4),
            borderRadius: "3px",
            backgroundColor: `var(--bn-colors-highlights-${color}-background)`,
            // border: `0.5px solid var(--bn-colors-highlights-${color}-text)`,
          },
      }),
      {}
    ),
    "& .mention": {
      display: "inline-flex",
      alignItems: "center",
      gap: 1,
      textDecoration: "underline",
      backgroundColor: theme.palette.action.selected,
      borderRadius: "4px",
      padding: 2,
      cursor: "pointer",

      "&:hover": {
        backgroundColor: theme.palette.action.hover,
        color: theme.palette.info.main,
        fontWeight: 600,
        "& svg": {
          color: theme.palette.primary.main,
        },
      },
    },
    "& .check-list": {
      display: "flex",
      alignItems: "center",
      justifyContent: "flex-start",
      fontSize: "14px",

      "&[data-check-list='checked']": {
        textDecoration: "line-through",
        color: theme.palette.text.disabled,
      },

      "& .checkbox": {
        display: "inline-flex",
        alignItems: "center",
        justifyContent: "center",
        marginRight: theme.spacing(1),
        marginTop: theme.spacing(0.5),
        width: 14,
        height: 14,
        borderRadius: "50%",
        border: `1px solid ${theme.palette.divider}`,
        cursor: "pointer",

        "& svg": {
          color: theme.palette.success.main,
        },
      },
    },
    ...BADGE_COLORS.reduce(
      (acc, color) => ({
        ...acc,
        [`.badge[data-value="${color}"]`]: {
          padding: theme.spacing(0.5, 2),
          borderRadius: "8px",
          backgroundColor: `var(--bn-colors-highlights-${color}-background)`,
          lineHeight: "100%",
        },
      }),
      {}
    ),
  },
  "& .mantine-Menu-dropdown:not(.bn-slash-menu)": {
    maxHeight: "500%",
  },
}));
