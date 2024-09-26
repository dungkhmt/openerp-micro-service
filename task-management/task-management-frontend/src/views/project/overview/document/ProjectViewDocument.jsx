import { isStyledTextInlineContent } from "@blocknote/core";
import { Icon } from "@iconify/react";
import { LoadingButton } from "@mui/lab";
import { Box, Typography } from "@mui/material";
import { useTour } from "@reactour/tour";
import PropTypes from "prop-types";
import { forwardRef, useEffect, useMemo, useRef, useState } from "react";
import toast from "react-hot-toast";
import { useDispatch, useSelector } from "react-redux";
import { DashboardCard } from "../../../../components/card/DashboardCard";
import { PreventPrompt } from "../../../../components/common/PreventFrompt";
import BlockEditor, {
  cleanDoc,
} from "../../../../components/editor/block-editor/BlockEditor";
import { updateProject } from "../../../../store/project";

const buildMembers = (members) =>
  members.map(({ member, roleId }) => ({
    cells: [
      [
        {
          type: "mention",
          props: {
            user: member.id ?? "unknown",
            fullname: `${member?.firstName ?? ""} ${member?.lastName ?? ""}`,
          },
        },
      ],
      roleId,
      member.email ?? "",
    ],
  }));

const getProjectTemplate = (project, members) => {
  return [
    {
      type: "heading",
      content: [project.name, " 👋"],
      props: {
        level: 1,
        textAlignment: "center",
      },
    },
    {
      type: "paragraph",
      content: [
        {
          type: "text",
          text: "ReactJs",
          styles: {
            badge: "green",
          },
        },
        " ",
        {
          type: "text",
          text: "Spring Boot",
          styles: {
            badge: "brown",
          },
        },
        " ",
      ],
    },
    {
      type: "banner",
      content: ["Welcome to ", project.name, " project"],
      props: {
        textAlignment: "center",
        color: "blue",
      },
    },
    {
      type: "paragraph",
      content: [
        {
          type: "text",
          text: "This is a project document template. You can customize it to fit your project needs.",
          styles: {
            italic: true,
            textColor: "gray",
          },
        },
      ],
    },
    {
      type: "heading",
      content: ["📝 Document"],
      props: {
        level: 3,
      },
    },
    {
      type: "paragraph",
      content: [
        {
          type: "link",
          content: "Link to project document",
          href: "https://example.com",
        },
      ],
    },
    {
      type: "paragraph",
    },
    {
      type: "bulletListItem",
      content: [
        {
          type: "text",
          text: "Functional Requirements",
          styles: {
            bold: true,
          },
        },
      ],
    },
    {
      type: "checklist",
      content: ["Check list 1"],
      props: {
        checked: "checked",
      },
    },
    {
      type: "checklist",
      content: ["Check list 2"],
    },
    {
      type: "paragraph",
    },
    {
      type: "bulletListItem",
      content: [
        {
          type: "text",
          text: "Technical Requirements",
          styles: {
            bold: true,
          },
        },
      ],
    },
    {
      type: "checklist",
      content: ["Check list 1"],
    },
    {
      type: "checklist",
      content: ["Check list 2"],
    },
    {
      type: "heading",
      content: ["✈️ Planning"],
      props: {
        level: 3,
      },
    },
    {
      type: "checklist",
      content: ["Create a project document"],
      props: {
        checked: "checked",
      },
    },
    {
      type: "checklist",
      content: ["Add project members"],
    },
    {
      type: "checklist",
      content: ["Set up project milestones"],
    },
    {
      type: "checklist",
      content: ["Create project tasks"],
    },
    {
      type: "heading",
      content: ["👥 Members"],
      props: {
        level: 3,
      },
    },
    {
      type: "paragraph",
      content: [
        "Here are the project members. You can mention them using the '@' symbol.",
      ],
    },
    {
      type: "table",
      content: {
        type: "tableContent",
        rows: [
          {
            cells: [
              [
                {
                  type: "text",
                  text: "Member",
                  styles: {
                    bold: true,
                  },
                },
              ],
              [
                {
                  type: "text",
                  text: "Role",
                  styles: {
                    bold: true,
                  },
                },
              ],
              [
                {
                  type: "text",
                  text: "Email",
                  styles: {
                    bold: true,
                  },
                },
              ],
            ],
          },
          ...buildMembers(members),
        ],
      },
    },
  ];
};

const insertProjectTemplate = (editor, project, members) => ({
  title: "Document template",
  onItemClick: () => {
    const currentBlock = editor.getTextCursorPosition().block;
    const template = getProjectTemplate(project, members);

    editor.insertBlocks(template, currentBlock, "after");
    if (
      Array.isArray(currentBlock.content) &&
      ((currentBlock.content.length === 1 &&
        isStyledTextInlineContent(currentBlock.content[0]) &&
        currentBlock.content[0].type === "text" &&
        currentBlock.content[0].text === "/") ||
        currentBlock.content.length === 0)
    )
      editor.removeBlocks([currentBlock]);
  },
  aliases: ["template", "project-template", "document"],
  group: "Template",
  icon: <Icon icon="bi:file-earmark-text" fontSize={20} />,
  subtext: "Insert project document template",
});

const parseDoc = (desc) => {
  if (!desc) return [];

  try {
    return JSON.parse(desc);
  } catch (error) {
    console.log("parse doc error");
    return [];
  }
};

const SaveAction = ({ disabled, onSave }) => {
  const [loading, setLoading] = useState(false);

  const handleSave = async () => {
    try {
      setLoading(true);
      await onSave?.();
      toast.success("Tài liệu đã được lưu thành công.");
    } catch (error) {
      console.error(error);
      toast.error("Có lỗi xảy ra khi lưu tài liệu.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <LoadingButton
      variant="text"
      startIcon={<Icon icon="material-symbols:save" fontSize={14} />}
      disabled={disabled}
      loading={loading}
      onClick={handleSave}
    >
      <Typography variant="caption" sx={{ textTransform: "capitalize" }}>
        Save
      </Typography>
    </LoadingButton>
  );
};

SaveAction.propTypes = {
  disabled: PropTypes.bool,
  onSave: PropTypes.func.isRequired,
};

const ProjectViewDocument = forwardRef(function ProjectViewDocument(
  { style, className, onMouseDown, onMouseUp, onTouchEnd, children, ...props },
  ref
) {
  const { members, project, fetchLoading } = useSelector(
    (state) => state.project
  );
  const dispatch = useDispatch();
  const { setIsOpen } = useTour();
  const [toggleDialog, setToggleDialog] = useState(false);

  const documentTemplate = useMemo(() => {
    return fetchLoading ? [] : parseDoc(project.description);
  }, [project, fetchLoading]);

  const editorRef = useRef(null);
  const [document, setDocument] = useState(documentTemplate);
  const [isEdited, setIsEdited] = useState(false);

  const onSave = async () => {
    const doc = cleanDoc(document);
    await dispatch(
      updateProject({
        id: project.id,
        data: { description: JSON.stringify(doc) },
      })
    );
    setIsEdited(false);
  };

  useEffect(() => {
    if (
      JSON.stringify(documentTemplate) !== JSON.stringify(cleanDoc(document))
    ) {
      setIsEdited(true);
    } else {
      setIsEdited(false);
    }
  }, [documentTemplate, document]);

  useEffect(() => {
    if (
      documentTemplate.length <= 0 &&
      window.localStorage.getItem("tour") !== "false"
    ) {
      setIsOpen(true);
      window.localStorage.setItem("tour", "false");
    }
  }, [setIsOpen, documentTemplate]);

  useEffect(() => {
    if (!fetchLoading) {
      setDocument(documentTemplate);
      setToggleDialog(!toggleDialog);
    }
  }, [project, fetchLoading]);

  return (
    <DashboardCard
      title={project.name ?? ""}
      {...props}
      style={style}
      className={className}
      onMouseDown={onMouseDown}
      onMouseUp={onMouseUp}
      onTouchEnd={onTouchEnd}
      onDialogClose={() => setToggleDialog(!toggleDialog)}
      isDraggable={false}
      isRefreshable={false}
      action={() => <SaveAction disabled={!isEdited} onSave={onSave} />}
      ref={ref}
      sx={{ overflow: "unset" }}
    >
      <Box sx={{ height: "87%", overflow: "auto" }}>
        <BlockEditor
          document={document}
          setDocument={setDocument}
          toggle={toggleDialog}
          ref={editorRef}
          memberList={members}
          getCustomSuggestionItems={(editor) => [
            insertProjectTemplate(editor, project, members),
          ]}
        />
      </Box>
      <PreventPrompt hasUnsavedChanges={isEdited} />
      {children}
    </DashboardCard>
  );
});

ProjectViewDocument.propTypes = {
  style: PropTypes.object,
  className: PropTypes.string,
  onMouseDown: PropTypes.func,
  onMouseUp: PropTypes.func,
  onTouchEnd: PropTypes.func,
  children: PropTypes.node,
};

export { ProjectViewDocument };
