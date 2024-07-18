import React, { useState } from "react";
import { Box, Typography, Select, MenuItem, FormControl, InputLabel } from "@mui/material";
import { useParams, useHistory } from "react-router-dom";
import { boxComponentStyle } from "components/thesisdefensejury/constant";
import { useFetch } from "hooks/useFetch";
import KeywordChip from "components/common/KeywordChip";
import { useForm, Controller } from "react-hook-form";
import { StandardTable } from "erp-hust/lib/StandardTable";
import PrimaryButton from "components/button/PrimaryButton";
import { request } from "api";
import { successNoti } from "utils/notification";
const ITEM_HEIGHT = 48;
const ITEM_PADDING_TOP = 8;
const MenuProps = {
    PaperProps: {
        style: {
            maxHeight: ITEM_HEIGHT * 4.5 + ITEM_PADDING_TOP,
            width: 180,
        },
    },
};
// - Trang chủ tịch phân công gv phản biện từng đồ án
export default function PresidentAssignJuryDefense() {
    const { id, juryId } = useParams();
    const { data: defenseJury } = useFetch(`/defense-jury/${juryId}`);
    const history = useHistory();
    const thesisList = defenseJury?.thesisList;
    const defenseJuryTeacherRoles = defenseJury?.defenseJuryTeacherRoles;
    const { register, handleSubmit, formState: { errors } } = useForm();
    const columns = [
        { title: "Tên đồ án", field: "thesisName" },
        {
            title: "Sinh viên",
            field: "studentName",
        },
        {
            title: "Giáo viên",
            field: "supervisor",
            render: (rowData) => rowData?.supervisor,
        },
        {
            title: "Keyword",
            field: "academicKeywordList",
            render: (rowData) =>
                rowData?.academicKeywordList?.map(({ keyword, description }) => (
                    <KeywordChip key={keyword} keyword={description} />
                )),
        },
        {
            title: 'Giáo viên phản biện',
            render: (rowData) => (
                <FormControl fullWidth>
                    <InputLabel>Giáo viên phản biện</InputLabel>
                    <Select
                        MenuProps={MenuProps}
                        {...register(`${rowData?.id}`, { required: true })}
                    >
                        {defenseJuryTeacherRoles?.map((item) => <MenuItem key={item?.id} value={item?.teacher?.id}>
                            {item?.teacher?.teacherName}
                        </MenuItem>)}
                    </Select>

                </FormControl>
            )
        }
    ];
    const onAssignTeacherAndThesis = (data) => {
        let body = {
            defenseJuryId: juryId,
            reviewerThesisList: [],
        }
        for (const thesis in data) {
            body?.reviewerThesisList?.push({
                thesisId: thesis,
                scheduledReviewerId: data[thesis],
            })
        }
        request('post', '/defense-jury/assign-defense-teacher', (res) => {
            successNoti('Phân công giáo viên thành công', true);
            return history.goBack();
        }, (e) => { console.log(e) }, body).then();
    }
    return (
        <>
            <Box sx={{ ...boxComponentStyle, minHeight: "600px" }}>
                <Typography variant="h4" mb={1} component={"h4"}>
                    {defenseJury?.name}
                </Typography>
                <div className="defense-jury-info">
                    Ngày tổ chức: {defenseJury?.defenseDate?.split("T")[0]}
                </div>
                {defenseJury?.academicKeywordList?.map(({ keyword, description }) => (
                    <KeywordChip key={keyword} keyword={description} />
                ))}
                <form onSubmit={handleSubmit(onAssignTeacherAndThesis)}>
                    <StandardTable
                        title={"Danh sách đồ án"}
                        data={thesisList}
                        columns={columns}
                        options={{
                            selection: false,
                            pageSize: 5,
                            search: true,
                            sorting: true,
                        }}
                    />
                    <Box display={"flex"} flexDirection={"row-reverse"} marginTop={3}>
                        <PrimaryButton type="submit">
                            Phân chia bảo vệ
                        </PrimaryButton>
                    </Box>

                </form>
            </Box>
        </>

    )
}