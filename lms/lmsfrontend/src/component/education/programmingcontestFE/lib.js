import {TableCell} from "@material-ui/core";
import {tableCellClasses} from "@mui/material/TableCell";
import TableRow from "@material-ui/core/TableRow";
import {alpha, styled} from '@mui/material/styles';
import InputBase from '@mui/material/InputBase';
import {makeStyles} from "@material-ui/core/styles";

export function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

export function getColorLevel(level) {
  // const colors = ['red', 'yellow', 'green']
  switch (level) {
    case 'easy':
      return 'green';
    case 'medium':
      return 'orange';
    case 'hard':
      return 'red';
    default:
      return 'blue';
  }
}

export function getColorRegisterStatus(status) {
  switch (status) {
    case 'SUCCESSFUL':
      return 'green';
    case 'PENDING':
      return 'yellow';
    case 'FAILED':
      return 'red';
    case 'NOT REGISTER':
      return 'purple';
    default:
      return 'blue';
  }
}

export const StyledTableCell = styled(TableCell)(({theme}) => ({
  [`&.${tableCellClasses.head}`]: {
    backgroundColor: theme.palette.common.black,
    color: theme.palette.common.white,
  },
  [`&.${tableCellClasses.body}`]: {
    fontSize: 14,
  },
}));

export const StyledTableRow = styled(TableRow)(({theme}) => ({
  '&:nth-of-type(odd)': {
    backgroundColor: theme.palette.action.hover,
  },
  // hide last border
  '&:last-child td, &:last-child th': {
    border: 0,
  },
}));

export const getStatusColor = (status) => {
  switch (status) {
    case 'Accept':
      return 'green';
    default:
      return 'red';
  }
}


export const Search = styled('div')(({theme}) => ({
  position: 'relative',
  borderRadius: theme.shape.borderRadius,
  backgroundColor: alpha(theme.palette.common.white, 0.15),
  '&:hover': {
    backgroundColor: alpha(theme.palette.common.white, 0.25),
  },
  marginRight: theme.spacing(2),
  marginLeft: 0,
  width: '100%',
  [theme.breakpoints.up('sm')]: {
    marginLeft: theme.spacing(3),
    width: 'auto',
  },
}));

export const SearchIconWrapper = styled('div')(({theme}) => ({
  padding: theme.spacing(0, 2),
  height: '100%',
  position: 'absolute',
  pointerEvents: 'none',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
}));

export const StyledInputBase = styled(InputBase)(({theme}) => ({
  color: 'inherit',
  '& .MuiInputBase-input': {
    padding: theme.spacing(1, 1, 1, 0),
    // vertical padding + font size from searchIcon
    paddingLeft: `calc(1em + ${theme.spacing(4)})`,
    transition: theme.transitions.create('width'),
    width: '100%',
    [theme.breakpoints.up('md')]: {
      width: '20ch',
    },
  },
}));

export const styleBase = makeStyles((theme) => (
  {
    searchStyle: {
      color: 'inherit',
      '& .MuiInputBase-input': {
        padding: theme.spacing(1, 1, 1, 0),
        // vertical padding + font size from searchIcon
        paddingLeft: `calc(1em + ${theme.spacing(4)})`,
        transition: theme.transitions.create('width'),
        width: '100%',
        [theme.breakpoints.up('md')]: {
          width: '20ch',
        },
      },
    }
  }
))

