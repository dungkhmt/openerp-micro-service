import React, { useState } from 'react'
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import KeyboardArrowUpIcon from '@mui/icons-material/KeyboardArrowUp';
import KeywordChip from 'components/common/KeywordChip';
import { StandardTable } from 'erp-hust/lib/StandardTable';
import ModalLoading from 'components/common/ModalLoading';
import { Collapse, Box, Typography } from '@mui/material';
import IconButton from '@mui/material/IconButton';
import PrimaryButton from 'components/button/PrimaryButton';
import { useParams, useHistory } from "react-router-dom";
const Row = ({ row }) => {
    const [open, setOpen] = useState(false);
    const history = useHistory();

    return (
        <>
            <TableRow sx={{ '& > *': { borderBottom: 'unset' } }}>
                <TableCell>
                    <IconButton
                        aria-label="expand row"
                        size="small"
                        onClick={() => setOpen(!open)}
                    >
                        {open ? <KeyboardArrowUpIcon /> : <KeyboardArrowDownIcon />}
                    </IconButton>
                </TableCell>
                <TableCell component="th" scope="row" colSpan={3}>
                    {row?.thesisDefensePlan}
                </TableCell>
            </TableRow>
            <TableRow>
                <TableCell style={{ paddingBottom: 0, paddingTop: 0 }} colSpan={6}>
                    <Collapse in={open} timeout="auto" unmountOnExit>
                        <Box sx={{ margin: 1 }}>
                            <Typography variant="h6" gutterBottom component="div">
                                History
                            </Typography>
                            <Table size="small" aria-label="purchases">
                                <TableHead>
                                    <TableRow>
                                        <TableCell style={{ width: 300 }} >Tên đồ án</TableCell>
                                        <TableCell>Sinh viên</TableCell>
                                        <TableCell style={{ width: 160 }} >MSSV</TableCell>
                                        <TableCell style={{ width: 160 }} >Keyword</TableCell>
                                        <TableCell style={{ width: 200 }} >Phân ban</TableCell>
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {row?.thesisList?.map((item) => (
                                        <TableRow key={item?.id}>
                                            <TableCell style={{ width: 300 }} component="th" scope="row">
                                                {item?.thesisName}
                                            </TableCell>
                                            <TableCell>{item?.studentName}</TableCell>
                                            <TableCell style={{ width: 160 }}>{item?.studentId}</TableCell>
                                            <TableCell style={{ width: 160 }}>
                                                {item?.academicKeywordList?.map((kw) => <KeywordChip key={kw?.keyword} keyword={kw?.description} />)}
                                            </TableCell>
                                            <TableCell style={{ width: 200 }}>
                                                {item?.juryTopic
                                                    ? <KeywordChip keyword={item?.juryTopic?.name} />
                                                    : <PrimaryButton onClick={() => { history.push(`/thesis/teacher/superviser/assign/${item?.id}`) }}>
                                                        Phân loại đồ án
                                                    </PrimaryButton>}
                                            </TableCell>
                                        </TableRow>
                                    ))}
                                </TableBody>
                            </Table>
                        </Box>
                    </Collapse>
                </TableCell>
            </TableRow>
        </>
    )
}

export default Row