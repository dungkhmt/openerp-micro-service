import React, {useEffect, useState} from "react";
import {request} from "../../api";
import {StandardTable} from "erp-hust/lib/StandardTable";
import IconButton from "@mui/material/IconButton";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import Switch from '@mui/material/Switch';

export const LocationScreen = () => {
    const [locations, setLocations] = useState([]);

    useEffect(() => {
        request("get", "/location/get-all", (res) => {
            setLocations(res.data);
        }).then();
    }, []);

    const label = { inputProps: { 'aria-label': 'Size switch demo' } };

    const columns = [
        {
            title: "Name",
            field: "name",
        },
        {
            title: "Image",
            field: "image",
        },
        {
            title: "Address",
            field: "address",
        },
        {
            title: "Description",
            field: "description",
        },
        {
            title: "Status",
            sorting: true,
            render: (rowData) => (
                <Switch {...label} defaultChecked />
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
                title="Location List"
                columns={columns}
                data={locations}
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
