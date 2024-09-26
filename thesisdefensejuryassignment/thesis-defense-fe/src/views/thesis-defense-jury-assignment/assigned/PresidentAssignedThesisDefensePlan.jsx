
import { StandardTable } from "erp-hust/lib/StandardTable";
import { useKeycloak } from "@react-keycloak/web";
import { useFetchData } from "hooks/useFetchData";
import PrimaryButton from "component/button/PrimaryButton";
import { useNavigate } from "react-router-dom";

export default function PresidentAssignedThesisDefensePlan() {
    const { keycloak } = useKeycloak();
    const navigate = useNavigate();

    const columns = [
        { title: "ID", field: "id" },
        { title: "Tên đợt bảo vệ", field: "name" },
        { title: "Kì học", field: "semester" },
        {
            title: "Ngày bắt đầu",
            field: "startDate",
            render: (rowData) => rowData?.startDate?.split("T")[0],
        },
        {
            title: "Ngày kết thúc",
            field: "endDate",
            render: (rowData) => rowData?.endDate?.split("T")[0],
        },
        {
            title: "",
            sorting: false,
            render: (rowData) => (
                <PrimaryButton
                    onClick={() => {
                        navigate(`${rowData.id}`);
                    }}
                    variant="contained"
                    color="error"
                >
                    Xem hội đồng
                </PrimaryButton>
            ),
        },
    ];
    const plans = useFetchData(`thesis-defense-plan/get-assigned-for-teacher/${keycloak?.tokenParsed?.email}/president`);
    return (
        <StandardTable
            title={"Danh sách đợt bảo vệ"}
            data={plans}
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