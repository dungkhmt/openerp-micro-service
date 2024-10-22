import { Box, Typography } from "@mui/material";
import ActionsWithNotificationButton from "./ActionsWithNotificationButton";

export default function NotificationTitle({ showActionsMenu }) {
  return (
    <div style={{ margin: "20px 16px 12px", position: "relative" }}>
      <Box
        position="relative"
        display="flex"
        flexShrink={0}
        flexWrap="no-wrap"
        flexDirection="row"
        justifyContent="space-between"
        alignItems="center"
      >
        <Box
          position="relative"
          minWidth={0}
          maxWidth="100%"
          flexShrink={1}
          flexGrow={1}
          flexBasis="auto"
        >
          <Typography
            component="h1"
            variant="h5"
            sx={(theme) => ({
              fontWeight: theme.typography.fontWeightMedium,
            })}
            style={{ marginTop: "-7px", marginBottom: "-7px" }}
          >
            Thông báo
          </Typography>
        </Box>
        {showActionsMenu && <ActionsWithNotificationButton />}
      </Box>
    </div>
  );
}
