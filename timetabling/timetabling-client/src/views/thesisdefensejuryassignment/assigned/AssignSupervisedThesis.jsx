import React from 'react'
import { useFetch } from 'hooks/useFetch'
import { useParams, useHistory } from "react-router-dom";
import KeywordChip from 'components/common/KeywordChip';
import { useForm } from 'react-hook-form';
import { successNoti, errorNoti } from 'utils/notification';
import {
    FormControl,
    InputLabel,
    MenuItem,
    Select,
    OutlinedInput,
    Typography, Box,
} from "@mui/material";
import PrimaryButton from 'components/button/PrimaryButton';
import { request } from 'api';
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

const AssignSupervisedThesis = () => {
    const { register, handleSubmit, formState: { errors } } = useForm({
        defaultValues: {
            juryTopicId: 0,
        }
    });
    const profile = {
        backgroundColor: "white",
        borderRadius: "6px",
        width: "100%",
        display: "inline-block",
    }
    const profile_name = {
        lineHeight: '18px',
        padding: '20px'
    }
    const profile_name_name = {
        textTransform: 'uppercase',
        fontSize: '20px',
        fontWeight: 'bold'
    }
    const profile_name_title = {
        fontSize: '17px',
        color: '#777'
    }

    const { id } = useParams();
    const history = useHistory();
    const { data: thesis } = useFetch(`/thesis/${id}`);
    const { data: juryType } = useFetch("/jury-topic/get-all");
    const onSubmit = (data) => {
        request("POST", '/thesis/assign',
            (res) => {
                if (res.data) {
                    const message = res.data;
                    successNoti(message, true);
                    return history.goBack();
                } else {
                    errorNoti("Create failed", true);
                }
            },
            {
                onError: (e) => {
                    errorNoti(e.response.message, true)
                },
            },
            {
                thesisId: id,
                juryTopicId: data?.juryTopicId
            }
        ).then()
    }
    return (
        <Box sx={{ backgroundColor: 'white', boxShadow: '0 2px 92px 0 rgba(0,0,0,0.13)', borderRadius: '10px', maxHeight: '92.5vh' }}>
            <Typography variant="h6" component={"h6"} paddingLeft={'20px'} paddingRight={'20px'}>
                Thông tin đồ án
            </Typography>
            <Box sx={profile}>
                <Box sx={profile_name}>
                    <Typography sx={profile_name_name}>{thesis?.thesisName}</Typography>
                    <Typography sx={profile_name_title}>{thesis?.thesisAbstract}</Typography>
                    <Box>
                        {thesis?.academicKeywordList?.map((kw) => <KeywordChip key={kw?.keyword} keyword={kw?.description} />)}
                    </Box>
                    <Box sx={{ marginTop: 3, marginBottom: 3 }}>
                        Sinh viên:
                        <Typography sx={{ fontWeight: '500' }}>{thesis?.studentName}</Typography>
                    </Box>
                    <Box sx={{ marginTop: 3, marginBottom: 3 }}>
                        Mã số sinh viên:
                        <Typography sx={{ fontWeight: '500' }}>{thesis?.studentId}</Typography>
                    </Box>

                    <form onSubmit={handleSubmit(onSubmit)} style={{ width: '50%' }}>
                        <div>Phân ban</div>
                        <FormControl fullWidth margin="normal">
                            <InputLabel id="defense-room-id">Chọn phân ban</InputLabel>
                            <Select
                                MenuProps={MenuProps}
                                name="juryTopicId"
                                {...register("juryTopicId")}
                                label="topic"
                                input={<OutlinedInput label="Tag" />}
                            >
                                {juryType?.map((item) => (
                                    <MenuItem key={item?.id} value={item?.id} >
                                        {item?.name}
                                    </MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                        <Box sx={{ marginTop: 2, marginBottom: 2 }}>
                            <PrimaryButton type="submit">Lưu phân ban cho đồ án</PrimaryButton>
                        </Box>
                    </form>
                </Box>
            </Box>
        </Box>
    )
}

export default AssignSupervisedThesis