import { IconButton, Tooltip } from "@mui/material";

export const Action = ({ extraAction, item, disabled, onActionCall }) => {
  return (
    <IconButton
      size="small"
      disabled={disabled}
      onClick={() => {
        onActionCall(item);
      }}
      sx={{
        color: extraAction.color,
      }}
    >
      <Tooltip title={extraAction.title}>{extraAction.icon}</Tooltip>
    </IconButton>
  );
};
