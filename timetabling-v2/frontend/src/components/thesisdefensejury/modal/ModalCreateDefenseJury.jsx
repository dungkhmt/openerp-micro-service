import React, { useState } from "react";
import { boxChildComponent, boxComponentStyle } from "../constant";
import {
    Modal,
    Fade,
    Box,
    Typography,
    FormControl,
    MenuItem,
    Select,
    OutlinedInput,
    InputLabel,
    Grid, TextField
} from "@mui/material";
import { request } from "api";
import { errorNoti, successNoti } from "utils/notification";
import { useForm } from "react-hook-form";
import PrimaryButton from "components/button/PrimaryButton";
import Checkbox from "@mui/material/Checkbox";
import ListItemText from "@mui/material/ListItemText";
import { useFetch } from "hooks/useFetch";
const ITEM_HEIGHT = 48;
const ITEM_PADDING_TOP = 8;
const MenuProps = {
    PaperProps: {
        style: {
            maxHeight: ITEM_HEIGHT * 4.5 + ITEM_PADDING_TOP,
            width: 250,
        },
    },
};
const styles = {
    modal: {
        position: "absolute",
        top: "5%",
        left: "20%",
        width: 900,
    },
    button: {
        display: "flex",
        justifyContent: "end",
    },
};

/**
 * Modal to create new defense jury
 * 
 */

function CreateDefenseJury({
    open,
    handleClose,
    handleToggle,
    thesisPlanName,
    juryTopic,
}) {
    const [defenseSession, setDefenseSession] = useState([]);

    const {
        register,
        handleSubmit,
        formState: { errors },
    } = useForm({
        defaultValues: {
            name: "",
            maxThesis: "",
            defenseDate: "",
            defenseRoomId: 0,
            juryTopicId: 0,
        },
    });
    const handleFormSubmit = (data) => {
        data.thesisPlanName = thesisPlanName;
        data.juryTopicId = juryTopic?.id;
        if (data?.defenseRoomId === 0) {
            return errorNoti("Bạn cần chọn phòng tổ chức hội đồng", true)
        }
        if (data?.juryTopicId === 0) {
            return errorNoti("Bạn cần chọn phân ban của hội đồng", true)
        }
        if (defenseSession.length === 0) {
            return errorNoti("Bạn cần chọn buổi tổ chức hội đồng", true)
        }
        data.defenseSessionId = defenseSession;
        console.log(data);
        request(
            "post",
            "/defense-jury/save",
            (res) => {
                if (res.data) {
                    const message = res.data;
                    if (message === "Success") {
                        handleClose();
                        handleToggle();
                        successNoti("Tạo hội đồng mới thành công", true);
                    }
                    else {
                        errorNoti(message, true);
                    }
                } else {
                    errorNoti("Create failed", true);
                }
            },
            {
                onError: (e) => {
                    console.log(e);
                },
            },
            data
        ).then();
    };
    const { data: roomList } = useFetch("/defense-room/get-all");
    const { data: sessionList } = useFetch("/defense-session/get-all");
    const handleChange = (event) => {
        const {
            target: { value },
        } = event;
        setDefenseSession(
            // On autofill we get a stringified value.
            typeof value === "string" ? value.split(",") : value
        );
    };

    return (
        <Modal
            open={open}
            onClose={handleClose}
            closeAfterTransition
            sx={styles.modal}
        >
            <Fade in={open}>
                <form onSubmit={handleSubmit(handleFormSubmit)}>
                    <Box sx={boxComponentStyle}>
                        <Typography variant="h4" mb={4} component={"h4"}>
                            Thêm mới Hội Đồng cho phân ban {juryTopic?.name}
                        </Typography>
                        <Box mb={3}>
                            {/* Input defense jury name */}
                            <TextField
                                {...register("name", { required: true })}
                                fullWidth={true}
                                id="input-with-icon-grid"
                                label="Tên hội đồng"
                                variant="outlined"
                                margin="normal"
                            />
                            {/* Input defense jury's maximum thesis number */}
                            <TextField
                                fullWidth={true}
                                label="Số lượng đồ án tối đa"
                                name="maxThesis"
                                {...register("maxThesis", { required: true })}
                                variant="outlined"
                                margin="normal"
                            />
                        </Box>
                        <Box sx={boxChildComponent} mb={3}>
                            <Grid container spacing={2}>
                                <Grid item={true} xs={6} spacing={2} p={2}>
                                    <span>Ngày bảo vệ</span>
                                    {/* Input defense jury's date */}
                                    <TextField
                                        id="input-with-icon-grid"
                                        {...register("defenseDate", { required: true })}
                                        fullWidth
                                        variant="outlined"
                                        type="date"
                                        placeholder="Enter"
                                        margin="normal"
                                    />
                                </Grid>
                                <Grid item={true} xs={6} spacing={2} p={2}>
                                    <span>Phòng tổ chức</span>
                                    {/* Select defense jury room */}
                                    <FormControl fullWidth margin="normal">
                                        <InputLabel id="defense-room-id">Chọn phòng</InputLabel>
                                        <Select
                                            MenuProps={MenuProps}
                                            name="defenseRoomId"
                                            {...register("defenseRoomId", { required: true })}
                                            label="Room"
                                            input={<OutlinedInput label="Tag" />}
                                        >
                                            {roomList?.map((item) => (
                                                <MenuItem key={item?.id} value={item?.id}>
                                                    {item?.name}
                                                </MenuItem>
                                            ))}
                                        </Select>
                                    </FormControl>
                                </Grid>
                                <Grid item={true} xs={6} spacing={2} p={2}>
                                    {/* Select defense jury's session */}
                                    <span>Thời gian tổ chức</span>
                                    <FormControl fullWidth margin="normal">
                                        <InputLabel id="defense-session-label">
                                            Chọn ca bảo vệ
                                        </InputLabel>
                                        <Select
                                            MenuProps={MenuProps}
                                            labelId="defense-session-label"
                                            label="defense-session"
                                            name="defenseSessionId"
                                            multiple
                                            value={defenseSession}
                                            onChange={handleChange}
                                            input={<OutlinedInput label="Tag" />}
                                            renderValue={(selected) => selected.map((item) => sessionList?.find((session) => session?.id === item)?.name).join(', ')}
                                        >
                                            {sessionList?.map((item) => (
                                                <MenuItem key={item?.id} value={item?.id}>
                                                    <Checkbox checked={defenseSession.indexOf(item?.id) !== -1} />
                                                    <ListItemText primary={item?.name} />
                                                </MenuItem>
                                            ))}
                                        </Select>
                                    </FormControl>
                                </Grid>
                            </Grid>
                        </Box>
                        <Box sx={styles.button}>
                            <PrimaryButton type="submit">Tạo hội đồng bảo vệ</PrimaryButton>
                        </Box>
                    </Box>
                </form>
            </Fade>
        </Modal>
    );
}

export default CreateDefenseJury;
