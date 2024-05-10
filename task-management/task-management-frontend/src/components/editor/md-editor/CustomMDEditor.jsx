import { Box, Divider, Typography } from "@mui/material";
import MDEditor from "@uiw/react-md-editor";
import PropTypes from "prop-types";
import React from "react";
import { useDropzone } from "react-dropzone";
import toast from "react-hot-toast";
import { request } from "../../../api";
import { config } from "../../../config/config";
import { processingNoti } from "../../../utils/notification";

const CustomMDEditor = (props) => {
  const editorRef = React.useRef(null);
  const containerRef = React.useRef(null);
  const inputRef = React.useRef(null);

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

  const handleChooseImage = (e) => {
    const file = e.target.files[0];
    if (!file) {
      return;
    }

    const filename = `image-${Date.now()}.jpg`;

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

  const { getRootProps } = useDropzone({
    multiple: false,
    accept: {
      "image/jpeg": [],
      "image/png": [],
      "image/webp": [],
      "image/heic": [],
      "image/jfif": [],
      "image/jpg": [],
      "image/gif": [],
    },
    noClick: true,
    onDrop: (acceptedFiles) => {
      // check if image file
      if (acceptedFiles[0].type.startsWith("image")) {
        handleChooseImage({ target: { files: acceptedFiles } });
      } else {
        toast.error("Chỉ hỗ trợ ảnh định dạng ảnh");
      }
    },
  });

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
    <Box
      sx={{
        backgroundColor: (theme) => theme.palette.action.hover,
        borderRadius: "5px",
        border: (theme) => `1px solid ${theme.palette.action.selected}`,
      }}
      {...getRootProps()}
    >
      <div ref={containerRef} style={{ padding: "4px 4px 0" }}>
        <MDEditor ref={editorRef} {...props} />
      </div>
      <Box sx={{ p: 1, display: "flex", gap: 3 }}>
        <Box
          sx={{
            display: "flex",
            alignItems: "center",
            gap: 2,
            borderRadius: "5px",
            textDecoration: "none",
            cursor: "pointer",
            p: (theme) => theme.spacing(1, 2),
            "&:hover": {
              backgroundColor: (theme) => theme.palette.action.selected,
            },
          }}
          component="a"
          href="https://guides.github.com/features/mastering-markdown/"
          target="_blank"
        >
          <svg
            aria-hidden="true"
            height="16"
            viewBox="0 0 16 16"
            version="1.1"
            width="16"
            data-view-component="true"
            fill="#6a737d"
          >
            <path d="M14.85 3c.63 0 1.15.52 1.14 1.15v7.7c0 .63-.51 1.15-1.15 1.15H1.15C.52 13 0 12.48 0 11.84V4.15C0 3.52.52 3 1.15 3ZM9 11V5H7L5.5 7 4 5H2v6h2V8l1.5 1.92L7 8v3Zm2.99.5L14.5 8H13V5h-2v3H9.5Z"></path>
          </svg>
          <Typography variant="caption">Markdown được hỗ trợ</Typography>
        </Box>
        <Divider orientation="vertical" flexItem />
        <Box
          sx={{
            display: "flex",
            alignItems: "center",
            gap: 2,
            borderRadius: "5px",
            textDecoration: "none",
            cursor: "pointer",
            p: (theme) => theme.spacing(1, 2),
            "&:hover": {
              backgroundColor: (theme) => theme.palette.action.selected,
            },
          }}
          onClick={() => inputRef.current?.click()}
        >
          <svg
            aria-hidden="true"
            height="16"
            viewBox="0 0 16 16"
            version="1.1"
            width="16"
            data-view-component="true"
            fill="#6a737d"
          >
            <path d="M16 13.25A1.75 1.75 0 0 1 14.25 15H1.75A1.75 1.75 0 0 1 0 13.25V2.75C0 1.784.784 1 1.75 1h12.5c.966 0 1.75.784 1.75 1.75ZM1.75 2.5a.25.25 0 0 0-.25.25v10.5c0 .138.112.25.25.25h.94l.03-.03 6.077-6.078a1.75 1.75 0 0 1 2.412-.06L14.5 10.31V2.75a.25.25 0 0 0-.25-.25Zm12.5 11a.25.25 0 0 0 .25-.25v-.917l-4.298-3.889a.25.25 0 0 0-.344.009L4.81 13.5ZM7 6a2 2 0 1 1-3.999.001A2 2 0 0 1 7 6ZM5.5 6a.5.5 0 1 0-1 0 .5.5 0 0 0 1 0Z"></path>
          </svg>
          <Typography variant="caption">
            Dán, kéo hoặc ấn để thêm ảnh *.(jpg, jpeg, png, gif,...)
          </Typography>
          <input
            type="file"
            hidden
            ref={inputRef}
            onChange={handleChooseImage}
            accept="image/*"
          />
        </Box>
      </Box>
    </Box>
  );
};

CustomMDEditor.propTypes = {
  setValue: PropTypes.func,
};

export { CustomMDEditor };
