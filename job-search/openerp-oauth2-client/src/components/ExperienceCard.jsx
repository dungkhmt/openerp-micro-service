import * as React from 'react';
import { makeStyles } from '@mui/styles';
import { Card, CardContent, Typography, Button, CardActions, Grid } from '@mui/material';
import { ExperienceDetail } from './userinfoDetails/ExperienceDetals';
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

function ExperienceCard({experience}) {
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
        Company name: {experience.companyName}
        </Typography>
        <Typography variant="body2" className={classes.subTitle}>
        Working position: {experience.workingPosition}
        </Typography>
        <Typography>
        Responsibility: {experience.responsibility}
        </Typography>
        <Typography variant="body2" className={classes.subTitle}>
        From: {experience.createdTime}
        </Typography>
        <Typography variant="body2" className={classes.subTitle}>
        To: {experience.endingTime}
        </Typography>
        <CardActions>
        <Grid display='flex' justifyContent={'center'} item xs={12}>
          <Button variant="contained" color="secondary"  onClick={handleClickOpen}>
            More details
          </Button>
        <ExperienceDetail open={open} onClose={handleClose} Experience={experience} />
        </Grid>
        </CardActions>
      </CardContent>
    </Card>
  );
}

export default ExperienceCard;