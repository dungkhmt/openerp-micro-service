import Button from "@mui/material/Button";
import React, {useEffect, useState} from "react";
import {request} from "../api";
import {StandardTable} from "erp-hust/dist/StandardTable";

function DemoScreen() {

    const [users, setUsers] = useState([]);

    useEffect(() => {
        request("get", "/user/get-all", (res) => {
            setUsers(res.data);
        }).then();
    }, [])

    const columns = [
        {
            title: "User",
            field: "id",
        },
        {
            title: "Creation time",
            field: "createdOn",
        },
        {
            title: "Action",
            sorting: false,
            render: (rowData) => (
                <Button
                    variant="contained"
                    color="error"
                    onClick={() => demoFunction(rowData)}
                >
                    Detail
                </Button>
            ),
        },
    ];

    const demoFunction = (user) => {
        alert("You clicked on User: " + user.id)
    }

    return (
        <div>
            <StandardTable
                title="User List"
                columns={columns}
                data={users}
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

export default DemoScreen;