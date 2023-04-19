import React, {useState} from "react";
import {useDispatch, useSelector} from "react-redux";
import Alert from '@mui/material/Alert';
import IconButton from '@mui/material/IconButton';
import CloseIcon from '@mui/icons-material/Close';
import {makeStyles} from "@material-ui/core/styles";
import {useHistory, useParams} from "react-router-dom";
import {Button, Card, CardActions, CardContent, TextField, Typography} from "@material-ui/core/";
import {axiosPatch} from "../../../api";

const useStyles = makeStyles((theme) => ({
  root: {
    padding: theme.spacing(4),
    "& .MuiTextField-root": {
      margin: theme.spacing(1),
      width: "100%",
      minWidth: 120,
    },
  },
  formControl: {
    margin: theme.spacing(1),
    minWidth: 120,
    maxWidth: 300,
  },
}));

function ResourceDomainEdit(props) {
  const history = useHistory();
  const classes = useStyles();
  const dispatch = useDispatch();
  const token = useSelector((state) => state.auth.token);
  const [name, setName] = useState(null);
  const [alert,setAlert] = useState(false);
  const [alertContent, setAlertContent] = useState('');
  const [open,setOpen] = useState(true);
  const params = useParams();
  const columns = [
    { field: "name", title: "Name" },
  ];

  const [domain, setDomain] = useState([]);
  // Functions.
  const createDomain = () => {
    const data = JSON.stringify({name:name})
    axiosPatch(token, `/domain/${params.id}`,data)
      .then((res) => {
        console.log("crean, domain ", res.data);
        if (res.data != null) {
          setAlertContent("Edit susscessed");
          setAlert(true);
        }
      })
      .catch((error) => {
        setAlertContent("Edit failed");
        setAlert(true);
      })
  };
  const handleSubmit = () => {
    createDomain();
  }


  return (
    <Card>
        <CardContent>
          <Typography variant="h5" component="h2">
            Tạo nguồn tham khảo
          </Typography>
          <form className={classes.root} noValidate autoComplete="off">
            <div>
              <TextField
                required
                id="name"
                label="Name"
                value={name}
                fullWidth
                onChange={(event) => {
                  setName(event.target.value);
                }}
              >
              
                  {/* <MenuItem key={item} value={item}>
                    {item}
                  </MenuItem> */}
          
              </TextField>
              </div>
              </form>
        </CardContent>
        {alert ? <Alert 
                  severity='error'
                  action={
                  <IconButton
                    aria-label="close"
                    color="inherit"
                    size="small"
                    onClick={() => {
                      setOpen(false);
                    }}
                  >
                  <CloseIcon fontSize="inherit" />
                  </IconButton>
                 }
                 sx={{ mb: 2 }}
                 >
         
                  </Alert> : <></> }
        <CardActions>
          <Button
            variant="contained"
            color="primary"
            style={{ marginLeft: "45px" }}
            onClick={handleSubmit}
          >
            Lưu
          </Button>
          <Button variant="contained" onClick={() => history.push("")}>
            Hủy
          </Button>
        </CardActions>
      </Card>
  );
}

export default ResourceDomainEdit;
