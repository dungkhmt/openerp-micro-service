import { StandardTable } from "erp-hust/lib/StandardTable";
import { useKeycloak } from "@react-keycloak/web";
import { useFetchData } from "hooks/useFetchData";
import PrimaryButton from "component/button/PrimaryButton";
import { useNavigate } from "react-router-dom";

export default function StudentAssignedDefenseJury() {
    const { keycloak } = useKeycloak();
    const navigate = useNavigate();
    const studentEmail = keycloak.tokenParsed?.email;
    const thesis = useFetchData(`/thesis/get-all?student-email=${studentEmail}`);
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
                        navigate(`/thesis/student/thesis_defense_plan/assigned/${rowData?.thesisDefensePlanId}/defense_jury/${rowData?.defenseJuryId}`);
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
    )
}