import { useState } from "react";
import { StandardTable } from "erp-hust/lib/StandardTable";
import { useKeycloak } from "@react-keycloak/web";
import { useFetch } from "hooks/useFetch";
import PrimaryButton from "components/button/PrimaryButton";
import { useHistory, useRouteMatch } from "react-router-dom";
import ModalLoading from "components/common/ModalLoading";
// - Theo dõi đợt bảo vệ được phân công của chủ tịch
export default function PresidentAssignedThesisDefensePlan() {
    const { keycloak } = useKeycloak();
    const history = useHistory();
    const { path } = useRouteMatch();
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
                        history.push(`${path}/${rowData.id}`);
                    }}
                    variant="contained"
                    color="error"
                >
                    Xem hội đồng
                </PrimaryButton>
            ),
        },
    ];
    const { loading, data: plans } = useFetch(`thesis-defense-plan/get-assigned-for-teacher/${keycloak?.tokenParsed?.email}/president`);
    return (
        <>
            {loading && <ModalLoading loading={loading} />}
            <StandardTable
                title={"Danh sách các đợt bảo vệ làm chủ tịch"}
                data={plans}
                columns={columns}
                options={{
                    selection: false,
                    pageSize: 5,
                    search: true,
                    sorting: true,
                }}
            />
        </>
    )
}