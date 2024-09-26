import { createReactStyleSpec } from "@blocknote/react";
import { Icon } from "@iconify/react";

export const BADGE_COLORS = [
  "default",
  "green",
  "blue",
  "red",
  "yellow",
  "purple",
  "orange",
  "pink",
  "brown",
  "gray",
];

export const Badge = createReactStyleSpec(
  {
    type: "badge",
    propSchema: "string",
  },
  {
    render: (props) => {
      return <span data-badge className="badge" ref={props.contentRef}></span>;
    },
  }
);

export const insertBadge = (editor) =>
  BADGE_COLORS.map((color) => ({
    title: `Badge ${color.charAt(0).toUpperCase() + color.slice(1)}`,
    onItemClick: () => {
      editor.insertInlineContent([
        "\u200B",
        {
          type: "text",
          text: "\u200B",
          styles: {
            badge: color,
          },
        },
        " ",
      ]);
    },
    aliases: ["badge", color],
    group: "Badges",
    icon: <Icon icon="bi:badge-ad" data-text-color={color} fontSize={20} />,
  }));
