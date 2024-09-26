import { defaultProps, insertOrUpdateBlock } from "@blocknote/core";
import { createReactBlockSpec } from "@blocknote/react";
import { Icon } from "@iconify/react";

export const BANNER_COLORS = [
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

export const Banner = createReactBlockSpec(
  {
    type: "banner",
    content: "inline",
    propSchema: {
      textAlignment: defaultProps.textAlignment,
      backgroundColor: defaultProps.backgroundColor,
      color: {
        default: "default",
        values: BANNER_COLORS,
      },
      textColor: defaultProps.textColor,
    },
  },
  {
    render: (props) => {
      return <div data-banner className="banner" ref={props.contentRef}></div>;
    },
  }
);

export const insertBanner = (editor) =>
  BANNER_COLORS.map((color) => ({
    title: `Banner ${color.charAt(0).toUpperCase() + color.slice(1)}`,
    onItemClick: () => {
      insertOrUpdateBlock(editor, {
        type: "banner",
        props: {
          color,
        },
      });
    },
    aliases: ["banner", "slogan", color],
    group: "Banners",
    icon: (
      <Icon
        icon="solar:bookmark-line-duotone"
        data-text-color={color}
        fontSize={20}
      />
    ),
  }));
