import React, { useEffect, useState } from 'react'
import { useParams, useHistory } from "react-router-dom";
import {
    Box,
    TextField,
    Typography,
    Grid,
    FormControl,
    MenuItem,
    Select,
    OutlinedInput,
    InputLabel,
    ListItemText,
    Checkbox
} from "@mui/material";
import { useFetch } from 'hooks/useFetch';
import { request } from 'api';
import { boxComponentStyle, boxChildComponent } from 'components/thesisdefensejury/constant';
import { useForm } from 'react-hook-form';
import PrimaryButton from 'components/button/PrimaryButton';
import ModalLoading from 'components/common/ModalLoading';
import { successNoti, errorNoti } from 'utils/notification';
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
export const EditDefenseJury = () => {
    const [defenseJury, setDefenseJury] = useState({});
    const { juryId } = useParams();
    const history = useHistory();
    const [loading, setLoading] = useState(false);
    const { data: roomList } = useFetch("/defense-room/get-all");
    const { data: keywordList } = useFetch("/academic_keywords/get-all");
    const { data: sessionList } = useFetch("/defense-session/get-all");
    // const prevKeywordList = defenseJury?.academicKeywordList?.map((item) => item?.keyword);
    const [keyword, setKeyword] = useState([]);
    const [defenseSessionId, setDefenseSessionId] = useState(0);
    const [defenseRoomId, setDefenseRoomId] = useState(0);
    const handleChange = (event) => {
        const {
            target: { value },
        } = event;
        setKeyword(
            // On autofill we get a stringified value.
            typeof value === "string" ? value.split(",") : value
        );
    };
    const {
        register,
        handleSubmit,
        formState: { errors },
        reset
    } = useForm({
        defaultValues: {
            id: juryId,
            name: defenseJury?.name,
            maxThesis: defenseJury?.maxThesis,
            defenseDate: defenseJury?.defenseDate,
            defenseRoomId: 1,
            defenseSessionId: 1,
            academicKeywordList: [],
        },
    });

    const handleFormSubmit = data => {
        data.academicKeywordList = [...keyword];
        data.defenseRoomId = defenseRoomId;
        data.defenseSessionId = defenseSessionId;
        data.maxThesis = parseInt(data.maxThesis);
        request(
            "POST",
            "/defense-jury/update",
            (res) => {
                if (res.data) {
                    const message = res.data;
                    if (message.toUpperCase() === "SUCCESS") {
                        successNoti("cập nhật hội đồng thành công", true)
                        return history.goBack();
                    }
                    else {
                        return errorNoti(message, true);
                    }
                }
            },
            {
                onError: (e) => {
                    errorNoti(e.response.message, true)
                },
            },
            data
        ).then();
    }
    useEffect(() => {
        setLoading(false);
        request('GET', `/defense-jury/${juryId}`, (res) => {
            setDefenseJury(res.data)
            reset({
                id: juryId,
                name: res.data?.name,
                maxThesis: res.data?.maxThesis,
                defenseDate: res.data?.defenseDate?.split("T")[0],
            })
            setDefenseRoomId(res.data.defenseRoom?.id)
            setDefenseSessionId(res.data.defenseSession?.id)
            const prevKeyword = res.data?.academicKeywordList?.map((item) => item?.keyword);
            setKeyword(prevKeyword)
            setLoading(false);
        })
    }, [])
    return (
        <form onSubmit={handleSubmit(handleFormSubmit)}>
            {loading ? <ModalLoading /> : <Box sx={boxComponentStyle}>
                <Typography variant="h4" mb={4} component={"h4"}>
                    Cập nhật Hội Đồng
                </Typography>
                <Box mb={3}>
                    <div>Tên hội đồng</div>
                    <TextField
                        {...register("name", { required: true })}
                        fullWidth={true}
                        id="input-with-icon-grid"
                        variant="outlined"
                        margin="normal"
                    />
                    <div>Số lượng đồ án tối đa</div>
                    <TextField
                        fullWidth={true}
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
                                    value={defenseRoomId}
                                    onChange={(e) => setDefenseRoomId(e.target.value)}
                                >
                                    {roomList?.map((item) => (
                                        <MenuItem key={item?.id} value={item?.id} selected={defenseJury?.defenseRoom?.id === item?.id}>
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
                                    value={defenseSessionId}
                                    onChange={(e) => setDefenseSessionId(e.target.value)}
                                >
                                    {sessionList?.map((item) => (
                                        <MenuItem key={item?.id} value={item?.id} selected={defenseJury?.defenseSession?.id === item?.id}>
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
                                    input={<OutlinedInput label="Tag" />}
                                    onChange={handleChange}
                                    renderValue={(selected) => selected?.join(", ")}
                                >
                                    {keywordList?.map((item) => (
                                        <MenuItem key={item?.id} value={item?.keyword}>
                                            <Checkbox
                                                checked={keyword?.indexOf(item?.keyword) > -1}
                                            />
                                            <ListItemText primary={item?.keyword} />
                                        </MenuItem>
                                    ))}
                                </Select>
                            </FormControl>
                        </Grid>
                    </Grid>
                </Box>
                <Box sx={{
                    display: "flex",
                    justifyContent: "end",
                }}>
                    <PrimaryButton type="submit">Cập nhật hội đồng bảo vệ</PrimaryButton>
                </Box>
            </Box>}

        </form>
    )
}
