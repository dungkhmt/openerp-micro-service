import React, { useEffect, useState } from 'react'
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
import { boxComponentStyle, boxChildComponent } from '../constant';
import PrimaryButton from "components/button/PrimaryButton";
import Checkbox from "@mui/material/Checkbox";
import { request } from "api";
import { useFetch } from "hooks/useFetch";
import ListItemText from "@mui/material/ListItemText";
import { errorNoti, successNoti } from 'utils/notification';
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
/**
 * Modal tạo phân ban hội đồng mới
 * 
 */

const ModalJuryTopic = ({ open, handleClose, handleToggle, type = "CREATE", topicId = null, thesisDefensePlanId }) => {
    const { data: keywordList } = useFetch('/academic_keywords/get-all')
    const { data: teacherList } = useFetch('/defense-jury/teachers')
    const [keyword, setKeyword] = React.useState([]);
    const [teacher, setTeacher] = useState("")
    const [name, setName] = React.useState("");
    const handleFormSubmit = (e) => {
        e.preventDefault()
        request("POST",
            type === "CREATE" ?
                '/jury-topic/save' : `/jury-topic/update/${topicId}`, (res) => {
                    if (res.data) {
                        successNoti(res.data, true)
                        handleClose();
                        handleToggle();
                    }
                },
            {
                onError: (e) => {
                    errorNoti('Thêm mới thất bại', true)
                    // setShowSubmitSuccess(false);
                },
            },
            {
                name,
                academicKeywordList: keyword,
                teacherId: teacher,
                thesisDefensePlanId,
            }).then();
    }
    const handleChangeKeyword = (event) => {
        const {
            target: { value },
        } = event;
        setKeyword(
            // On autofill we get a stringified value.
            typeof value === "string" ? value.split(",") : value
        );
    };
    useEffect(() => {
        if (topicId !== 0 && type === "UPDATE") {
            request("GET", `/jury-topic/${topicId}`, (res) => {
                setName(res?.data?.name)
                setKeyword(res?.data?.academicKeywordList?.map((item) => item?.keyword))
            })
        }
    }, [type, topicId])
    return (
        <Modal open={open}
            onClose={handleClose}
            closeAfterTransition
        >
            <Fade in={open}>
                <form onSubmit={handleFormSubmit}>
                    <Box sx={{
                        ...boxComponentStyle,
                        position: "absolute",
                        top: '10%',
                        left: '20%',
                        width: 900
                    }}>
                        <Typography variant="h4" mb={4} component={"h4"}>
                            {type === "CREATE" ? "Thêm Phân ban mới" : "Cập nhật thông tin phân ban"}
                        </Typography>
                        <Box sx={boxChildComponent} mb={3}>
                            <Grid container spacing={2}>
                                <Grid item={true} xs={6} spacing={2} p={2}>
                                    <span>Tên phân ban</span>
                                    <TextField
                                        id="input-with-icon-grid"
                                        value={name}
                                        onChange={(e) => setName(e.target.value)}
                                        fullWidth
                                        variant="outlined"
                                        type="text"
                                        placeholder="Nhập tên phân ban"
                                        margin="normal"
                                    />
                                </Grid>
                                <Grid item={true} xs={6} spacing={2} p={2}>
                                    <span>Keyword</span>
                                    <FormControl fullWidth sx={{ marginTop: 2 }}>
                                        <InputLabel id="demo-multiple-checkbox-label">Hướng đề tài</InputLabel>
                                        <Select
                                            labelId="demo-multiple-checkbox-label"
                                            id="demo-multiple-checkbox"
                                            multiple
                                            value={keyword}
                                            onChange={handleChangeKeyword}
                                            input={<OutlinedInput label="Tag" />}
                                            renderValue={(selected) => selected.join(', ')}
                                            MenuProps={MenuProps}
                                        >
                                            {keywordList?.map((ele) => (
                                                <MenuItem key={ele?.keyword} value={ele?.keyword}>
                                                    <Checkbox checked={keyword.indexOf(ele?.keyword) !== -1} />
                                                    <ListItemText primary={ele?.keyword} />
                                                </MenuItem>
                                            ))}
                                        </Select>
                                    </FormControl>
                                </Grid>
                                <Grid item={true} xs={6} spacing={2} p={2}>
                                    <span>Điều phối viên</span>
                                    <FormControl fullWidth sx={{ marginTop: 2 }}>
                                        <InputLabel id="teacher">Chọn điều phối viên</InputLabel>
                                        <Select
                                            labelId="teacher"
                                            id="teacher-select"
                                            value={teacher}
                                            onChange={(e) => { setTeacher(e.target.value) }}
                                            MenuProps={MenuProps}
                                            label="teacher"
                                            input={<OutlinedInput label="Tag" />}

                                        >
                                            {teacherList?.map((ele) => (
                                                <MenuItem key={ele?.id} value={ele?.id}>
                                                    {ele?.teacherName}
                                                </MenuItem>
                                            ))}
                                        </Select>
                                    </FormControl>
                                </Grid>
                            </Grid>
                        </Box>
                        <Box
                            display={"flex"}
                            flexDirection={"row-reverse"}
                            marginTop={3}
                        >
                            <PrimaryButton type="submit">
                                {type === "CREATE" ? "Tạo phân ban" : "Cập nhật phân ban"}</PrimaryButton>
                        </Box>
                    </Box>
                </form>
            </Fade>
        </Modal>
    )
}

export default ModalJuryTopic