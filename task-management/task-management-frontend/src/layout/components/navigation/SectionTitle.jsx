import {
  styled,
  Divider,
  Typography,
  ListSubheader as MuiListSubheader,
} from "@mui/material";
import PropsTypes from "prop-types";

const ListSubheader = styled((props) => (
  <MuiListSubheader component="li" {...props} />
))(({ theme }) => ({
  lineHeight: 1,
  display: "flex",
  position: "static",
  marginTop: theme.spacing(7),
  marginBottom: theme.spacing(2),
  backgroundColor: "transparent",
}));

const TypographyHeaderText = styled(Typography)(({ theme }) => ({
  fontSize: "0.75rem",
  lineHeight: "normal",
  letterSpacing: "0.21px",
  textTransform: "uppercase",
  fontWeight: theme.typography.fontWeightMedium,
}));

const SectionTitle = (props) => {
  const { item, navHover, collapsedNavWidth, navCollapsed } = props;

  return (
    <ListSubheader
      className="nav-section-title"
      sx={{
        ...(navCollapsed && !navHover
          ? {
              py: 1.5,
              pr: (collapsedNavWidth - 24) / 8 - 1,
              pl: (collapsedNavWidth - 24) / 8 + 0.25,
            }
          : { px: 0, py: 1 }),
        mt: 2,
      }}
    >
      <Divider
        textAlign="left"
        sx={{
          m: "0 !important",
          lineHeight: "normal",
          ...(navCollapsed && !navHover
            ? {
                width: 22,
                borderColor: (theme) =>
                  `rgba(${theme.palette.customColors.main}, 0.3)`,
              }
            : {
                width: "100%",
                textTransform: "uppercase",
                "&:before, &:after": { top: 7, transform: "none" },
                "& .MuiDivider-wrapper": {
                  px: 2.5,
                  fontSize: "0.75rem",
                  letterSpacing: "0.21px",
                },
              }),
        }}
      >
        {navCollapsed && !navHover ? null : (
          <TypographyHeaderText noWrap sx={{ color: "text.disabled" }}>
            {item.sectionTitle}
          </TypographyHeaderText>
        )}
      </Divider>
    </ListSubheader>
  );
};

SectionTitle.propTypes = {
  item: PropsTypes.object.isRequired,
  navHover: PropsTypes.bool.isRequired,
  collapsedNavWidth: PropsTypes.number.isRequired,
  navCollapsed: PropsTypes.bool.isRequired,
};

export { SectionTitle };
