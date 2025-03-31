import React, {useEffect, useState} from 'react';
import {request} from "../../api";
import IconButton from "@mui/material/IconButton";
import MapIcon from "@mui/icons-material/Map";
import VisibilityIcon from "@mui/icons-material/Visibility";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import CheckIcon from '@mui/icons-material/Check';

import StandardTable from "../../components/StandardTable";
import {Box, Button, Modal, Typography} from "@mui/material";
import Maps from "../../components/map/map";
import {useSelector} from "react-redux";
import SaveIcon from "@mui/icons-material/Save";
import {errorNoti, successNoti} from "../../utils/notification";



const InOrder = () =>{
    const [orders, setOrders] = useState([]);
    const [openModal, setOpenModal] = useState(false);
    const [selectedHub, setSelectedHub] = useState(null);
    const hubId = useSelector((state) => state.auth.hubId);

    useEffect(() => {
        request("get", `/smdeli/ordermanager/order/collected-collector/${hubId}`, (res) => {
            setOrders(res.data);
        }).then();
    }, []);

    const columns = [
        {
            title: "Mã đơn hàng",
            field: "id",
        },
        {
            title: "Người gửi",
            field: "senderName",
        },
        {
            title: "Người nhận",
            field: "recipientName",
        },
        {
            title: "Loại hình",
            field: "orderType"
        },
        {
            title: "Trạng thái",
            field: "status"
        },
        {
            title: "Thao tác",
            field: "actions", // Field này vẫn cần để tránh lỗi nếu StandardTable sử dụng nó
            centerHeader: true,
            sorting: false,
            renderCell: (rowData) => ( // Sử dụng renderCell thay vì render
                <div >
                    <IconButton
                        onClick={() => handleEdit(rowData)}
                        color="success"
                    >
                        <VisibilityIcon />
                    </IconButton>
                    <IconButton
                        onClick={() => confirmHandle(rowData.id)}
                        color="success"
                    >
                        <CheckIcon />
                    </IconButton>
                </div>
            ),
        },
    ];

    const handleShowLocation = (hub) => {
        setSelectedHub(hub);
        setOpenModal(true);
    };

    const handleEdit = (hub) => {
        window.location.href = `/hubmanager/hub/update/${hub.id}`;
    };

    const handleDelete = (hub) => {


    };
    const confirmHandle = (selectedIds) => {
        const ids = Array.isArray(selectedIds) && selectedIds.length > 1
            ? selectedIds.join(',')
            : selectedIds;

        request(
            "put",
            `smdeli/ordermanager/collected-hub/complete/${ids}`,
            (res) => {
                successNoti("Cập nhật trạng thái cho sản phẩm thành công");
                window.location.reload();
            },
            {
                500: () => errorNoti("Có lỗi xảy ra, vui lòng thử lại sau")
            }
        )

    }

    return (
        <div>
            <StandardTable
                title="Danh sách đơn hàng đã thu gom"
                columns={columns}
                data={orders}
                rowKey="id" // Use whatever unique identifier your data has
                editable={true}
                deletable={false}
                editable={
                    true
                }
                actions={[
                    {
                        iconOnClickHandle: confirmHandle,
                        tooltip: "Duyệt vào",
                    }
                ]}
                options={{
                    selection: true,
                    pageSize: 20,
                    search: true,
                    sorting: true,

                }}

            />

        </div>
    );
}
export default InOrder;