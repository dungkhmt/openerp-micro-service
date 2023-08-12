import * as React from 'react';
import Box from '@mui/material/Box';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TablePagination from '@mui/material/TablePagination';
import TableRow from '@mui/material/TableRow';
import TableSortLabel from '@mui/material/TableSortLabel';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import Paper from '@mui/material/Paper';
import Checkbox from '@mui/material/Checkbox';
import IconButton from '@mui/material/IconButton';
import Tooltip from '@mui/material/Tooltip';
import FormControlLabel from '@mui/material/FormControlLabel';
import Switch from '@mui/material/Switch';
import DeleteIcon from '@mui/icons-material/Delete';
import FilterListIcon from '@mui/icons-material/FilterList';
import { visuallyHidden } from '@mui/utils';
import { Chip, Icon } from '@mui/material';
import { colorStatus, menuIconMap } from 'config/menuconfig';
import { useHistory } from 'react-router-dom';
import { deleteTruck, getTrucks } from 'api/TruckAPI';

const DEFAULT_ORDER = 'asc';
const DEFAULT_ORDER_BY = 'calories';
const headCells = [
    {
        id: 'code',
        numeric: false,
        disablePadding: false,
        label: 'Truck Code',
        width: '12%'
    },
    {
        id: 'facility',
        numeric: false,
        disablePadding: false,
        label: 'Facility Name',
        width: '13%'
    },
    {
        id: 'driver',
        numeric: false,
        disablePadding: false,
        label: 'Driver Name',
        width: '13%'
    },
    {
        id: 'status',
        numeric: false,
        disablePadding: false,
        label: 'Status',
        width: '13%'
    },
    {
        id: 'licensePlates',
        numeric: false,
        disablePadding: false,
        label: 'License Plates',
        width: '13%'
    },
    {
        id: 'brand',
        numeric: false,
        disablePadding: false,
        label: 'Brand',
        width: '10%'
    },
    {
        id: 'createdAt',
        numeric: false,
        disablePadding: false,
        label: 'Created At',
        width: '12%'
    },
    {
        id: 'view',
        numeric: false,
        disablePadding: false,
        label: '',
        width: '10%'
    },
];
function EnhancedTableHead(props) {
    const { onSelectAllClick, order, orderBy, numSelected, rowCount } = props;

    return (
        <TableHead>
            <TableRow>
                {/* <TableCell padding="checkbox">
                    <Checkbox
                        color="primary"
                        indeterminate={numSelected > 0 && numSelected < rowCount}
                        checked={rowCount > 0 && numSelected === rowCount}
                        onChange={onSelectAllClick}
                        inputProps={{
                            'aria-label': 'select all desserts',
                        }}
                    />
                </TableCell> */}
                {headCells.map((headCell) => (
                    <TableCell
                        key={headCell.id}
                        align={headCell.numeric ? 'right' : 'left'}
                        padding={headCell.disablePadding ? 'none' : 'normal'}
                        sortDirection={orderBy === headCell.id ? order : false}
                        width={headCell.width}
                    >
                        {headCell.label}
                    </TableCell>
                ))}
            </TableRow>
        </TableHead>
    );
}

export default function TruckInFacility(props) {
    const { facilityId, setToast, setToastType, setToastMsg } = props;
    const [order, setOrder] = React.useState(DEFAULT_ORDER);
    const [orderBy, setOrderBy] = React.useState(DEFAULT_ORDER_BY);
    const [selected, setSelected] = React.useState([]);
    const history = useHistory();

    const [page, setPage] = React.useState(0);
    const [rowsPerPage, setRowsPerPage] = React.useState(5);
    const [count, setCount] = React.useState(0);

    const [trucks, setTrucks] = React.useState([]);

    const [load, setLoad] = React.useState(false);

    React.useEffect(() => {
        getTrucks({ page: page, pageSize: rowsPerPage, facilityId: facilityId }).then((res) => {
            console.log("truck==========", res?.data.truckModels)
            setTrucks(res?.data.truckModels);
            setCount(res?.data.count);
        });
    }, [page, rowsPerPage, count, load])
    const handleSelectAllClick = (event) => {
        if (event.target.checked) {
            const newSelected = trucks.map((n) => n.name);
            setSelected(newSelected);
            return;
        }
        setSelected([]);
    };

    const handleClick = (event, id) => {
        const selectedIndex = selected.indexOf(id);
        let newSelected = [];

        if (selectedIndex === -1) {
            newSelected = newSelected.concat(selected, id);
        } else if (selectedIndex === 0) {
            newSelected = newSelected.concat(selected.slice(1));
        } else if (selectedIndex === selected.length - 1) {
            newSelected = newSelected.concat(selected.slice(0, -1));
        } else if (selectedIndex > 0) {
            newSelected = newSelected.concat(
                selected.slice(0, selectedIndex),
                selected.slice(selectedIndex + 1),
            );
        }

        setSelected(newSelected);
    };

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event) => {
        const updatedRowsPerPage = parseInt(event.target.value, 10);
        setRowsPerPage(updatedRowsPerPage);

        setPage(0);
    };

    const isSelected = (name) => selected.indexOf(name) !== -1;
    const handleDetail = (id) => {
        history.push({
            pathname: `/truck/detail/${id}`,
        })
    }
    const handleDelete = (uid) => {
        deleteTruck(uid).then((res) => {
          console.log(res);
          setToastMsg("Delete Truck Success");
          setToastType("success");
          setToast(true);
          setLoad(!load);
          setTimeout(() => {
            setToast(false);
          }, "3000");
        })
      }
    return (
        <Box sx={{ width: '100%', display: "flex", justifyContent: "center", backgroundColor: "white" }}>
            <Paper sx={{ width: '95%', mb: 2, boxShadow: "none" }}>
                <TableContainer>
                    <Table
                        sx={{ minWidth: 750 }}
                        aria-labelledby="tableTitle"
                    >
                        <EnhancedTableHead
                            numSelected={selected.length}
                            order={order}
                            orderBy={orderBy}
                            onSelectAllClick={handleSelectAllClick}
                            rowCount={trucks?.length}
                            headCells={headCells}
                        />
                        <TableBody>
                            {trucks
                                ? trucks.map((row, index) => {
                                    const isItemSelected = isSelected(row.id);
                                    const labelId = `enhanced-table-checkbox-${index}`;

                                    return (
                                        <TableRow
                                            hover
                                            // onClick={(event) => handleClick(event, row.name)}
                                            role="checkbox"
                                            aria-checked={isItemSelected}
                                            tabIndex={-1}
                                            key={row.id}
                                            selected={isItemSelected}
                                            sx={{ cursor: 'pointer' }}
                                        >
                                            {/* <TableCell padding="checkbox">
                                                <Checkbox
                                                    color="primary"
                                                    checked={isItemSelected}
                                                    inputProps={{
                                                        'aria-labelledby': labelId,
                                                    }}
                                                    onClick={(event) => handleClick(event, row.id)}
                                                />
                                            </TableCell> */}
                                            <TableCell
                                                component="th"
                                                id={labelId}
                                                scope="row"
                                                align="left"
                                            >
                                                {row.truckCode}
                                            </TableCell>
                                            <TableCell align="left">{row.facilityResponsiveDTO.facilityName}</TableCell>
                                            <TableCell align="left">{row.driverName}</TableCell>
                                            <TableCell align="left">
                                                <Chip label={row.status} color={colorStatus.get(row.status)} />
                                            </TableCell>
                                            <TableCell align="left">{row.licensePlates}</TableCell>
                                            <TableCell align="left">{row.brandTruck}</TableCell>
                                            <TableCell align="left">{new Date(row.createdAt).toLocaleDateString()}</TableCell>
                                            <TableCell >
                                                <Box sx={{ display: 'flex' }}>
                                                    <Tooltip title="View">
                                                        <Box
                                                            onClick={() => { handleDetail(row?.uid) }}
                                                        >
                                                            <Icon className='icon-view-screen'>{menuIconMap.get("RemoveRedEyeIcon")}</Icon>
                                                        </Box>
                                                    </Tooltip>
                                                    {row.status === "AVAILABLE" ? (
                                                        <Tooltip title="Delete">
                                                            <Box onClick={() => handleDelete(row?.uid)}>
                                                                <Icon className='icon-view-screen' sx={{ marginLeft: '8px' }}>{menuIconMap.get("DeleteForeverIcon")}</Icon>
                                                            </Box>
                                                        </Tooltip>) : null}
                                                </Box>
                                            </TableCell>
                                        </TableRow>
                                    );
                                })
                                : null}
                        </TableBody>
                    </Table>
                </TableContainer>
                <TablePagination
                    rowsPerPageOptions={[5, 10, 25]}
                    component="div"
                    count={count}
                    rowsPerPage={rowsPerPage}
                    page={page}
                    onPageChange={handleChangePage}
                    onRowsPerPageChange={handleChangeRowsPerPage}
                />
            </Paper>
        </Box>
    );

}

