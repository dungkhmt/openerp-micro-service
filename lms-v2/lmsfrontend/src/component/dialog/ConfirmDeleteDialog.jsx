import {makeStyles} from "@material-ui/core/styles";
import React from "react";
import CustomizedDialogs from "./CustomizedDialogs";
import TertiaryButton from "../button/TertiaryButton";
import {Button} from "@mui/material";
import {useTranslation} from "react-i18next";
import Typography from "@mui/material/Typography";

const useStyles = makeStyles((theme) => ({
  dialogContent: {minWidth: 450},
}));

export const ConfirmDeleteDialog = ({open, handleClose, handleDelete, entity, name}) => {
  const classes = useStyles();
  const {t} = useTranslation('common');

  return (
    <CustomizedDialogs
      open={open}
      handleClose={handleClose}
      title={t('common:delete')}
      contentTopDivider
      content={
        <Typography variant="subtitle2">
          {t('common:confirmDeleteHead')} {entity || ''} <Typography variant="subtitle2"
                                                                     component='span'
                                                                     color='error'>{name || ''}</Typography>{t('common:confirmDeleteTail')}
        </Typography>
      }
      actions={
        <>
          <TertiaryButton
            variant="outlined"
            color="inherit"
            onClick={handleClose}>
            {t('common:cancel')}
          </TertiaryButton>

          <Button variant='contained' color='error' sx={{textTransform: 'none'}} onClick={handleDelete}>
            {t('common:delete')}
          </Button>
        </>
      }
      classNames={{content: classes.dialogContent}}
    />
  );
};
