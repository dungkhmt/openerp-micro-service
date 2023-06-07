import SearchIcon from "@mui/icons-material/Search";
import * as React from 'react';
import PropTypes from 'prop-types';
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
import DeleteIcon from '@mui/icons-material/Delete';
import AddIcon from '@mui/icons-material/Add';
import SaveIcon from '@mui/icons-material/Save';
import { visuallyHidden } from '@mui/utils';
import { Button, Modal, TextField } from '@mui/material';
import CommandBarButton from './button/commandBarButton';

const buildHeaderCells = ( columns ) => {
  return columns.map(c => {
    return { id: c.field, label: c.title, numeric: c.type == "numeric" };
  });
}

const buildTableData = ( data, columns ) => {
  const headerCells = buildHeaderCells(columns);
  const fields = headerCells.map(header => header.id);
  const tableData = [];
  for (var i = 0; i < data?.length; i++) {
    const row = data[i];
    const adder = {};
    for (var j = 0; j < fields.length; j++) {
      const field = fields[j];
      adder[field] = row[field];
    }
    // tableData.push(adder);
    tableData.push(row);
  }
  console.log("Table data => ", tableData);
  return tableData;
}

const descendingComparator = (a, b, orderBy) => {
  if (b[orderBy] < a[orderBy]) {
    return -1;
  }
  if (b[orderBy] > a[orderBy]) {
    return 1;
  }
  return 0;
}

const getComparator = (order, orderBy) => {
  return order === 'desc'
    ? (a, b) => descendingComparator(a, b, orderBy)
    : (a, b) => -descendingComparator(a, b, orderBy);
}

const stableSort = (array, comparator) => {
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

const SearchBar = ( { setSearchQuery } ) => {
  return (
    <form>
      <TextField
        id="search-bar"
        className="text"
        onInput={(e) => {
          setSearchQuery(e.target.value);
        }}
        label="Tìm kiếm..."
        variant="outlined"
        placeholder="Tìm kiếm..."
        fullWidth
      />
      {/* <IconButton type="submit" aria-label="search">
        <SearchIcon style={{ fill: "blue" }} />
      </IconButton> */}
    </form>
  );
}
SearchBar.propTypes = {
  setSearchQuery: PropTypes.func.isRequired
};


const EnhancedTableHead = ( props ) => {
  const { headerCells, onSelectAllClick, order, orderBy, numSelected, rowCount, 
    onRequestSort, isEditable, options } = props;
  const createSortHandler = (property) => (event) => {
    onRequestSort(event, property);
  };

  return (
    <TableHead>
      <TableRow>
        {
          isEditable && options.selection != false && 
          <TableCell padding="checkbox">
            <Checkbox
              color="primary"
              indeterminate={numSelected > 0 && numSelected < rowCount}
              checked={rowCount > 0 && numSelected === rowCount}
              onChange={onSelectAllClick}
              inputProps={{
                'aria-label': 'select all',
              }}
            />
          </TableCell>
        }
        {
          headerCells !== undefined && 
          headerCells.map((headCell) => (
          <TableCell
            key={headCell.id}
            // align={headCell.numeric ? 'right' : 'left'}
            align={'left'}
            padding={headCell.disablePadding ? 'none' : 'normal'}
            sortDirection={orderBy === headCell.id ? order : false}
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

EnhancedTableHead.propTypes = {
  headerCells: PropTypes.array.isRequired,
  numSelected: PropTypes.number.isRequired,
  onRequestSort: PropTypes.func.isRequired,
  onSelectAllClick: PropTypes.func.isRequired,
  order: PropTypes.oneOf(['asc', 'desc']).isRequired,
  orderBy: PropTypes.string.isRequired,
  rowCount: PropTypes.number.isRequired
};

const EnhancedTableToolbar = (props) => {
  const { numSelected, tableTitle, openNewRow, isEditable, deleteButtonHandle,
  actions, deletable, selectedIds } = props;

  return (
    <Toolbar
      sx={{
        pl: { sm: 2 },
        pr: { xs: 1, sm: 1 },
        ...(numSelected > 0 && {
          bgcolor: (theme) =>
            alpha(theme.palette.primary.main, theme.palette.action.activatedOpacity),
        }),
      }}
    >
      {numSelected > 0 ? (
        <Typography
          sx={{ flex: '1 1 100%' }}
          color="inherit"
          variant="subtitle1"
          component="div"
        >
          {numSelected} bản ghi được chọn
        </Typography>
      ) : (
        <Typography
          sx={{ flex: '1 1 100%' }}
          variant="h6"
          id="tableTitle"
          component="div"
        >
          {tableTitle}
        </Typography>
      )}

      {
        actions == undefined && isEditable &&
        <Tooltip title="Thêm mới">
          <IconButton onClick={openNewRow}> 
            <AddIcon />
          </IconButton>
        </Tooltip>
      }

      {
        isEditable ?
        <>
        {numSelected > 0 && deletable != false ? (
          <Tooltip title="Xóa">
            <IconButton onClick={deleteButtonHandle}>
              <DeleteIcon />
            </IconButton>
          </Tooltip>
        ) : (
          // <Tooltip title="Thêm mới">
          //   <IconButton onClick={openNewRow}> 
          //     <AddIcon />
          //   </IconButton>
          // </Tooltip>
          <div></div>
        )
        }
        </> : <div></div>
      }

      {
        actions != undefined && actions.length > 0 && 
        actions.map(action => {
          if (action.iconOnClickHandle == undefined) {
            return <Tooltip title={action.tooltip}>
                      {action.icon}
                    </Tooltip>
          } else {
            return <CommandBarButton 
                    onClick={() => action.iconOnClickHandle(selectedIds)}>
                       {action.tooltip}
                   </CommandBarButton>
          }
        })
      }
    </Toolbar>
  );
}

EnhancedTableToolbar.propTypes = {
  numSelected: PropTypes.number.isRequired,
  tableTitle: PropTypes.string,
  openNewRow: PropTypes.func,
  isEditable: PropTypes.bool,
  deleteButtonHandle: PropTypes.func,
  selectedIds: PropTypes.array
};

const StandardTable = ({ columns, data, title, options, editable, onRowClick, 
  rowKey, actions, deletable }) => {

  const [selected, setSelected] = React.useState([]);
  const [order, setOrder] = React.useState('asc');
  const [orderBy, setOrderBy] = React.useState('id'); // id ?
  const [page, setPage] = React.useState(0);
  const [rowsPerPage, setRowsPerPage] = React.useState(options?.pageSize != undefined ? options?.pageSize : 5);
  const [rows, setRows] = React.useState([]);
  const [isShowAddModal, setShowAddModal] = React.useState(false);
  const [originalRows, setOriginalRows] = React.useState([]);
  const [searchQuery, setSearchQuery] = React.useState(null);

  const headerCells = buildHeaderCells(columns);

  const isEditable = editable != null || editable != undefined 
    || actions != undefined;

  React.useEffect(() => {
    setRows(buildTableData(data, columns));
    setOriginalRows(buildTableData(data, columns));
    setSearchQuery(''); // work around for bug: not re-render table with rows after fetching data from server 
  }, [data]);

  const handleRequestSort = (event, property) => {
    const isAsc = orderBy === property && order === 'asc';
    setOrder(isAsc ? 'desc' : 'asc');
    setOrderBy(property);
  };

  const handleSelectAllClick = (event) => {
    if (event.target.checked) {
      const newSelected = rows.map((n) => n[rowKey]);
      setSelected(newSelected);
      return;
    }
    setSelected([]);
  };

  const handleClick = (event, row) => {
    // const selectedIndex = selected.indexOf(name);
    // let newSelected = [];

    // if (selectedIndex === -1) {
    //   newSelected = newSelected.concat(selected, name);
    // } else if (selectedIndex === 0) {
    //   newSelected = newSelected.concat(selected.slice(1));
    // } else if (selectedIndex === selected.length - 1) {
    //   newSelected = newSelected.concat(selected.slice(0, -1));
    // } else if (selectedIndex > 0) {
    //   newSelected = newSelected.concat(
    //     selected.slice(0, selectedIndex),
    //     selected.slice(selectedIndex + 1),
    //   );
    // }

    // setSelected(newSelected);
    // console.log("handleClick: Event => ", event);
    // console.log("handleClick: Row => ", row);
    if (typeof onRowClick === 'function') {
      onRowClick(event, row);
    }
  };

  const handleRowCheckBoxClick = (event, row) => {
    const name = row[rowKey];
    const selectedIndex = selected.indexOf(name);
    let newSelected = [];

    if (selectedIndex === -1) {
      newSelected = newSelected.concat(selected, name);
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
  }

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  const deleteButtonHandle = async () => {
    // delete data from data base
    await editable.onRowDelete(selected);
    // delete data from UI
    const newTableData = rows.filter(row => !selected.includes(row[rowKey]));
    setRows(newTableData);
  }

  const isSelected = (name) => selected.indexOf(name) !== -1;

  React.useEffect(() => {
    if (searchQuery == '' || searchQuery == null) {
      setRows(originalRows);
    } else {
      const normQuery = String(searchQuery).toLowerCase();
      const newRows = originalRows.filter(query => Object.entries(query).some(entry => String(entry[1]).toLowerCase().includes(normQuery)));
      setRows(newRows);
    }
  }, [searchQuery]);

  const emptyRows =
    page > 0 ? Math.max(0, (1 + page) * rowsPerPage - rows.length) : 0;
  
  return (
  <>
  {
    isEditable &&
    <Modal open={isShowAddModal}
      onClose={() => setShowAddModal(!isShowAddModal)}
      aria-labelledby="modal-modal-title" 
      aria-describedby="modal-modal-description"
    >
      <Box sx={{
        position: 'absolute',
        top: '50%',
        left: '50%',
        width: '75%',
        height: '50%',
        transform: 'translate(-50%, -50%)',
        bgcolor: 'background.paper',
        border: '2px solid #000',
        boxShadow: 24,
        p: 4,
      }}>
        <Toolbar>
          <Typography
            sx={{ flex: '1 1 100%' }}
            variant="h6"
            id="tableTitle"
            component="div"
          >
            Thêm mới bản ghi
          </Typography>
          <Tooltip title="Lưu">
            <IconButton onClick={() => {
                editable?.onRowAdd();
                // successNoti("Lưu thành công");
                setShowAddModal(false);
              }}>
              <SaveIcon />
            </IconButton>
          </Tooltip>
        </Toolbar>
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow>
                {
                  headerCells != undefined &&
                  headerCells.map((headCell) => 
                  <TableCell
                    key={headCell.id}
                    align={headCell.numeric ? 'right' : 'left'}
                    padding={headCell.disablePadding ? 'none' : 'normal'}
                  >
                    <TableSortLabel
                      active={orderBy === headCell.id}
                      direction={orderBy === headCell.id ? order : 'asc'}
                    >
                      {headCell.label}
                    </TableSortLabel>
                  </TableCell>)
                }
              </TableRow>
            </TableHead>
            
            <TableBody>
                <TableRow>
                  {
                    columns != undefined &&
                    columns.map(column => {
                      if (column.editComponent == undefined) {
                        return <TableCell>
                          <TextField />
                        </TableCell>
                      } else {
                        return <TableCell>{column.editComponent}</TableCell>;
                      }
                    })
                  }
                </TableRow>
            </TableBody>
          </Table>
        </TableContainer>
      </Box>
    </Modal>
  }

  <Box sx={{ width: '100%' }}>
    <Paper sx={{ width: '100%', mb: 2 }}>
      <EnhancedTableToolbar numSelected={selected.length} tableTitle={title}
        openNewRow={() => setShowAddModal(true)} isEditable={isEditable}
        deleteButtonHandle={deleteButtonHandle} actions={actions}
        deletable={deletable} 
        selectedIds={selected}
      />

      {
        options?.search == true &&
        <Paper>
          <SearchBar setSearchQuery={setSearchQuery} />
        </Paper>
      }

      <TableContainer>
          <Table
            sx={{ minWidth: 750 }}
            aria-labelledby="tableTitle"
            size={'medium'}
          >
            <EnhancedTableHead
              headerCells={headerCells}
              numSelected={selected.length}
              order={order}
              orderBy={orderBy}
              onSelectAllClick={handleSelectAllClick}
              onRequestSort={handleRequestSort}
              rowCount={rows.length}
              isEditable={isEditable}
              options={options}
            />

            <TableBody>
              {stableSort(rows, getComparator(order, orderBy))
                .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                .map((row, index) => {
                  const isItemSelected = isSelected(row[rowKey]);
                  const labelId = `enhanced-table-checkbox-${index}`;

                  return (
                    <TableRow
                      hover
                      role="checkbox"
                      aria-checked={isItemSelected}
                      tabIndex={-1}
                      key={row[rowKey]}
                      selected={isItemSelected}
                    >
                      {
                        isEditable && options.selection != false &&
                        <TableCell padding="checkbox">
                          <Checkbox
                            color="primary"
                            checked={isItemSelected}
                            inputProps={{
                              'aria-labelledby': labelId,
                            }}
                            onClick={(event) => handleRowCheckBoxClick(event, row)}
                          />
                        </TableCell>
                      }
                      {
                        headerCells != undefined &&
                        headerCells.map(cell => <TableCell 
                          onClick={(event) => handleClick(event, row)}>
                            {row[cell.id]}</TableCell>
                        )
                      }
                      {
                        columns != undefined &&
                        columns.map(column => {
                          if (column.buttonOnclickHandle != undefined) {
                            return <TableCell 
                              children={<Button variant="contained"
                                onClick={() => column.buttonOnclickHandle(row)}
                              >{column.buttonOnclickText != null ? column.buttonOnclickText : "Xử lý"}</Button>}
                            />
                          }
                        })
                      }
                    </TableRow>
                  );
                })}
              {emptyRows > 0 && (
                <TableRow
                  style={{
                    height: 53 * emptyRows,
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
          count={rows.length}
          rowsPerPage={rowsPerPage}
          page={page}
          onPageChange={handleChangePage}
          onRowsPerPageChange={handleChangeRowsPerPage}
        />
    </Paper>
  </Box>
  </>
  );
}

StandardTable.propTypes = {
  columns: PropTypes.array.isRequired, 
  data: PropTypes.array.isRequired, 
  title: PropTypes.string, 
  options: PropTypes.object, 
  editable: PropTypes.object, 
  onRowClick: PropTypes.func, 
  rowKey: PropTypes.string.isRequired,
  deletable: PropTypes.bool
};

export default StandardTable;