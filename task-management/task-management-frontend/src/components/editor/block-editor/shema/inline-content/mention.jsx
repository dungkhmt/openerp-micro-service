import { createReactInlineContentSpec } from "@blocknote/react";
import { Icon } from "@iconify/react";
import { Tooltip } from "@mui/material";

export const Mention = createReactInlineContentSpec(
  {
    type: "mention",
    propSchema: {
      user: {
        default: "unknown",
      },
      fullname: {
        default: "Unknown User",
      },
    },
    content: "none",
  },
  {
    render: (props) => {
      return (
        <Tooltip title={props.inlineContent.props.fullname}>
          <span data-mention className="mention">
            <Icon icon="bi:people-circle" fontSize={14} />@
            {props.inlineContent.props.user}
          </span>
        </Tooltip>
      );
    },
  }
);
