import * as React from 'react';
import { makeStyles } from '@mui/styles';
import { Card, CardContent, Typography, Button, CardActions } from '@mui/material';
import { useState, useEffect } from "react"
import { request } from "../../src/api"
import Swal from "sweetalert2";
import './styles.css';

const useStyles = makeStyles((theme) => ({
  card: {
    width: '300px',
  },
  cardContent: {
    paddingBottom: '0px',
  },
  title: {
    fontSize: '16px',
    fontWeight: 'bold',
  },
  subTitle: {
    fontSize: '14px',
    color: theme.palette.text.secondary,
  },
  button: {
    marginTop: '10px',
    fontSize: '12px',
  },
}));

function ApplicantCard({ applicant, index, id }) {
  const classes = useStyles();
  // let id = 4
  const [allCV, setAllCV] = useState([])
  const [user, setUser] = useState({})
  const [CV, setCV] = useState([])
  const [cvApplication, setCVApplication] = useState([])
  useEffect(() => {
    request("get", "/user/get-user-data", (res) => {
      setUser(res.data)
    }).then();
  }, [])
  useEffect(() => {
    request("get", `/cv-application/${id}`, (res) => {
      setCVApplication(res.data)
    }).then();
  }, [])

  useEffect(() => {
    request("get", "/employee-cv", (res) => {
      setAllCV(res.data)
    }).then();
  }, [])

  useEffect(() => {
    request("get", `/employee-cv/${applicant.cvId}`, (res) => {
        setCV(res.data)
    }).then();
}, [])

  const handleSubmit = async (status, index) => {
    let cv = cvApplication[index]
    console.log(cv)
    let submitToServerForm = {
      ...cv,
      "status": status,

    };
    console.log(submitToServerForm)

    Swal.fire({
      title: 'Uploading...',
      text: 'Please wait while your request is being uploaded.',
      allowOutsideClick: false,
      didOpen: () => {
        Swal.showLoading();
      }
    });
    try {
      const res = await request("put", `/cv-application/user/${user.id}/${id}`, null, null, submitToServerForm)
      // Show a success Swal if the form is submitted
      Swal.fire({
        title: 'Submitted!',
        text: 'Your request has been submitted.',
        icon: 'success',
        confirmButtonText: 'OK'
      });
    } catch (error) {
      console.error(error);
      // Show an error Swal if there's an issue with the upload or form submission
      Swal.fire({
        title: 'Error!',
        text: 'There was an issue with the upload or submission.',
        icon: 'error',
        confirmButtonText: 'OK'
      });
    }


}
return (
  <Card className={classes.card} style={{ width: "100%" }}>
    <CardContent className={classes.cardContent}>
      <Typography variant="h4">
        Job name: {applicant.jobId.title}
      </Typography>
      <Typography variant="h6" component="div" className={classes.title}>
        Candidate name: {applicant.user.id}
      </Typography>
      <Typography variant="body2" className={classes.subTitle}>
        <Button variant="text" href="#">
          View Profile
        </Button>
      </Typography>
      <Typography>
        status: {applicant.status}
      </Typography>
      <Typography variant="body2" className={classes.subTitle}>
        Applicant CV: <a variant="text" href={CV?.employeeCV?.cvLink + "?alt=media"} target='_blank'>Click here to View</a>
      </Typography>
      <Typography variant="body2" className={classes.subTitle}>
        Applicant message:
      </Typography>
      <Typography variant="body2" className={classes.subTitle}>
        Created At: {applicant.createdTime}
      </Typography>
      <CardActions>
        <Button size="small" onClick={() => handleSubmit('reject', index)} name="reject" value="reject" cv={applicant}>Reject CV</Button>
        <Button size="small" onClick={() => handleSubmit('accept', index)} name="accept" value="accept" cv={applicant}>Accept CV</Button>
      </CardActions>
    </CardContent>
  </Card>
);
}

export default ApplicantCard;