import React, { useEffect } from 'react'
import { useParams, useHistory } from "react-router-dom";
import {
    Box,
    TextField,
    Typography,
    Grid
} from "@mui/material";
import { useFetch } from 'hooks/useFetch';
import { request } from 'api';
import { boxComponentStyle, boxChildComponent } from 'components/thesisdefensejury/constant';
import { useForm } from 'react-hook-form';
import PrimaryButton from 'components/button/PrimaryButton';
import ModalLoading from 'components/common/ModalLoading';
import { successNoti, errorNoti } from 'utils/notification';
// Màn hình chỉnh sửa đợt BVĐA
export const EditThesisDefensePlan = () => {
    const { id } = useParams();
    const history = useHistory();
    const { loading, data: thesisDefensePlan } = useFetch(`/thesis-defense-plan/${id}`);
    const {
        register,
        handleSubmit,
        formState: { errors },
        reset
    } = useForm({
        defaultValues: {
            id: thesisDefensePlan?.id,
            name: thesisDefensePlan?.name,
            description: thesisDefensePlan?.description,
            semester: thesisDefensePlan?.semester,
            startDate: thesisDefensePlan?.startDate?.split("T")[0],
            endDate: thesisDefensePlan?.endDate?.split("T")[0],
        },
    });

    const handleFormSubmit = data => {
        const start = new Date(data?.startDate);
        const end = new Date(data?.endDate);
        if (end.getTime() <= start.getTime()) {
            return errorNoti("Ngày kết thúc phải diễn ra sau ngày bắt đầu", true)
        }

        request(
            "POST",
            "/thesis-defense-plan/edit?id=" + id,
            (res) => {
                if (res.data) {
                    // setShowSubmitSuccess(true);
                    // setOpenAlert(true);
                    successNoti(res.data, true)
                    history.goBack();
                }
            },
            {
                onError: (e) => {
                    errorNoti('Thêm mới thất bại', true)
                    // setShowSubmitSuccess(false);
                },
            },
            {
                id: data?.id,
                name: data?.name,
                description: data?.description,
                semester: data?.semester,
                startDate: data?.startDate,
                endDate: data?.endDate,
            }
        ).then();
    }
    useEffect(() => {
        reset({
            id: thesisDefensePlan?.id,
            name: thesisDefensePlan?.name,
            description: thesisDefensePlan?.description,
            semester: thesisDefensePlan?.semester,
            startDate: thesisDefensePlan?.startDate?.split("T")[0],
            endDate: thesisDefensePlan?.endDate?.split("T")[0],
        })
    }, [thesisDefensePlan])
    return (
        <form onSubmit={handleSubmit(handleFormSubmit)}>
            {loading && <ModalLoading />}
            <Box sx={boxComponentStyle}>
                <Typography variant="h4" mb={4} component={"h4"}>
                    Chỉnh sửa đợt bảo vệ đồ án
                </Typography>
                <Box mb={3}>
                    <span>Mã đợt bảo vệ</span>
                    <TextField
                        {...register("id", { required: true })}
                        fullWidth={true}
                        id="plan_id"
                        variant="outlined"
                        margin="normal"
                    />
                    <span>Tên đợt bảo vệ</span>
                    <TextField
                        fullWidth={true}
                        name="name"
                        {...register("name", { required: true })}
                        variant="outlined"
                        margin="normal"
                    />
                    <span>Mô tả</span>
                    <TextField
                        id="description"
                        {...register("description", { required: true })}
                        fullWidth
                        variant="outlined"
                        type="text"
                        margin="normal"
                    />
                </Box>
                <Box sx={boxChildComponent} mb={3}>
                    <Grid container spacing={2}>
                        <Grid item={true} xs={6} spacing={2}>
                            <span>Ngày bắt đầu</span>
                            <TextField
                                id="input-with-icon-grid"
                                {...register("startDate", { required: true })}
                                fullWidth
                                variant="outlined"
                                type="date"
                                placeholder="Enter"
                                margin="normal"
                            />
                        </Grid>
                        <Grid item={true} xs={6} spacing={2} >
                            <span>Ngày kết thúc</span>
                            <TextField
                                id="input-with-icon-grid"
                                {...register("endDate", { required: true })}
                                fullWidth
                                variant="outlined"
                                type="date"
                                placeholder="Enter"
                                margin="normal"
                            />
                        </Grid>
                        <Grid item={true} xs={6} spacing={2}>
                            <span>Học kì</span>
                            <TextField
                                id="semester"
                                {...register("semester", { required: true })}
                                fullWidth
                                variant="outlined"
                                type="text"
                                margin="normal"
                            />
                        </Grid>
                    </Grid>
                </Box>
                <Box>
                    <PrimaryButton type="submit">Cập nhật đợt bảo vệ</PrimaryButton>
                </Box>
            </Box>
        </form>
    )
}
