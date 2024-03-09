import MDEditor from "@uiw/react-md-editor";
import React from "react";
import PropTypes from "prop-types";
import { processingNoti } from "../../utils/notification";
import { request } from "../../api";
import { config } from "../../config/config";

const CustomEditor = (props) => {
  const editorRef = React.useRef(null);
  const containerRef = React.useRef(null);

  const onPaste = (evt) => {
    const clipboardItems = evt.clipboardData.items;
    const items = [].slice.call(clipboardItems).filter(function (item) {
      // Filter the image items only
      return /^image\//.test(item.type);
    });
    if (items.length === 0) {
      return;
    }

    const item = items[0];
    const blob = item.getAsFile();

    const filename = `image-${Date.now()}.jpg`;

    const file = new File(
      [blob],
      filename,
      { type: "image/jpeg", lastModified: new Date().getTime() },
      "utf-8"
    );

    if (editorRef.current) {
      props.setValue(
        editorRef.current.markdown + "  \n" + `![uploading...](${filename})\n`
      );
    }

    uploadFile(file)
      .then((res) => {
        if (res?.data?.id) {
          const id = res.data.id;
          const img = `![${filename}](${config.url.API_URL}/content/img/${id})`;
          if (editorRef.current) {
            props.setValue(
              editorRef.current.markdown.replace(
                `![uploading...](${filename})`,
                img
              )
            );
          }
        }
      })
      .catch(() => {
        if (editorRef.current) {
          props.setValue(
            editorRef.current.markdown.replace(
              `![uploading...](${filename})`,
              ``
            )
          );
        }
      });
  };

  const uploadFile = (file) => {
    const body = {
      id: `${Date.now()}_${file.name}`,
    };
    const formData = new FormData();
    formData.append("inputJson", JSON.stringify(body));
    formData.append("file", file);

    return processingNoti(
      () =>
        request("post", "/content/create", () => {}, {}, formData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        }),
      {
        loading: "Đang tải lên ảnh...",
        success: "Tải lên ảnh thành công",
        error: "Tải lên ảnh thất bại",
      }
    );
  };

  React.useEffect(() => {
    const current = containerRef.current;
    if (current) {
      current.addEventListener("paste", onPaste);
    }

    return () => {
      if (current) {
        current.removeEventListener("paste", onPaste);
      }
    };
  }, [containerRef.current]);

  return (
    <div ref={containerRef}>
      <MDEditor ref={editorRef} {...props} />
    </div>
  );
};

CustomEditor.propTypes = {
  setValue: PropTypes.func,
};

export { CustomEditor };
