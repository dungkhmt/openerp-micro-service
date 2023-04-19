import React, {useState} from "react";
import {useDispatch, useSelector} from "react-redux";
import Alert from '@mui/material/Alert';
import {makeStyles} from "@material-ui/core/styles";
import {Button, Card, CardActions, CardContent, TextField, Typography} from "@material-ui/core/";
import {useHistory, useParams} from "react-router-dom";
import {axiosPost} from "../../../api";

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

function ResourceCreate(props) {
  const history = useHistory();
  const classes = useStyles();
  const dispatch = useDispatch();
  const token = useSelector((state) => state.auth.token);
  const [link,setLink] = useState(null);
  const [status,setStatus] = useState(null);
  const [description,setDescription] = useState(null);
  const [alert,setAlert] = useState(false);
  const [alertContent, setAlertContent] = useState('');
  const params = useParams();
  const columns = [
    { field: "link", title: "Link" },
    { field: "description", title: "Description" },
    { field: "status", title: "Status" },
  ];

  const [resource, setResource] = useState([]);
  // Functions.
  const createResource = () => {
    const data = JSON.stringify({link:link,description:description,statusId:status})
    axiosPost(token, `/domains/${params.id}/resource`,data)
      .then((res) => {
        console.log("create, resource ", res.data);
        if (res.data == true) {
          setAlertContent("Create susscessed");
          setAlert(true);
        }
      })
      .catch((error) => {
        setAlertContent("Create failed");
        setAlert(true);
      })
  };
  const handleSubmit = () => {
    createResource();
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
                  id="link"
                  label="Link"
                  placeholder="Nhập link"
                  value={link}
                  onChange={(event) => {
                    setLink(event.target.value);
                  }}
                />
                <TextField
                  required
                  id="description"
                  label="Description"
                  placeholder="Nhập mô tả"
                  value={description}
                  onChange={(event) => {
                    setDescription(event.target.value);
                  }}
                />
                <TextField
                  required
                  id="status"
                  label="Status"
                  placeholder="Nhập trạng thái"
                  value={status}
                  onChange={(event) => {
                    setStatus(event.target.value);
                  }}
                />
              </div>
              </form>
        </CardContent>
        {alert ? <Alert severity='error'>{alertContent}</Alert> : <></> }
        <CardActions>
          <Button
            variant="contained"
            color="primary"
            style={{ marginLeft: "45px" }}
            onClick={handleSubmit}
          >
            Lưu
          </Button>
          <Button variant="contained" onClick={() => history.push(`/edu/resources/${params.id}/resources`)}>
            Hủy
          </Button>
        </CardActions>
      </Card>
  );
}

export default ResourceCreate;
