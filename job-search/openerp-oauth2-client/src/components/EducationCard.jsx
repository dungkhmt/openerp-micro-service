import * as React from 'react';
import { makeStyles } from '@mui/styles';
import { Card, CardContent, Typography, Button, CardActions, Grid } from '@mui/material';
import { EducatonDetail } from './userinfoDetails/EducationDetails';
import { useState, useEffect } from "react"

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

function EducationCard({education}) {
  const classes = useStyles();
  const [open, setOpen] = useState(false);

  const handleClickOpen = () => {
      setOpen(true);
  };

  const handleClose = () => {
      setOpen(false);
  };


  return (
    <Card className={classes.card} style={{ width: "100%" }}>
      <CardContent className={classes.cardContent}>
        <Typography variant="h6" component="div" className={classes.title}>
        School name: {education.schoolName}
        </Typography>
        <Typography variant="body2" className={classes.subTitle}>
        Major: {education.major}
        </Typography>
        <Typography>
        Description: {education.description}
        </Typography>
        <Typography>
        Grade: {education.grade}
        </Typography>        
        <Typography variant="body2" className={classes.subTitle}>
        From: {education.startingTime}
        </Typography>
        <Typography variant="body2" className={classes.subTitle}>
        To: {education.endingTime}
        </Typography>
        <CardActions>
        <Grid display='flex' justifyContent={'center'} item xs={12}>
          <Button variant="contained" color="secondary"  onClick={handleClickOpen}>
            More details
          </Button>
        <EducatonDetail open={open} onClose={handleClose} Education={education} />
        </Grid>
        </CardActions>
      </CardContent>
    </Card>
  );
}

export default EducationCard;