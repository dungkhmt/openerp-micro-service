import React from "react";
import { StandardTable } from "erp-hust/lib/StandardTable";
import KeywordChip from "components/common/KeywordChip";
import { Checkbox } from "@mui/material";
/**
 * Component để chọn đồ án trong hội đồng
 * 
 */
export default function ElementAddThesis({ availableThesisList, assignedThesis, handleSelectThesis }) {
    const columns = [
        {
            title: "",
            render: (rowData) => (
                <Checkbox
                    checked={
                        assignedThesis?.find((item) => item === rowData?.id) !== undefined
                    }
                    onChange={(e) => { handleSelectThesis(rowData) }}
                    color="default"
                />
            ),
        },
        { title: "Tên đồ án", field: "thesisName" },
        {
            title: "Phân ban",
            field: "juryTopic",
            render: (rowData) => rowData?.secondaryJuryTopicName ? `(1)${rowData?.juryTopicName} (2)${rowData?.secondaryJuryTopicName}` : `${rowData?.juryTopicName}`,
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
            title: "Sinh viên",
            field: "studentName",
        },
        {
            title: "Giáo viên",
            field: "supervisor",
            render: (rowData) => rowData?.supervisor,
        },
    ];
    return (
        <StandardTable
            title={"Danh sách đồ án"}
            data={availableThesisList}
            columns={columns}
            options={{
                selection: false,
                pageSize: 5,
                search: true,
                sorting: true,
            }}
        />
    );
}
