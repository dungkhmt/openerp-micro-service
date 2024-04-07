import React from "react";
import {DropzoneArea} from "material-ui-dropzone";
import {useTranslation} from "react-i18next";
import Box from "@mui/material/Box";
import {makeStyles} from "@material-ui/core";
import {Typography} from "@mui/material";

const useStyles = makeStyles((theme) => ({
  dropzone: {
    minHeight: "60px",
    borderRadius: "16px",
    marginBottom: theme.spacing(1),
  },
}));

const HustDropzoneArea = (props) => {
  const {
    title,
    onChangeAttachment,
    classRoot,
    ...remainProps
  } = props;

  const {t} = useTranslation("component/dropzone");

  const classes = useStyles();

  return (
    <Box className={`${classRoot}`}>
      <Typography
        variant="h6"
        display="block"
        style={{margin: "12px 0 12px 2px", width: "100%"}}
      >
        {title ? title : t("title")}
      </Typography>
      <DropzoneArea
        {...remainProps}
        dropzoneClass={classes.dropzone}
        filesLimit={10}
        showPreviews={true}
        showPreviewsInDropzone={false}
        useChipsForPreview
        dropzoneText={t("dropzoneText")}
        previewText={t("previewText")}
        previewChipProps={{
          variant: "outlined",
          color: "primary",
          size: "medium",
        }}
        getFileAddedMessage={(fileName) =>
          t("getFileAddedMessage", {fileName: fileName})
        }
        getFileRemovedMessage={(fileName) =>
          t("getFileRemovedMessage", {fileName: fileName})
        }
        getFileLimitExceedMessage={(filesLimit) =>
          t("getFileLimitExceedMessage", {filesLimit: filesLimit})
        }
        alertSnackbarProps={{
          anchorOrigin: {vertical: "bottom", horizontal: "right"},
          autoHideDuration: 1800,
        }}
        onChange={onChangeAttachment}
      />
    </Box>

  );
};

export default React.memo(HustDropzoneArea);
