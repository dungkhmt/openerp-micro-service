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

function SkillCard({skill}) {
  const classes = useStyles();

  return (
    <Card className={classes.card} style={{ width: "100%" }}>
      <CardContent className={classes.cardContent}>
        <Typography variant="h6" component="div" className={classes.title}>
        Skill name: {skill.schoolName}
        </Typography>
        <Typography variant="body2" className={classes.subTitle}>
        Cert link: <a href={skill.certLink}></a>
        </Typography>
        <Typography>
        Score: {skill.score}
        </Typography>
        <Typography>
        Grade: {education.grade}
        </Typography>        
        <Typography variant="body2" className={classes.subTitle}>
        Created At: {experience.createdTime}
        </Typography>
        <Button>
            <Typography>more details</Typography>
        </Button>
      </CardContent>
    </Card>
  );
}

export default SkillCard;