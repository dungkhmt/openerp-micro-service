import { Icon } from "@iconify/react";
import {
  Box,
  IconButton,
  List,
  ListItem,
  Typography,
  styled,
} from "@mui/material";
import PropTypes from "prop-types";
import { useDropzone } from "react-dropzone";
import { Link } from "react-router-dom";

const DropzoneWrapper = styled(Box)(({ theme }) => ({
  "&.dropzone, & .dropzone": {
    minHeight: 250,
    display: "flex",
    flexWrap: "wrap",
    cursor: "pointer",
    position: "relative",
    alignItems: "center",
    justifyContent: "center",
    borderRadius: theme.shape.borderRadius,
    border: `2px dashed ${
      theme.palette.mode === "light"
        ? "rgba(93, 89, 98, 0.22)"
        : "rgba(247, 244, 254, 0.14)"
    }`,
    [theme.breakpoints.down("xs")]: {
      textAlign: "center",
    },
    "&:focus": {
      outline: "none",
    },
    "& + .MuiList-root": {
      padding: 0,
      marginTop: theme.spacing(6.25),
      "& .MuiListItem-root": {
        display: "flex",
        justifyContent: "space-between",
        borderRadius: theme.shape.borderRadius,
        padding: theme.spacing(2.5, 2.4, 2.5, 6),
        border: `1px solid ${
          theme.palette.mode === "light"
            ? "rgba(93, 89, 98, 0.14)"
            : "rgba(247, 244, 254, 0.14)"
        }`,
        "& .file-details": {
          display: "flex",
          alignItems: "center",
        },
        "& .file-preview": {
          display: "flex",
          marginRight: theme.spacing(3.75),
          "& svg": {
            fontSize: "2rem",
          },
        },
        "& img": {
          width: 38,
          height: 38,
          padding: theme.spacing(0.75),
          borderRadius: theme.shape.borderRadius,
          border: `1px solid ${
            theme.palette.mode === "light"
              ? "rgba(93, 89, 98, 0.14)"
              : "rgba(247, 244, 254, 0.14)"
          }`,
        },
        "& .file-name": {
          fontWeight: 600,
        },
        "& + .MuiListItem-root": {
          marginTop: theme.spacing(3.5),
        },
      },
      "& + .buttons": {
        display: "flex",
        justifyContent: "flex-end",
        marginTop: theme.spacing(6.25),
        "& > :first-of-type": {
          marginRight: theme.spacing(3.5),
        },
      },
    },
    "& img.single-file-image": {
      objectFit: "cover",
      position: "absolute",
      width: "calc(100% - 1rem)",
      height: "calc(100% - 1rem)",
      borderRadius: theme.shape.borderRadius,
    },
  },
}));

const Img = styled("img")(({ theme }) => ({
  [theme.breakpoints.up("md")]: {
    marginRight: theme.spacing(15.75),
  },
  [theme.breakpoints.down("md")]: {
    marginBottom: theme.spacing(4),
  },
  [theme.breakpoints.down("sm")]: {
    width: 160,
  },
}));

const HeadingTypography = styled(Typography)(({ theme }) => ({
  marginBottom: theme.spacing(5),
  [theme.breakpoints.down("sm")]: {
    marginBottom: theme.spacing(4),
  },
}));

const FileUploader = ({ files, setFiles, isMultiple = false }) => {
  const { getRootProps, getInputProps } = useDropzone({
    multiple: isMultiple,
    onDrop: (acceptedFiles) => {
      setFiles(acceptedFiles.map((file) => Object.assign(file)));
    },
  });

  const renderFilePreview = (file) => {
    if (file.type.startsWith("image")) {
      return (
        <img
          width={38}
          height={38}
          alt={file.name}
          src={URL.createObjectURL(file)}
        />
      );
    } else {
      return <Icon icon="mdi:file-document-outline" />;
    }
  };

  const handleRemoveFile = (file) => {
    const uploadedFiles = files;
    const filtered = uploadedFiles.filter((i) => i.name !== file.name);
    setFiles([...filtered]);
  };

  const fileList = files?.map((file) => (
    <ListItem key={file.name}>
      <div className="file-details">
        <div className="file-preview">{renderFilePreview(file)}</div>
        <div>
          <Typography className="file-name">{file.name}</Typography>
          <Typography className="file-size" variant="body2">
            {Math.round(file.size / 100) / 10 > 1000
              ? `${(Math.round(file.size / 100) / 10000).toFixed(1)} mb`
              : `${(Math.round(file.size / 100) / 10).toFixed(1)} kb`}
          </Typography>
        </div>
      </div>
      <IconButton onClick={() => handleRemoveFile(file)}>
        <Icon icon="mdi:close" fontSize={20} />
      </IconButton>
    </ListItem>
  ));

  return (
    <DropzoneWrapper>
      <div {...getRootProps({ className: "dropzone" })}>
        <input {...getInputProps()} />
        <Box
          sx={{
            display: "flex",
            flexDirection: ["column", "column", "row"],
            alignItems: "center",
          }}
        >
          <Img alt="Upload img" src="/static/images/upload.png" />
          <Box
            sx={{
              display: "flex",
              flexDirection: "column",
              textAlign: ["center", "center", "inherit"],
            }}
          >
            <HeadingTypography variant="h5">
              Kéo và thả tệp vào đây hoặc nhấp để tải lên.
            </HeadingTypography>
            <Typography
              color="textSecondary"
              sx={{ "& a": { color: "primary.main", textDecoration: "none" } }}
            >
              Kéo và thả tệp vào đây hoặc nhấp để{" "}
              <Link href="/" onClick={(e) => e.preventDefault()}>
                duyệt
              </Link>{" "}
              qua máy tính của bạn
            </Typography>
          </Box>
        </Box>
      </div>
      {files?.length ? <List>{fileList}</List> : null}
    </DropzoneWrapper>
  );
};

FileUploader.propTypes = {
  files: PropTypes.array.isRequired,
  setFiles: PropTypes.func.isRequired,
  isMultiple: PropTypes.bool,
};

export { FileUploader };
