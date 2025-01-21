import React, {useEffect, useState} from 'react';
import {Editor} from "react-draft-wysiwyg";
import PropTypes from "prop-types";
import {ContentState, convertToRaw, EditorState} from "draft-js";
import draftToHtml from "draftjs-to-html";
import htmlToDraft from "html-to-draftjs";

export default function RichTextEditor(props) {
  const [editorState, setEditorState] = useState(createEditorStateFromHTML(props.content));
  const [changeContentFromInternal, setChangeContentFromInternal] = useState(false)

  useEffect(() => {
    if (changeContentFromInternal) {
      setChangeContentFromInternal(false)
    } else {
      setEditorState(createEditorStateFromHTML(props.content))
    }
  }, [props.content])

  function updateBindingContentOnEditorStateChange(newEditorState) {
    let htmlContent = draftToHtml(convertToRaw(newEditorState.getCurrentContent()))
    setEditorState(newEditorState)
    setChangeContentFromInternal(true)
    props.onContentChange(htmlContent);
  }

  function createEditorStateFromHTML(htmlContent) {
    const {contentBlocks, entityMap} = htmlToDraft(htmlContent)
    const contentState = ContentState.createFromBlockArray(contentBlocks, entityMap);
    return EditorState.createWithContent(contentState)
  }

  return (
    <Editor
      editorState={editorState}
      contentState={editorState.getCurrentContent()}
      handlePastedText={() => false}
      onEditorStateChange={updateBindingContentOnEditorStateChange}
      toolbarStyle={props.editorStyle.toolbar}
      editorStyle={props.editorStyle.editor}
      readOnly={props.readOnly}
    />
  );
}

RichTextEditor.propTypes = {
  content: PropTypes.string.isRequired,
  // onContentChange: PropTypes.func.isRequired,
  editorStyle: PropTypes.object
}

const DEFAULT_EDITOR_STYLE = {
  toolbar: {
    background: "#FBFBFB",
  },
  editor: {
    border: "1px solid darkgray",
    borderRadius: "2px",
    minHeight: "260px",
    padding: "8px"
  }
}

RichTextEditor.defaultProps = {
  editorStyle: DEFAULT_EDITOR_STYLE
}