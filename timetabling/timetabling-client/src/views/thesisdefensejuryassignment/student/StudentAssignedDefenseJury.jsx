import { StandardTable } from "erp-hust/lib/StandardTable";
import { useKeycloak } from "@react-keycloak/web";
import { useFetch } from "hooks/useFetch";
import PrimaryButton from "components/button/PrimaryButton";
import { useHistory } from "react-router-dom";
import ModalLoading from "components/common/ModalLoading";
import { Box } from "@mui/material";
export default function StudentAssignedDefenseJury() {
    const { keycloak } = useKeycloak();
    const history = useHistory();
    const studentEmail = keycloak.tokenParsed?.email;
    const { loading, data: thesis } = useFetch(`/thesis/get-all?student-email=${studentEmail}`);
    const columns = [
        { title: "Tên đợt bảo vệ", field: "thesisName" },
        { title: 'Mô tả đồ án', field: 'thesisAbstract' },
        { title: "Giáo viên hướng dẫn", field: "supervisor" },
        {
            title: "Hội đồng được phân công",
            field: "defenseJuryName",
            render: (rowData) => rowData?.defenseJuryName ? rowData?.defenseJuryName : "Đang chờ phân công",
        },
        {
            title: "",
            sorting: false,
            render: (rowData) => rowData?.defenseJuryName && (
                <PrimaryButton
                    onClick={() => {
                        history.push(`/thesis/student/thesis_defense_plan/assigned/${rowData?.thesisDefensePlanId}/defense_jury/${rowData?.defenseJuryId}`);
                    }}
                    variant="contained"
                    color="error"
                >
                    Xem hội đồng
                </PrimaryButton>
            ),
        },
    ];

    return (
        <Box>
            {loading && <ModalLoading />}
            <StandardTable
                title={"Danh sách đồ án"}
                data={thesis}
                columns={columns}
                options={{
                    selection: false,
                    pageSize: 5,
                    search: true,
                    sorting: true,
                }}
            />
        </Box>
    )
}