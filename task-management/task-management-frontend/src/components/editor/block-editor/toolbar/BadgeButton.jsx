import {
  ToolbarSelect,
  useBlockNoteEditor,
  useEditorContentOrSelectionChange,
  useSelectedBlocks,
} from "@blocknote/react";
import { Icon } from "@iconify/react";
import { useCallback, useMemo, useState } from "react";
import { BADGE_COLORS } from "../shema/style/badge";

function checkBadgeInSchema(badge, editor) {
  return (
    `${badge}` in editor.schema.styleSchema &&
    editor.schema.styleSchema[`${badge}`].type === `${badge}` &&
    editor.schema.styleSchema[`${badge}`].propSchema === "string"
  );
}

const badgeItems = BADGE_COLORS.map((color) => ({
  name: `Badge ${color.charAt(0).toUpperCase() + color.slice(1)}`,
  icon: (size) => (
    <Icon icon="bi:badge-ad" data-text-color={color} size={size ?? 20} />
  ),
  isSelected: (currentBadge) => currentBadge === color,
  color,
  type: "badge",
}));

const BadgeButton = () => {
  const editor = useBlockNoteEditor();

  const badgeInSchema = checkBadgeInSchema("badge", editor);

  const selectedBlocks = useSelectedBlocks(editor);

  const [currentBadge, setCurrentBadge] = useState(
    badgeInSchema ? editor.getActiveStyles().badge || "default" : "default"
  );

  const setBadge = useCallback(
    (color) => {
      if (!badgeInSchema) {
        throw Error(
          `Tried to set badge ${color}, but style does not exist in editor schema.`
        );
      }

      editor.focus();
      color === "default"
        ? editor.removeStyles({ badge: color })
        : editor.addStyles({ badge: color });
    },
    [editor, badgeInSchema]
  );

  const show = useMemo(() => {
    if (!badgeInSchema) {
      return false;
    }

    for (const block of selectedBlocks) {
      if (block.content !== undefined) {
        return true;
      }
    }

    return false;
  }, [badgeInSchema, selectedBlocks]);

  useEditorContentOrSelectionChange(() => {
    if (badgeInSchema) {
      setCurrentBadge(editor.getActiveStyles().badge || "default");
    }
  }, editor);

  const fullItems = useMemo(() => {
    const onClick = (item) => {
      editor.focus();

      setBadge(item.color);
    };

    return badgeItems.map((item) => ({
      text: item.name,
      icon: item.icon,
      onClick: () => onClick(item),
      isSelected: item.isSelected(currentBadge),
    }));
  }, [currentBadge, editor, setBadge]);

  if (!show) {
    return null;
  }

  return <ToolbarSelect items={fullItems} />;
};

export { BadgeButton };
