import * as React from 'react';
import { makeStyles } from '@mui/styles';
import { Card, CardContent, Typography, Button } from '@mui/material';
import JobCard from './JobCard';

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

function CVCard({cvData}) {
  const classes = useStyles();

  return (
    <Card className={classes.card} style={{ width: "100%" }}>
      <CardContent className={classes.cardContent}>
        <Typography variant="h6" component="div" className={classes.title}>
        Job CV Title: {cvData.employeeCV?.title}
        </Typography>
        <Typography variant="body2" className={classes.subTitle}>
            CV Description: {cvData.employeeCV?.description}
        </Typography>
        <Typography>
          Skill: {cvData.skills[0]?.skillName}...
        </Typography>
        <Typography variant="body2" className={classes.subTitle}>
          Education: {cvData.educations[0]?.schoolName}...
        </Typography>
        <Typography variant="body2" className={classes.subTitle}>
          Experiences: {cvData.experiences[0]?.companyName ? cvData.experiences[0]?.companyName : "project cá nhân"}...
        </Typography>
        <Typography variant="body2" className={classes.subTitle}>
          Created At: {cvData.employeeCV?.createdTime}
        </Typography>
        <Button>
            <Typography><a target='_blank' href={cvData.employeeCV?.cvLink + "?alt=media"}>more details</a></Typography>

        </Button>
      </CardContent>
    </Card>
  );
}

export default CVCard;