import {Backdrop, Fade, makeStyles, Modal} from "@material-ui/core";
import React from "react";
import LoadingSpinner from "./load/LoadingSpinner";


const useStyles = makeStyles((theme) => ({
    modal: {
      display: "flex",
      alignItems: "center",
      justifyContent: "center",
    },
    card: {
      minWidth: 400,
    },
    action: {
      display: "flex",
      justifyContent: "center",
    },
    error: {
      textAlign: "center",
      color: "red",
      marginTop: theme.spacing(2),
    },
  }));
  
  
  export default function ModalLoading({ openLoading }) {
    const classes = useStyles();

    return (
      <Modal
        className={classes.modal}
        open={openLoading}
        closeAfterTransition
        BackdropComponent={Backdrop}
        BackdropProps={{
          timeout: 500,
        }}
      >
        <Fade in={openLoading}>
            <LoadingSpinner />
        </Fade>
      </Modal>
    );
  }