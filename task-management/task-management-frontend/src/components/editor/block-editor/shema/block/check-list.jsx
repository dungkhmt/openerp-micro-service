import { defaultProps, insertOrUpdateBlock } from "@blocknote/core";
import { createReactBlockSpec } from "@blocknote/react";
import { Icon } from "@iconify/react";

export const CheckList = createReactBlockSpec(
  {
    type: "checklist",
    content: "inline",
    propSchema: {
      textAlignment: defaultProps.textAlignment,
      backgroundColor: defaultProps.backgroundColor,
      checked: {
        default: "unchecked",
        values: ["checked", "unchecked"],
      },
      textColor: defaultProps.textColor,
    },
  },
  {
    render: (props) => {
      const toggleCheck = () => {
        props.editor.updateBlock(props.block, {
          ...props.block,
          props: {
            ...props.block.props,
            checked:
              props.block.props.checked === "checked" ? "unchecked" : "checked",
          },
        });
      };

      return (
        <div data-check-list={props.block.props.checked} className="check-list">
          <span className="checkbox" onClick={toggleCheck}>
            {props.block.props.checked === "checked" && (
              <Icon icon="icon-park-solid:check-one" fontSize={20} />
            )}
          </span>
          <span className="content" ref={props.contentRef}></span>
        </div>
      );
    },
  }
);

export const insertCheckList = (editor) => ({
  title: `Check list`,
  onItemClick: () => {
    insertOrUpdateBlock(editor, {
      type: "checklist",
      props: {
        checked: "unchecked",
      },
    });
  },
  aliases: ["check-list", "todo"],
  group: "List",
  icon: <Icon icon="mage:checklist-note" fontSize={20} />,
});
