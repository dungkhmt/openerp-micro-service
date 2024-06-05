import React, { useEffect, useMemo, useState } from "react";
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

function CreateDefenseJury({
    open,
    handleClose,
    handleToggle,
    thesisPlanName,
}) {
    const [keyword, setKeyword] = useState([]);
    const {
        register,
        handleSubmit,
        formState: { errors },
    } = useForm({
        defaultValues: {
            name: "",
            maxThesis: "",
            defenseDate: "",
            defenseRoomId: 1,
            defenseSessionId: 1,
            academicKeywordList: [],
        },
    });
    const handleFormSubmit = (data) => {
        data.academicKeywordList = [...data.academicKeywordList, ...keyword];
        data.thesisPlanName = thesisPlanName;
        request(
            "post",
            "/defense-jury/save",
            (res) => {
                if (res.data) {
                    const message = res.data;
                    if (message == "Success") {
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
    const { data: keywordList } = useFetch("/academic_keywords/get-all");
    const { data: sessionList } = useFetch("/defense-session/get-all");
    const handleChange = (event) => {
        const {
            target: { value },
        } = event;
        setKeyword(
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
                            Thêm mới Hội Đồng
                        </Typography>
                        <Box mb={3}>
                            <TextField
                                {...register("name", { required: true })}
                                fullWidth={true}
                                id="input-with-icon-grid"
                                label="Tên hội đồng"
                                variant="outlined"
                                margin="normal"
                            />
                            <TextField
                                fullWidth={true}
                                label="Số lượng đồ án tối đa"
                                name="maxThesis"
                                {...register("maxThesis", { required: true })}
                                variant="outlined"
                                margin="normal"
                            // onBlur={(e) => handleInputValidationThesis(e)}
                            // {...register("name", { required: "Thiếu tên luận văn!" })}
                            // error={!!errors?.name}
                            // helperText={errors?.name?.message}
                            />
                        </Box>
                        <Box sx={boxChildComponent} mb={3}>
                            <Grid container spacing={2}>
                                <Grid item={true} xs={6} spacing={2} p={2}>
                                    <span>Ngày bảo vệ</span>
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
                                    <FormControl fullWidth margin="normal">
                                        <InputLabel id="defense-room-id">Chọn phòng</InputLabel>
                                        <Select
                                            MenuProps={MenuProps}
                                            name="defenseRoomId"
                                            {...register("defenseRoomId")}
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
                                    <span>Thời gian tổ chức</span>
                                    <FormControl fullWidth margin="normal">
                                        <InputLabel id="defense-session-label">
                                            Chọn ca bảo vệ
                                        </InputLabel>
                                        <Select
                                            MenuProps={MenuProps}
                                            label="defense-session"
                                            name="defenseSessionId"
                                            input={<OutlinedInput label="Tag" />}
                                            {...register("defenseSessionId", { required: true })}
                                        >
                                            {sessionList?.map((item) => (
                                                <MenuItem key={item?.id} value={item?.id}>
                                                    {item?.name}
                                                </MenuItem>
                                            ))}
                                        </Select>
                                    </FormControl>
                                </Grid>
                                <Grid item={true} xs={6} spacing={2} p={2}>
                                    <span>Keyword hội đồng</span>
                                    <FormControl fullWidth margin="normal">
                                        <InputLabel id="demo-multiple-name-label">
                                            Keyword
                                        </InputLabel>
                                        <Select
                                            multiple
                                            MenuProps={MenuProps}
                                            value={keyword}
                                            name="academicKeywordList"
                                            label="Keyword"
                                            renderValue={(selected) => selected.join(", ")}
                                            input={<OutlinedInput label="Tag" />}
                                            onChange={handleChange}
                                        >
                                            {keywordList?.map((item) => (
                                                <MenuItem key={item?.id} value={item?.keyword}>
                                                    <Checkbox
                                                        checked={keyword.indexOf(item?.keyword) > -1}
                                                    />
                                                    <ListItemText primary={item?.keyword} />
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
