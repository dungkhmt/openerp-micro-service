import React, { useEffect, useState } from "react";
import { request } from "api";
import StandardTable from "components/StandardTable";
import IconButton from "@mui/material/IconButton";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import VisibilityIcon from "@mui/icons-material/Visibility";
import AddIcon from "@mui/icons-material/Add";
import { Box, Button, Typography } from "@mui/material";
import { useHistory } from "react-router-dom";
import { errorNoti, successNoti } from "utils/notification";
import LoadingScreen from "components/common/loading/loading";

const RoutesList = () => {
    const [routes, setRoutes] = useState([]);
    const [loading, setLoading] = useState(true);
    const history = useHistory();

    useEffect(() => {
        loadRoutes();
    }, []);

    const loadRoutes = () => {
        setLoading(true);
        request(
            "get",
            "/smdeli/middle-mile/routes",
            (res) => {
                setRoutes(res.data);
                setLoading(false);
            },
            {
                401: () => {
                    errorNoti("Không có quyền truy cập");
                    setLoading(false);
                },
                500: () => {
                    errorNoti("Có lỗi xảy ra khi tải dữ liệu");
                    setLoading(false);
                }
            }
        );
    };

    const handleView = (route) => {
        history.push(`/middle-mile/routes/${route.routeId}`);
    };

    const handleEdit = (route) => {
        history.push(`/middle-mile/routes/edit/${route.id}`);
    };

    const handleDelete = (route) => {
        if (window.confirm(`Bạn có chắc chắn muốn xóa tuyến đường ${route.routeName}?`)) {
            request(
                "delete",
                `/smdeli/middle-mile/routes/${route.id}`,
                () => {
                    successNoti("Xóa tuyến đường thành công");
                    loadRoutes();
                },
                {
                    401: () => errorNoti("Không có quyền thực hiện hành động này"),
                    500: () => errorNoti("Có lỗi xảy ra khi xóa tuyến đường")
                }
            );
        }
    };

    const handleCreateRoute = () => {
        history.push("/middle-mile/routes/create");
    };

    const columns = [
        {
            title: "Mã tuyến",
            field: "routeCode",
        },
        {
            title: "Tên tuyến",
            field: "routeName",
        },
        {
            title: "Mô tả",
            field: "description",
        },
        {
            title: "Trạng thái",
            field: "status",
            render: (rowData) => {
                switch (rowData.status) {
                    case "ACTIVE":
                        return <span style={{ color: "green" }}>Hoạt động</span>;
                    case "INACTIVE":
                        return <span style={{ color: "red" }}>Ngưng hoạt động</span>;
                    default:
                        return <span>{rowData.status}</span>;
                }
            }
        },
        {
            title: "Thao tác",
            field: "actions",
            centerHeader: true,
            sorting: false,
            renderCell: (rowData) => (
                <div style={{ display: 'flex', gap: '5px' }}>
                    <IconButton
                        style={{ padding: '5px' }}
                        onClick={() => handleView(rowData)}
                        color="primary"
                    >
                        <VisibilityIcon />
                    </IconButton>
                    <IconButton
                        style={{ padding: '5px' }}
                        onClick={() => handleEdit(rowData)}
                        color="secondary"
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
        },
    ];

    return loading ? (
        <LoadingScreen />
    ) : (
        <Box>
            <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
                <Typography variant="h5">Quản lý tuyến đường</Typography>
                <Button
                    variant="contained"
                    color="primary"
                    startIcon={<AddIcon />}
                    onClick={handleCreateRoute}
                >
                    Thêm mới tuyến đường
                </Button>
            </Box>
            <StandardTable
                title="Danh sách tuyến đường"
                columns={columns}
                data={routes}
                options={{
                    selection: false,
                    pageSize: 10,
                    search: true,
                    sorting: true,
                }}
                rowKey="id"
            />
        </Box>
    );
};

export default RoutesList;