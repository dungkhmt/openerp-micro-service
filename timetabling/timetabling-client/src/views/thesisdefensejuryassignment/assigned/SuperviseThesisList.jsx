import React, { useState } from 'react'
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import { useKeycloak } from '@react-keycloak/web';
import { useFetch } from 'hooks/useFetch';
import KeywordChip from 'components/common/KeywordChip';
import { StandardTable } from 'erp-hust/lib/StandardTable';
import ModalLoading from 'components/common/ModalLoading';
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import KeyboardArrowUpIcon from '@mui/icons-material/KeyboardArrowUp';
import { Collapse, Box, Typography } from '@mui/material';
import IconButton from '@mui/material/IconButton';
import PrimaryButton from 'components/button/PrimaryButton';
import Row from 'components/thesisdefensejury/Row'
const SuperviseThesisList = () => {
    const [open, setOpen] = useState(true);
    const { keycloak } = useKeycloak();
    console.log(keycloak?.tokenParsed?.email)
    const { loading, data: supervisedThesisList } = useFetch(
        `/thesis/get-all-by-supervisor?teacher=${keycloak?.tokenParsed?.email}`
    );
    const handleClick = () => {
        setOpen(!open);
    };
    return (
        <div>
            {loading && <ModalLoading loading={loading} />}
            {supervisedThesisList?.length > 0 ? <TableContainer component={Paper}>
                <Table aria-label="collapsible table">
                    <TableBody>
                        {supervisedThesisList?.map((row) => (
                            <Row row={row} />
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
                : <TableContainer component={Paper}>
                    <Table size="small" aria-label="purchases">
                        <TableHead>
                            <TableRow>
                                <TableCell>Tên đồ án</TableCell>
                                <TableCell>Sinh viên</TableCell>
                                <TableCell align="right">MSSV</TableCell>
                                <TableCell align="right">Keyword</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            <TableRow>
                                <TableCell colSpan={4}>Không có đồ án nào đang được phân công</TableCell>
                            </TableRow>
                        </TableBody>
                    </Table>
                </TableContainer>}

        </div>
    )
}

export default SuperviseThesisList