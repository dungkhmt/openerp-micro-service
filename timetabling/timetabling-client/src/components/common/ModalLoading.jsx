import React from "react";
import { Backdrop } from "@mui/material";
import { makeStyles } from "@mui/styles";
import { FacebookCircularProgress } from "./progressBar/CustomizedCircularProgress";

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


const ModalLoading = (loading) => {
    return (
        <Backdrop sx={{ color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1 }}
            open={loading}>
            <FacebookCircularProgress />
        </Backdrop>
    )
}

export default ModalLoading