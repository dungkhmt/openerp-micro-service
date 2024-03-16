import React, {useEffect, useState} from "react";
import {request} from "../api";
import {StandardTable} from "erp-hust/lib/StandardTable";
import IconButton from "@mui/material/IconButton";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import Switch from '@mui/material/Switch';

export const UsersScreen = () => {

    const [users, setUsers] = useState([]);

    useEffect(() => {
        request("get", "/user/get-all", (res) => {
            setUsers(res.data);
        }).then();
    }, []);

    const label = { inputProps: { 'aria-label': 'Size switch demo' } };

    const columns = [
        {
            title: "User",
            field: "id",
        },
        {
            title: "First name",
            field: "firstName",
        },
        {
            title: "Last name",
            field: "lastName",
        },
        {
            title: "Email",
            field: "email",
        },
        {
            title: "Status",
            sorting: true,
            render: (rowData) => (
                <Switch {...label} checked={rowData.enabled} />
            )
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
        },
        {
            title: "Delete",
            sorting: false,
            render: (rowData) => (
                <IconButton
                    onClick={() => {
                        demoFunction(rowData)
                    }}
                    variant="contained"
                    color="error"
                >
                    <DeleteIcon/>
                </IconButton>
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
};