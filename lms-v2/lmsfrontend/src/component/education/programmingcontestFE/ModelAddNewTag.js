import React from "react";
import {Stack, TextField} from "@mui/material";
import {useTranslation} from "react-i18next";
import PrimaryButton from "component/button/PrimaryButton";
import CustomizedDialogs from "component/dialog/CustomizedDialogs";
import TertiaryButton from "component/button/TertiaryButton";
import {useForm} from "react-hook-form";
import {request} from "../../../api";
import {errorNoti} from "../../../utils/notification";
import {makeStyles} from "@material-ui/core/styles";

const useStyles = makeStyles((theme) => ({
  dialogContent: {
    minWidth: 500,
    padding: theme.spacing(2),
  },
}));

const ModelAddNewTag = ({isOpen, handleSuccess, handleClose}) => {
  const classes = useStyles();

  const {t} = useTranslation(
    ["education/programmingcontest/problem", "common", "validation"]
  );

  const {
    register,
    handleSubmit,
    errors,
  } = useForm({
    defaultValues: {
      name: null,
      description: null,
    }
  });

  const onSubmit = (data) => {
    handleClose()

    request(
      "post",
      "/tags/",
      handleSuccess,
      {
        onError: (e) => {
          errorNoti(t("common:error"))
        },
      },
      data
    )
  }

  const content = <form onSubmit={handleSubmit(onSubmit)}>
    <Stack spacing={2}>
      <TextField
        autoFocus
        fullWidth
        id="tag-name"
        label={t("tagName") + " *"}
        name="name"
        size="small"
        error={!!errors.name}
        helperText={errors.name?.message}
        inputRef={register({
          required: t("required", {ns: "validation"})
        })}
      />
      <TextField
        fullWidth
        id="tag-description"
        label={t("common:description")}
        name="description"
        size="small"
        error={!!errors.description}
        helperText={errors.description?.message}
        inputRef={register()}
      />
    </Stack>
    <Stack direction="row" spacing={2} justifyContent='center' mt={3}>
      <TertiaryButton
        variant="outlined"
        onClick={handleClose}
      >
        {t("common:cancel")}
      </TertiaryButton>
      <PrimaryButton
        type="submit"
      >
        {t("common:save")}
      </PrimaryButton>
    </Stack>
  </form>

  return (
    <CustomizedDialogs
      open={isOpen}
      handleClose={handleClose}
      title={t("common:add", {name: t('tag')})}
      contentTopDivider
      content={content}
      classNames={{content: classes.dialogContent}}
    />
  );
}

export default ModelAddNewTag;