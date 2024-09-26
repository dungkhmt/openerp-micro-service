import {Badge} from "@material-ui/core";
import {withStyles} from "@material-ui/core/styles";

export const StyledBadge = withStyles((theme) => ({
  badge: {
    right: -10,
    top: 5,
    border: `2px solid ${theme.palette.background.paper}`,
    padding: "0 4px",
  },
}))(Badge);
