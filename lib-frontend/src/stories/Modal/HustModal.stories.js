import React from "react";
import {Body} from "./data/basic";
import {HustModal} from "../../components";

export default {
    title: 'Components/HustModal',
    component: HustModal,
    tags: ['autodocs'],
};

const onOk = () => {
    alert("OK");
}
const handleClose = () => {
    alert("You clicked CANCEL")
};

export const Sample = {
    args: {
        open: false,
        onOk: onOk,
        textOk: "Save",
        onClose: handleClose,
        isLoading: false,
        title: "Modal",
        maxWidthPaper: 400,
        children: <Body/>
    }
}

