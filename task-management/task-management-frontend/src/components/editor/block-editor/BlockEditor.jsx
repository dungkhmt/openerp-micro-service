import { filterSuggestionItems } from "@blocknote/core";
import "@blocknote/core/fonts/inter.css";
import {
  BlockNoteView,
  DragHandleButton,
  FormattingToolbarController,
  SideMenu,
  SideMenuController,
  SuggestionMenuController,
  getDefaultReactSlashMenuItems,
  useCreateBlockNote,
} from "@blocknote/react";
import "@blocknote/react/style.css";
import { Icon } from "@iconify/react";
import { isFunction } from "lodash";
import PropTypes from "prop-types";
import { forwardRef } from "react";
import { EditorWrapper } from "./EditorWrapper";
// import { insertBadge } from "./shema/style/badge";
import { insertBanner } from "./shema/block/banner";
import { insertCheckList } from "./shema/block/check-list";
// import { insertMagicAi } from "./shema/openai";
import { schema as defaultSchema } from "./shema/schema";
import { CustomToolbar } from "./toolbar/CustomToolbar";

const cleanProps = (props) => {
  const clean = {};

  if (props === undefined) return {};

  for (const key in props) {
    if (
      props[key] !== undefined &&
      props[key] !== null &&
      props[key] !== "default"
    ) {
      clean[key] = props[key];
    }
  }

  return Object.entries(clean).length > 0 ? { props: clean } : {};
};

export const cleanDoc = (docs) =>
  docs.map((doc) => {
    if (Object.entries(doc).length <= 0) return doc;
    // eslint-disable-next-line no-unused-vars
    const { id: _, children, props, ...rest } = doc;
    return {
      ...rest,
      ...(children?.length > 0 ? { children } : {}),
      ...cleanProps(props),
      // TODO: remove when the content is table body
      content: Array.isArray(rest.content)
        ? rest.content?.map((content) => {
            if (
              typeof content === "object" &&
              content.type === "text" &&
              (!content.styles || Object.entries(content.styles).length === 0)
            ) {
              return content.text;
            } else {
              return content;
            }
          })
        : rest.content,
    };
  });

const BlockEditor = forwardRef(function BlockEditor(
  {
    document,
    setDocument,
    toggle,
    memberList = [],
    schema = defaultSchema,
    getCustomSuggestionItems,
  },
  ref
) {
  const editor = useCreateBlockNote(
    {
      initialContent: document.length > 0 ? document : undefined,
      placeholders: {
        default: "Nhập văn bản hoặc gõ '/' để thêm nội dung mới, @ để mention",
        heading: "Nhập tiêu đề",
        checklist: "Nhập mục cần kiểm tra",
        banner: "Nhập thông báo",
      },
      schema,
    },
    [toggle]
  );

  const getMentionMenuItems = (editor) => {
    return memberList.map(({ member }) => ({
      title: member.id ?? "",
      onItemClick: () => {
        editor.insertInlineContent([
          "\u200B", // add a zero-width space to prevent the mention from being merged with the previous text
          {
            type: "mention",
            props: {
              user: member.id ?? "unknown",
              fullname: `${member?.firstName ?? ""} ${member?.lastName ?? ""}`,
            },
          },
          " ", // add a space after the mention
        ]);
      },
      group: "Mentions",
      icon: <Icon icon="bi:people-circle" />,
      subtext: `${member?.firstName ?? ""} ${member?.lastName ?? ""}`,
    }));
  };

  return (
    <EditorWrapper>
      <BlockNoteView
        editor={editor}
        theme={"light"}
        sideMenu={false}
        formattingToolbar={false}
        slashMenu={false}
        ref={ref}
        onChange={(editor) => setDocument(editor.document)}
      >
        <SuggestionMenuController
          triggerCharacter={"/"}
          getItems={async (query) =>
            // Gets all default slash menu items and `insertAlert` item.
            filterSuggestionItems(
              [
                ...(isFunction(getCustomSuggestionItems)
                  ? getCustomSuggestionItems(editor)
                  : []),
                // insertMagicAi(editor),
                ...getDefaultReactSlashMenuItems(editor),
                ...insertBanner(editor),
                // ...insertBadge(editor),
                insertCheckList(editor),
              ],
              query
            )
          }
          // suggestionMenuComponent={CustomSuggestionMenu}
        />
        <SuggestionMenuController
          triggerCharacter={"@"}
          getItems={async (query) =>
            // Gets the mentions menu items
            filterSuggestionItems(getMentionMenuItems(editor), query)
          }
        />
        <SideMenuController
          sideMenu={(props) => (
            <SideMenu {...props}>
              <DragHandleButton {...props} />
            </SideMenu>
          )}
        />
        <FormattingToolbarController
          formattingToolbar={() => <CustomToolbar />}
        />
      </BlockNoteView>
    </EditorWrapper>
  );
});

BlockEditor.propTypes = {
  document: PropTypes.array.isRequired,
  setDocument: PropTypes.func.isRequired,
  toggle: PropTypes.bool.isRequired,
  memberList: PropTypes.array,
  schema: PropTypes.object,
  getCustomSuggestionItems: PropTypes.func,
};

export default BlockEditor;
