import React from "react";
import {
    Box,
    Button,
    Card,
    CardActions,
    CardContent,
    CardHeader,
    Fade,
    Modal,
    TextField,
    Typography,
} from "@mui/material";
import { makeStyles } from "@mui/styles";
import { request } from "api";
import { useForm } from "react-hook-form";
import { errorNoti, successNoti } from "utils/notification";
const useStyles = makeStyles((theme) => ({
    modal: {
        position: "absolute",
        top: "5%",
        left: "20%",
        width: 900,
        height: 900
    },
    card: {
        minWidth: 800,
    },
    action: {
        display: "flex",
        justifyContent: "center",
    },
    error: {
        color: "red",
        fontSize: 12,
    },
}));

export default function ModalCreateThesisDefensePlan({
    open,
    handleClose,
    handleToggle,
}) {
    const classes = useStyles();
    const {
        register,
        handleSubmit,
        formState: { errors },
    } = useForm({
        defaultValues: {
            id: "",
            name: "",
            description: "",
            semester: "",
            startDate: "",
            endDate: "",
        },
    });
    async function createThesisDefensePlan(body) {
        const start = new Date(body?.startDate);
        const end = new Date(body?.endDate);
        if (end.getTime() <= start.getTime()) {
            return errorNoti("Ngày kết thúc phải diễn ra sau ngày bắt đầu", true)
        }
        request(
            "post",
            "/thesis-defense-plan/save",
            (res) => {
                if (res.data === "Create successfully") {
                    successNoti('Bạn vừa tạo đợt bảo vệ mới thành công', true)
                    handleClose();
                    handleToggle();
                } else {
                    errorNoti('Thêm mới thất bại', true)
                }
            },
            {
                onError: (e) => {
                    errorNoti('Thêm mới thất bại', true)
                    // setShowSubmitSuccess(false);
                },
            },
            body
        ).then();
    }
    const onSubmit = (data) => {
        createThesisDefensePlan(data);
        // history.push({
        //     pathname: `/thesis`,
        //   });
    };

    return (
        <Modal
            className={classes.modal}
            open={open}
            onClose={handleClose}
            closeAfterTransition
        >
            <Fade in={open}>
                <form onSubmit={handleSubmit(onSubmit)}>
                    <Card className={classes.card}>
                        <CardHeader title="Thêm đợt bảo vệ mới" />
                        <CardContent>
                            <Box
                                sx={{
                                    display: "flex",
                                    flexDirection: "column",
                                    justifyContent: "center",
                                }}
                            >
                                <Typography>ID</Typography>
                                <TextField
                                    id="id"
                                    name="id"
                                    placeholder="Nhập ID"
                                    {...register("id", { required: true })}
                                />
                                {errors.id && (
                                    <Typography className={classes.error}>
                                        Trường này không được để trống
                                    </Typography>
                                )}
                                <Typography>Tên đợt bảo vệ</Typography>
                                <TextField
                                    id="name"
                                    name="name"
                                    placeholder="Nhập tên đợt bảo vệ"
                                    {...register("name", { required: true })}
                                />
                                {errors.name && (
                                    <Typography className={classes.error}>
                                        Trường này không được để trống
                                    </Typography>
                                )}
                                <Typography>Kì học</Typography>
                                <TextField
                                    id="name"
                                    name="name"
                                    placeholder="Nhập tên đợt bảo vệ"
                                    {...register("semester", { required: true })}
                                />
                                {errors.semester && (
                                    <Typography className={classes.error}>
                                        Trường này không được để trống
                                    </Typography>
                                )}
                                <Typography>Miêu tả</Typography>
                                <TextField
                                    id="name"
                                    name="description"
                                    type="text"
                                    placeholder="Nhập miêu tả"
                                    multiline
                                    rows={2}
                                    {...register("description", { required: false })}
                                />
                                <Typography>Ngày bắt đầu</Typography>
                                <TextField
                                    id="name"
                                    name="startDate"
                                    type="date"
                                    {...register("startDate", { required: true })}
                                />
                                {errors.startDate && (
                                    <Typography className={classes.error}>
                                        Trường này không được để trống
                                    </Typography>
                                )}
                                <Typography>Ngày kết thúc</Typography>
                                <TextField
                                    id="name"
                                    name="endDate"
                                    type="date"
                                    {...register("endDate", { required: true })}
                                />
                                {errors.endDate && (
                                    <Typography className={classes.error}>
                                        Trường này không được để trống
                                    </Typography>
                                )}
                            </Box>
                        </CardContent>
                        <CardActions className={classes.action}>
                            <Button type="submit" variant="contained" color="primary">
                                Thêm đợt bảo vệ đồ án
                            </Button>
                            {/* {alert ? <Alert severity="error">{alertContent}</Alert> : <></>} */}
                        </CardActions>
                    </Card>
                </form>
            </Fade>
        </Modal>
    );
}
