import React, {useEffect, useState} from "react";
import {request} from "../../api";
import {StandardTable} from "erp-hust/lib/StandardTable";
import IconButton from "@mui/material/IconButton";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import { toFormattedDateTime } from "../../utils/dateutils";
function LmsLogs() {

    const [logs, setLogs] = useState([]);

    useEffect(() => {
        request("get", "/log/get-logs", (res) => {
            //setLogs(res.data);
            const data = res.data.map((log) => ({
                id: log.id,
                userId: log.userId,
                actionType: log.actionType,
                createdAt: toFormattedDateTime(log.createdStamp)
                
              }));
              setLogs(data);

        }).then();
    }, [])

    const columns = [
        {
            title: "id",
            field: "id",
        },
        {
            title: "User",
            field: "userId",
        },
        {
            title: "Action",
            field: "actionType",
        },
        {
            title: "Date",
            field: "createdAt",
        },
        
        {
            title: "Edit",
            sorting: false,
            render: (rowData) => (
                <IconButton
                    onClick={() => {
                        demoFunction(rowData)
                    }}
                    variant="contained"
                    color="success"
                >
                    <EditIcon/>
                </IconButton>
            ),
        }
    ];

    const demoFunction = (user) => {
        alert("You clicked on User: " + user.id)
    }

    return (
        <div>
            <StandardTable
                title="LMSLOGS"
                columns={columns}
                data={logs}
                // hideCommandBar
                options={{
                    selection: false,
                    pageSize: 20,
                    search: true,
                    sorting: true,
                }}
            />
        </div>

    );
}

export default LmsLogs;