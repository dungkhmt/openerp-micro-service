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

function EducationCard({education}) {
  const classes = useStyles();

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
        <Button>
            <Typography>more details</Typography>
        </Button>
      </CardContent>
    </Card>
  );
}

export default EducationCard;