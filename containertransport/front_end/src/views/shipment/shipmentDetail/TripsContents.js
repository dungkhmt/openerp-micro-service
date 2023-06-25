import * as React from 'react';
import { alpha } from '@mui/material/styles';
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
import { useHistory } from 'react-router-dom';
import { Button, Icon } from '@mui/material';
import { menuIconMap } from 'config/menuconfig';
import { deleteTrip } from 'api/TripAPI';

function descendingComparator(a, b, orderBy) {
  if (b[orderBy] < a[orderBy]) {
    return -1;
  }
  if (b[orderBy] > a[orderBy]) {
    return 1;
  }
  return 0;
}

function getComparator(order, orderBy) {
  return order === 'desc'
    ? (a, b) => descendingComparator(a, b, orderBy)
    : (a, b) => -descendingComparator(a, b, orderBy);
}

function stableSort(array, comparator) {
  const stabilizedThis = array.map((el, index) => [el, index]);
  stabilizedThis.sort((a, b) => {
    const order = comparator(a[0], b[0]);
    if (order !== 0) {
      return order;
    }
    return a[1] - b[1];
  });
  return stabilizedThis.map((el) => el[0]);
}

const headCells = [
  {
    id: 'code',
    numeric: false,
    disablePadding: false,
    label: 'Trip Code',
    width: '11%'
  },
  {
    id: 'truckCode',
    numeric: false,
    disablePadding: false,
    label: 'Truck Code',
    width: '12%'
  },
  {
    id: 'driverName',
    numeric: false,
    disablePadding: false,
    label: 'Driver Name',
    width: '10%'
  },
  {
    id: 'numberOrder',
    numeric: false,
    disablePadding: false,
    label: 'Number Orders',
    width: '10%'
  },
  {
    id: 'created_by_user_id',
    numeric: false,
    disablePadding: false,
    label: 'Created By User',
    width: '10%'
  },
  {
    id: 'status',
    numeric: false,
    disablePadding: false,
    label: 'Status',
    width: '12%'
  },
  {
    id: 'createdAt',
    numeric: false,
    disablePadding: false,
    label: 'Created At',
    width: '13%'
  },
  {
    id: 'updatedAt',
    numeric: false,
    disablePadding: false,
    label: 'Updated At',
    width: '13%'
  },
  {
    id: 'view',
    numeric: false,
    disablePadding: false,
    label: '',
    width: '10%'
  }

];

const DEFAULT_ORDER = 'asc';
const DEFAULT_ORDER_BY = 'calories';
const DEFAULT_ROWS_PER_PAGE = 5;

function EnhancedTableHead(props) {
  const { onSelectAllClick, order, orderBy, numSelected, rowCount, onRequestSort } = props;
  const createSortHandler = (newOrderBy) => (event) => {
    onRequestSort(event, newOrderBy);
  };

  return (
    <TableHead>
      <TableRow>
        <TableCell padding="checkbox">
          <Checkbox
            color="primary"
            indeterminate={numSelected > 0 && numSelected < rowCount}
            checked={rowCount > 0 && numSelected === rowCount}
            onChange={onSelectAllClick}
            inputProps={{
              'aria-label': 'select all desserts',
            }}
          />
        </TableCell>
        {headCells.map((headCell) => (
          <TableCell
            key={headCell.id}
            align={headCell.numeric ? 'right' : 'left'}
            padding={headCell.disablePadding ? 'none' : 'normal'}
            sortDirection={orderBy === headCell.id ? order : false}
            width={headCell?.width}
          >
            <TableSortLabel
              active={orderBy === headCell.id}
              direction={orderBy === headCell.id ? order : 'asc'}
              onClick={createSortHandler(headCell.id)}
            >
              {headCell.label}
              {orderBy === headCell.id ? (
                <Box component="span" sx={visuallyHidden}>
                  {order === 'desc' ? 'sorted descending' : 'sorted ascending'}
                </Box>
              ) : null}
            </TableSortLabel>
          </TableCell>
        ))}
      </TableRow>
    </TableHead>
  );
}

export default function TripsContents({ trips, shipmentId, setToast, setToastType, setToastMsg, flag, setFlag }) {
  const [order, setOrder] = React.useState(DEFAULT_ORDER);
  const [orderBy, setOrderBy] = React.useState(DEFAULT_ORDER_BY);
  const [selected, setSelected] = React.useState([]);
  const [page, setPage] = React.useState(0);
  const [visibleRows, setVisibleRows] = React.useState(null);
  const [rowsPerPage, setRowsPerPage] = React.useState(DEFAULT_ROWS_PER_PAGE);
  const [paddingHeight, setPaddingHeight] = React.useState(0);
  const history = useHistory();

  const handleRequestSort = React.useCallback(
    (event, newOrderBy) => {
      const isAsc = orderBy === newOrderBy && order === 'asc';
      const toggledOrder = isAsc ? 'desc' : 'asc';
      setOrder(toggledOrder);
      setOrderBy(newOrderBy);

      const sortedRows = stableSort(trips, getComparator(toggledOrder, newOrderBy));
      const updatedRows = sortedRows.slice(
        page * rowsPerPage,
        page * rowsPerPage + rowsPerPage,
      );

      setVisibleRows(updatedRows);
    },
    [order, orderBy, page, rowsPerPage],
  );

  const handleSelectAllClick = (event) => {
    if (event.target.checked) {
      const newSelected = trips.map((n) => n.uid);
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

  const handleChangePage = React.useCallback(
    (event, newPage) => {
      setPage(newPage);

      const sortedRows = stableSort(trips, getComparator(order, orderBy));
      const updatedRows = sortedRows.slice(
        newPage * rowsPerPage,
        newPage * rowsPerPage + rowsPerPage,
      );

      setVisibleRows(updatedRows);

      // Avoid a layout jump when reaching the last page with empty rows.
      const numEmptyRows =
        newPage > 0 ? Math.max(0, (1 + newPage) * rowsPerPage - trips.length) : 0;

      //   const newPaddingHeight = (dense ? 33 : 53) * numEmptyRows;
      //   setPaddingHeight(newPaddingHeight);
    },
    [order, orderBy, rowsPerPage],
  );

  const handleChangeRowsPerPage = React.useCallback(
    (event) => {
      const updatedRowsPerPage = parseInt(event.target.value, 10);
      setRowsPerPage(updatedRowsPerPage);

      setPage(0);

      const sortedRows = stableSort(trips, getComparator(order, orderBy));
      const updatedRows = sortedRows.slice(
        0 * updatedRowsPerPage,
        0 * updatedRowsPerPage + updatedRowsPerPage,
      );

      setVisibleRows(updatedRows);

      // There is no layout jump to handle on the first page.
      setPaddingHeight(0);
    },
    [order, orderBy],
  );

  const isSelected = (name) => selected.indexOf(name) !== -1;
  const handleDetail = (id) => {
    history.push({
      pathname: `/shipment/trip/detail/${shipmentId}/${id}`
    })
  };
  const handleDelete = (uid) => {
    let data = [];
    data.push(uid);
    deleteTrip({ listUidTrip: data }).then((res) => {
      console.log(res);
      setToastMsg("Delete Trip Success");
      setToastType("success");
      setToast(true);
      setTimeout(() => {
        setToast(false);
      }, "3000");
      setFlag(!flag);
    })
  }
  const handleDeleteTrips = () => {
    deleteTrip({ listUidTrip: selected }).then((res) => {
      console.log(res);
      setToastMsg("Delete Trips Success");
      setToastType("success");
      setToast(true);
      setTimeout(() => {
        setToast(false);
      }, "3000");
      setFlag(!flag);
    })
  }
  console.log("trips", trips)
  return (
    <Box sx={{ width: '100%', display: "flex", justifyContent: "center", backgroundColor: "white" }}>
      <Paper sx={{ width: '95%', mb: 2, boxShadow: "none" }}>
        {selected.length > 0 ? (
          <Box sx={{ marginTop: '16px' }} className="btn-not-upcase-text"
            onClick={handleDeleteTrips}
          >
            <Button variant="contained">Delete Selected Trip </Button>
          </Box>) : null}

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
              onRequestSort={handleRequestSort}
              rowCount={trips?.length}
            />
            <TableBody>
              {trips
                ? trips.map((row, index) => {
                  const isItemSelected = isSelected(row.uid);
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
                      <TableCell padding="checkbox">
                        <Checkbox
                          color="primary"
                          checked={isItemSelected}
                          inputProps={{
                            'aria-labelledby': labelId,
                          }}
                          onClick={(event) => handleClick(event, row.uid)}
                        />
                      </TableCell>
                      <TableCell
                        align="center"
                        onClick={() => history.push({
                          pathname: `/shipment/trip/detail/${shipmentId}/${row.id}`
                        })}
                      >
                        {row.code}
                      </TableCell>
                      <TableCell align="center">{row?.truckCode}</TableCell>
                      <TableCell align="center">{row?.driverName}</TableCell>
                      <TableCell align="center">{row?.orderIds?.length}</TableCell>
                      <TableCell align="left">{row?.created_by_user_id}</TableCell>
                      <TableCell align="left">{row?.status}</TableCell>
                      <TableCell align="left">{new Date(row?.createdAt).toLocaleDateString()}</TableCell>
                      <TableCell align="left">{new Date(row?.updatedAt).toLocaleDateString()}</TableCell>
                      <TableCell>
                        <Box sx={{ display: 'flex' }}>
                          <Tooltip title="View">
                            <Box onClick={() => { handleDetail(row?.uid) }} >
                              <Icon className='icon-view-screen'>{menuIconMap.get("RemoveRedEyeIcon")}</Icon>
                            </Box>
                          </Tooltip>
                          <Tooltip title="Delete">
                            <Box onClick={() => { handleDelete(row?.uid) }}>
                              <Icon className='icon-view-screen' sx={{ marginLeft: '8px' }}>{menuIconMap.get("DeleteForeverIcon")}</Icon>
                            </Box>
                          </Tooltip>
                        </Box>
                      </TableCell>
                    </TableRow>
                  );
                })
                : null}
              {paddingHeight > 0 && (
                <TableRow
                  style={{
                    height: paddingHeight,
                  }}
                >
                  <TableCell colSpan={6} />
                </TableRow>
              )}
            </TableBody>
          </Table>
        </TableContainer>
        <TablePagination
          rowsPerPageOptions={[5, 10, 25]}
          component="div"
          count={trips.length}
          rowsPerPage={rowsPerPage}
          page={page}
          onPageChange={handleChangePage}
          onRowsPerPageChange={handleChangeRowsPerPage}
        />
      </Paper>
    </Box>
  );
}
