import React, { useState } from 'react'
import KeywordChip from 'components/common/KeywordChip';
import { useKeycloak } from '@react-keycloak/web';
import { useFetch } from 'hooks/useFetch';
import { StandardTable } from 'erp-hust/lib/StandardTable';
import ModalLoading from 'components/common/ModalLoading';
import { Select, Card, MenuItem, FormControl, InputLabel } from '@mui/material';
import PrimaryButton from 'components/button/PrimaryButton';
import { useHistory } from "react-router-dom";
const SuperviseThesisList = () => {
    const [currentThesisDefensePlan, setCurrentThesisDefensePlan] = useState("");
    const { keycloak } = useKeycloak();
    const history = useHistory();
    const { loading, data: supervisedThesisList } = useFetch(
        `/thesis/get-all-by-supervisor?teacher=${keycloak?.tokenParsed?.email}`
    );
    const columns = [
        { title: "Tên đồ án", field: "thesisName" },
        { title: "Tên sinh viên", field: "studentName" },
        { title: "Mã số sinh viên", field: "studentId" },
        {
            title: "Keyword",
            field: "academicKeywordList",
            render: (rowData) => rowData?.academicKeywordList?.map((kw) => <KeywordChip key={kw?.keyword} keyword={kw?.description} />),
        },
        {
            title: "Phân ban",
            field: "juryTopic",
            sorting: false,
            render: (rowData) =>
                rowData?.juryTopic
                    ? <KeywordChip keyword={rowData?.juryTopic?.name} />
                    : <PrimaryButton onClick={() => { history.push(`/thesis/teacher/superviser/assign/${rowData?.id}`) }}>
                        Phân loại đồ án
                    </PrimaryButton>
        }

    ];
    const data = supervisedThesisList?.find((item) => item?.thesisDefensePlan === currentThesisDefensePlan);
    const thesisList = data && data?.thesisList;
    const handleChange = (event) => {
        setCurrentThesisDefensePlan(event.target.value);
    };

    return (
        <Card sx={{ paddingBottom: 2, paddingTop: 2, paddingLeft: 2, paddingRight: 1 }}>
            {loading && <ModalLoading loading={loading} />}
            <FormControl sx={{ width: '30%' }}>
                <InputLabel id="thesisDefensePlan">Chọn đợt bảo vệ đồ án</InputLabel>
                <Select
                    labelId="thesisDefensePlan"
                    id="thesisDefensePlanSelect"
                    value={currentThesisDefensePlan}
                    label="thesisDefensePlan"
                    onChange={handleChange}
                >
                    {supervisedThesisList?.map(({ thesisDefensePlan }) =>
                        <MenuItem key={thesisDefensePlan} value={thesisDefensePlan}>{thesisDefensePlan}</MenuItem>
                    )}
                </Select>
            </FormControl>
            <StandardTable
                title={"Danh sách đợt bảo vệ"}
                data={thesisList}
                columns={columns}
                options={{
                    selection: false,
                    pageSize: 5,
                    search: true,
                    sorting: true,
                }}
            />
        </Card>

    )
}

export default SuperviseThesisList