import {IconButton, Typography} from "@material-ui/core/";
import StandardTable from "component/table/StandardTable";
import React from "react";

// export const useStyles = makeStyles((theme) => ({
//     commandBar: {
//         position: "sticky",
//         top: 112,
//         zIndex: 11,
//         marginTop: -theme.spacing(3),
//         marginBottom: theme.spacing(3),
//     },
// }));

const MemberTable = ({ members }) => {
    // Command delete button
    const cellStyles = { headerStyle: { padding: 8 }, cellStyle: { padding: 8 } };
    const alignRightCellStyles = {
        headerStyle: { padding: 8, textAlign: "right" },
        cellStyle: { padding: 8, textAlign: "right" },
    };
    const columns = [
        { title: "Mã thành viên", field: "partyId", ...cellStyles },
        { title: "Tên thành viên", field: "fullName", ...cellStyles },
        { title: "Tên đăng nhập", field: "userLoginId", ...cellStyles },
        // { title: "Thời gian thêm vào dự án", field: "", ...cellStyles },
    ];
    return (
        <>
            <StandardTable
                title=""
                hideCommandBar
                columns={columns}
                data={members}
                onSelectionChange={(selectedRows) => setSelectedRows(selectedRows)}
                options={{ selection: false, pageSize: 10 }}
            />
        </>
    );
}

export default MemberTable;