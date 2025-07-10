import React, { useEffect, useState } from "react";
import { request } from "api";
import StandardTable  from "../../../components/StandardTable";
import IconButton from "@mui/material/IconButton";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import VisibilityIcon from '@mui/icons-material/Visibility';

import MapIcon from "@mui/icons-material/Map";
import { Modal, Box, Typography, Button } from '@mui/material';
import Maps from 'components/map/map';
import {errorNoti, successNoti} from "../../../utils/notification";
import {useSelector} from "react-redux";

function ColectorList() {
    const [shippers, setShippers] = useState([]);
    const [selectedShipper, setSelectedShipper] = useState(null);
    const hubId = useSelector((state) => state.auth.user?.hubId);
    const role = useSelector((state) => state.auth.role);

    useEffect(() => {
        let url = "/smdeli/humanresource/shipper"
        if(role !== "ADMIN") {
            url += "/hub/" + hubId;
        }
        request("get", url, (res) => {
            setShippers(res.data);
        }).then();
    }, []);
    const columns = [
        {
            title: "Mã nhân viên",
            field: "id",
        },
        {
            title: "Tên nhân viên",
            field: "name",
        },
        {
            title: "Số điện thoại",
            field: "phone",
        },
        {
            title: "Email",
            field: "email",
        },
        {
            title: "Thao tác",
            field: "actions", // Field này vẫn cần để tránh lỗi nếu StandardTable sử dụng nó
            centerHeader: true,
            sorting: false,
            renderCell: (rowData) => ( // Sử dụng renderCell thay vì render
                <div style={{ display: 'flex', gap: '5px', padding: '0px'}}>
                    <IconButton
                        style={{ padding: '5px' }}
                        onClick={() => handleEdit(rowData)}

                        color="success"
                    >
                        <VisibilityIcon />
                    </IconButton>
                    <IconButton
                        style={{ padding: '5px' }}
                        onClick={() => handleEdit(rowData)}
                    >
                        <EditIcon />
                    </IconButton>
                    <IconButton
                        style={{ padding: '5px' }}
                        onClick={() => handleDelete(rowData)}
                        color="error"
                    >
                        <DeleteIcon />
                    </IconButton>
                </div>
            ),
        }

    ];


    const handleEdit = (shipper) => {
        window.location.href = `/employee/shipper/update/${shipper.id}`;
    };

    const handleDelete = (shipper) => {
        const isConfirmed = window.confirm(`Bạn có chắc muốn xóa nhân viên giao hàng: ${shipper.name} - Địa chỉ: ${shipper.address}?`);

        if (isConfirmed) {

            request(
                "delete",
                `/smdeli/humanresource/shipper/${shipper.shipperId}`,
                (res) => {
                    if (res.status === 200) {
                        successNoti("Xóa shipper thành công",2000);
                        setShippers(shippers.filter(c => c.shipperId !== shipper.shipperId));
                    }
                },
                (error) => {
                    // Thêm callback cho lỗi
                    errorNoti("Xóa shipper thất bại. Lỗi: " + error.message, 2000);
                }
            );
        }
    };

    return (
        <div>
            <StandardTable
                title="Danh sách nhân viên giao hàng"
                columns={columns}
                data={shippers}
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

export default ColectorList;
