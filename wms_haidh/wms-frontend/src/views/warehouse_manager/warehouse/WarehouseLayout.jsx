import React, { useState, useEffect } from 'react';
import {
    Box,
    Typography,
    IconButton,
    Tooltip,
    Paper
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { useNavigate, useParams } from 'react-router-dom';
import { request } from "../../../api";

const WarehouseLayout = () => {
    const navigate = useNavigate();
    const { id } = useParams();
    const [warehouseInfo, setWarehouseInfo] = useState(null);
    const [bays, setBays] = useState([]);

    useEffect(() => {
        if (id) {
            request("get", `/warehouses/${id}`, (res) => {
                setWarehouseInfo(res.data);
            }, {});
            request("get", `/bays/full?warehouseId=${id}`, (res) => {
                setBays(res.data);
            }, {});
        }
    }, [id]);

    const renderBays = () => {
        if (!warehouseInfo) return null;

        return bays.map((bay) => (
            <Tooltip
                key={bay.bayId}
                title={`Length: ${bay.xlong}, Width: ${bay.ylong}`}
                arrow
            >
                <Box
                    onClick={() => navigate(`${bay.bayId}`)}
                    sx={{
                        gridColumn: `${bay.x + 1} / span ${bay.xlong}`,
                        gridRow: `${bay.y + 1} / span ${bay.ylong}`,
                        backgroundColor: '#90caf9',
                        borderRadius: 1,
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        fontWeight: 'bold',
                        color: '#fff',
                        boxShadow: 1,
                        border: '1px solid #42a5f5',
                        cursor: 'pointer', // Đổi con trỏ khi hover
                        '&:hover': {
                            backgroundColor: '#64b5f6',
                        }
                    }}
                >
                    {bay.code}
                </Box>
            </Tooltip>
        ));
    };


    return (
        <Box sx={{ p: 3, display: 'flex', flexDirection: 'column' }}>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <IconButton onClick={() => navigate('/admin/warehouse')} sx={{ color: 'grey.700', mr: 1 }}>
                    <ArrowBackIcon />
                </IconButton>
                <Typography variant="h6" gutterBottom sx={{ ml: 1 }}>
                    Warehouse Layout
                </Typography>
            </Box>

            {warehouseInfo && (
                <>
                    <Typography variant="subtitle1" gutterBottom>
                        {warehouseInfo.name} - {warehouseInfo.code}
                    </Typography>

                    <Paper elevation={3} sx={{
                        p: 2,
                        mt: 2,
                        display: 'grid',
                        gridTemplateColumns: `repeat(${warehouseInfo.width}, 20px)`,
                        gridTemplateRows: `repeat(${warehouseInfo.length}, 20px)`,
                        gap: '2px',
                        backgroundColor: '#e3f2fd',
                        overflow: 'auto',
                        border: '1px solid #90caf9'
                    }}>
                        {renderBays()}
                    </Paper>
                </>
            )}
        </Box>
    );
};

export default WarehouseLayout;
