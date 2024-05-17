import * as React from 'react';
import { makeStyles } from '@mui/styles';
import { Card, CardContent, Typography, Button,CardActions, Grid } from '@mui/material';
import { useState, useEffect } from "react"
import { SkillDetail } from './userinfoDetails/SkillDetails';
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
        Skill name: {skill.skillName}
        </Typography>
        <Typography variant="body2" className={classes.subTitle}>
        Cert link: <a target='_blank' href={skill.certLink}>click here</a>
        </Typography>
        <Typography>
        Score: {skill.score}
        </Typography>       
        <Typography variant="body2" className={classes.subTitle}>
        Created At: {skill.createdTime}
        </Typography>
        <CardActions>
        <Grid display='flex' justifyContent={'center'} item xs={12}>
          <Button variant="contained" color="secondary"  onClick={handleClickOpen}>
            More details
          </Button>
        <SkillDetail open={open} onClose={handleClose} Skill={skill} />
        </Grid>
        </CardActions>
      </CardContent>
    </Card>
  );
}

export default SkillCard;