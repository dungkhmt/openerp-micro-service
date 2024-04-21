import * as React from 'react';
import { makeStyles } from '@mui/styles';
import { Card, CardContent, Typography, Button } from '@mui/material';

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

function ApplicantCard() {
  const classes = useStyles();

  return (
    <Card className={classes.card} style={{ width: "100%" }}>
      <CardContent className={classes.cardContent}>
        <Typography variant="h6" component="div" className={classes.title}>
          Applicant name: LE HAI THANH
        </Typography>
        <Typography variant="body2" className={classes.subTitle}>
          <Button variant="text" href="#">
            View Profile
          </Button>
        </Typography>
        <Typography>
          status: shortListed
        </Typography>
        <Typography variant="body2" className={classes.subTitle}>
          Applicant CV: <a href="#">Click here to download</a>
        </Typography>
        <Typography variant="body2" className={classes.subTitle}>
          Applicant message:
        </Typography>
        <Typography variant="body2" className={classes.subTitle}>
          Created At: 19:12 06/07/2023
        </Typography>
      </CardContent>
    </Card>
  );
}

export default ApplicantCard;