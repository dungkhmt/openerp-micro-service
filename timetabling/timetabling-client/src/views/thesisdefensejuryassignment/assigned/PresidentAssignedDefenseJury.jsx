import { StandardTable } from "erp-hust/lib/StandardTable";
import { useKeycloak } from "@react-keycloak/web";
import PrimaryButton from "components/button/PrimaryButton";
import { useHistory, useParams } from "react-router-dom";
import KeywordChip from "components/common/KeywordChip";
// import { makeStyles } from "@material-ui/core/styles";
import { makeStyles } from "@mui/styles";
import { Verified, PendingActions } from '@mui/icons-material';
import { Chip } from "@mui/material";
import { useFetch } from "hooks/useFetch";
import ModalLoading from "components/common/ModalLoading";
const useStyles = makeStyles((theme) => ({
    card: {
        marginTop: theme.spacing(2),
    },
    approved: {
        color: "green",
        borderColor: "green",
        fontSize: "1rem",
        width: 160,
    },
    pendingApproval: {
        fontSize: "1rem",
    },
}));
export default function PresidentAssignedDefenseJury() {
    const classes = useStyles();
    const params = useParams();
    const { id } = params;
    const history = useHistory();
    const { keycloak } = useKeycloak();
    const columns = [
        { title: "Tên hội đồng", field: "name" },
        {
            title: "Ngày",
            field: "defenseDate",
            render: (rowData) => rowData.defenseDate.split("T")[0],
        },
        { title: "Số luận án tối đa", field: "maxThesis" },
        {
            title: "Keywords",
            field: "keywords",
            render: (rowData) =>
                rowData.keywords.map((item) => <KeywordChip key={item} keyword={item} />),
        },
        {
            title: "Trạng thái",
            field: "assigned",
            render: (rowData) =>
                rowData.assigned ? < Chip
                    icon={< Verified size={24} />}
                    label="Đã phân công"
                    variant="outlined"
                    color="success"
                    className={classes.approved}
                /> : < Chip
                    icon={< PendingActions size={24} />}
                    label="Chưa phân công"
                    variant="outlined"
                    color="primary"
                    className={classes.approved} />
        },
        {
            title: "",
            sorting: false,
            render: (rowData) => (
                rowData?.assigned ?
                    <PrimaryButton
                        onClick={() => {
                            history.push(`${id}/defense_jury/${rowData.id}`);
                        }}
                        variant="contained"
                        color="error"
                        sx={{ float: "right" }}
                    >
                        Xem hội đồng
                    </PrimaryButton> :
                    <PrimaryButton
                        onClick={() => {
                            history.push(`${id}/defense_jury/${rowData.id}/assign`);
                        }}
                        variant="contained"
                        color="error"
                        sx={{ float: "right" }}
                    >
                        Phân công phản biện
                    </PrimaryButton>
            ),
        },
    ];
    const { loading, data } = useFetch(`thesis-defense-plan/get-assigned-for-teacher/${keycloak?.tokenParsed?.email}/${id}/president`);
    const defenseJuries = data?.defenseJuries?.map((item) => ({
        ...item,
        keywords: item?.academicKeywordList.map((item) => item.keyword),
    }));
    return (
        <>
            {loading && <ModalLoading loading={loading} />}
            <StandardTable
                title={"Danh sách hội đồng bảo vệ"}
                data={defenseJuries}
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