import React, { useState, useEffect, useRef } from 'react';
import {
    Box,
    Typography,
    IconButton,
    Tooltip,
    Paper,
    TextField,
    MenuItem,
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import ContentCopyIcon from '@mui/icons-material/ContentCopy';
import { useNavigate, useParams } from 'react-router-dom';
import { request } from "../../../api";

const WarehouseLayout = () => {
    const navigate = useNavigate();
    const { id } = useParams();
    const [warehouseInfo, setWarehouseInfo] = useState(null);
    const [bays, setBays] = useState([]);
    const [shelf, setShelf] = useState(0);
    const containerRef = useRef(null);
    const [containerSize, setContainerSize] = useState({ width: 800, height: 400 });

    useEffect(() => {
        if (id) {
            request("get", `/warehouses/${id}`, (res) => {
                setWarehouseInfo(res.data);
            }, {});
        }
    }, [id]);

    useEffect(() => {
        if (id) {
            request("get", `/bays/full?warehouseId=${id}&shelf=${shelf}`, (res) => {
                setBays(res.data);
            }, {});
        }
    }, [id, shelf]);

    useEffect(() => {
        const observer = new ResizeObserver((entries) => {
            for (let entry of entries) {
                const width = entry.contentRect.width;
                if (warehouseInfo) {
                    const aspectRatio = warehouseInfo.width / warehouseInfo.length; // 15/30 = 0.5
                    const height = width * aspectRatio;
                    setContainerSize({ width, height });
                }
            }
        });

        if (containerRef.current) {
            observer.observe(containerRef.current);
        }

        return () => {
            if (containerRef.current) {
                observer.unobserve(containerRef.current);
            }
        };
    }, [warehouseInfo]);

    const handleCopy = (text) => {
        navigator.clipboard.writeText(text);
    };

    const renderBays = () => {
        if (!warehouseInfo || !containerSize.width) return null;
        
        const unit = containerSize.width / warehouseInfo.length;

        return bays.map((bay) => (
            <Tooltip
                key={bay.bayId}
                title={`Length: ${bay.xlong}, Width: ${bay.ylong}`}
                arrow
            >
                <Box
                    onClick={() => navigate(`${bay.bayId}`)}
                    sx={{
                        position: 'absolute',
                        left: bay.x * unit,
                        top: bay.y * unit,
                        width: bay.xlong * unit,
                        height: bay.ylong * unit,
                        backgroundColor: '#90caf9',
                        borderRadius: 1,
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        fontWeight: 'bold',
                        color: '#fff',
                        boxShadow: 1,
                        border: '1px solid #42a5f5',
                        cursor: 'pointer',
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
                <IconButton onClick={() => navigate('/warehouse-manager/warehouse')} sx={{ color: 'grey.700', mr: 1 }}>
                    <ArrowBackIcon />
                </IconButton>
                <Typography variant="h6" gutterBottom sx={{ ml: 1 }}>
                    Warehouse Layout
                </Typography>
            </Box>

            {warehouseInfo && (
                <>
                    <Box sx={{ mb: 2 }}>
                        <Paper elevation={2} sx={{ p: 2, display: 'flex', flexDirection: 'column', gap: 1 }}>
                            <Typography variant="h6">
                                {warehouseInfo.name} ({warehouseInfo.code})
                            </Typography>
                            <Typography variant="body2">
                                <strong>Address:</strong> {warehouseInfo.address}
                                <IconButton size="small" onClick={() => handleCopy(warehouseInfo.address)}>
                                    <ContentCopyIcon fontSize="inherit" />
                                </IconButton>
                            </Typography>
                            <Typography variant="body2">
                                <strong>Dimensions:</strong> {warehouseInfo.length}m (L)  × {warehouseInfo.width}m (W)
                            </Typography>
                            <Typography variant="body2">
                                <strong>Area:</strong> {warehouseInfo.width * warehouseInfo.length} m²
                            </Typography>
                            <Typography variant="body2">
                                <strong>Coordinates:</strong> ({warehouseInfo.latitude?.toFixed(6)}, {warehouseInfo.longitude?.toFixed(6)})
                                <IconButton size="small" onClick={() => handleCopy(`${warehouseInfo.latitude}, ${warehouseInfo.longitude}`)}>
                                    <ContentCopyIcon fontSize="inherit" />
                                </IconButton>
                            </Typography>
                        </Paper>
                    </Box>

                    <Box sx={{ mb: 2, mt: 2, width: "20%" }}>
                        <TextField
                            select
                            aria-label="Shelf"
                            value={shelf}
                            onChange={(e) => setShelf(e.target.value)}
                            fullWidth
                            InputProps={{ sx: { height: 48 } }}
                        >
                            {[0, 1, 2, 3, 4].map((s) => (
                                <MenuItem key={s} value={s}>
                                    Shelf {s}
                                </MenuItem>
                            ))}
                        </TextField>
                    </Box>

                    <Box ref={containerRef} sx={{ width: '100%' }}>
                        <Paper
                            elevation={3}
                            sx={{
                                position: 'relative',
                                width: '100%',
                                height: containerSize.height,
                                backgroundColor: '#e3f2fd',
                                border: '1px solid #90caf9',
                                overflow: 'hidden',
                                mt: 2,
                            }}
                        >
                            {renderBays()}
                        </Paper>
                    </Box>
                </>
            )}
        </Box>
    );
};

export default WarehouseLayout;
