import * as React from 'react';
import {
  Typography,
  Grid,
  Container,
  Divider,
  List,
  ListItem,
  ListItemText,
  ListItemSecondaryAction,
  IconButton,
} from '@mui/material';
import { Info as InfoIcon } from '@mui/icons-material';

// const jobDescription = {
//   title: 'Java Engineer (Middle-Senior, English)',
//   company: 'KBTG Vietnam',
//   overview:
//     'At KBTG, we build our own platforms as well as service external well-known clients like PTTOR, Facebook, LINE MAN, GRAB, and many more with modernized technology stack and an open platform structure. We dedicate to build new financial service experiences which aim to satisfy users the most and be part of their daily life.',
//   responsibilities: [
//     'Develop accurate and efficient programs',
//     'Maintain current knowledge of standard language, coding methods, and operations requirements',
//     'Thoroughly tests the operation of completed programs and linkage to other programs',
//     'Assists the system analyst to establish file requirements and processing specifications for automated portions of the system',
//     'Build software and deploy to UIS/SIT/UAT environment',
//     'Support defect/problem investigation',
//   ],
//   skills: ['Bachelor\'s Degree in Computer Engineer, Computer Science, IT or other related fields', 'Minimum 5-year experience in Java software development', 'Experience working with one or more from the followings: web application development, Unix environments, mobile application development, information retrieval, networking, developing large-scale software system, version control system, and/or security software development', 'Good communication in English'],
//   benefits: [
//     '13th month salary & Performance bonus',
//     '15 days Annual Leaves',
//     'Life Insurance, Health Insurance',
//   ],
// };

function JobDescription({jobDescription}) {

  jobDescription["benefits"] = [
        '13th month salary & Performance bonus',
        '15 days Annual Leaves',
        'Life Insurance, Health Insurance',
      ]


  return (
    <Container maxWidth="md">
      <Grid container spacing={2}>
        <Grid item xs={12}>
        <Typography variant="h6" gutterBottom>
            About us
          </Typography>
          <Typography variant="body1" gutterBottom>
            {jobDescription.company?.about}
          </Typography>
        </Grid>
        <Grid item xs={12}>
          <Typography variant="h6" gutterBottom>
            Responsibilities
          </Typography>
          <Typography variant="body1" gutterBottom>
            {jobDescription.jobPost?.description}
          </Typography>
        </Grid>
        <Grid item xs={12}>
          <Typography variant="h6" gutterBottom>
            Your Skills and Experience
          </Typography>
          <Typography variant="body1" gutterBottom>
            {jobDescription.jobPost?.requirements}
          </Typography>
        </Grid>
        <Grid item xs={12}>
          <Typography variant="h6" gutterBottom>
            Why You'll Love Working Here
          </Typography>
          <List>
            {jobDescription.benefits?.map((benefit) => (
              <ListItem key={benefit}>
                <ListItemText primary={benefit} />
              </ListItem>
            ))}
          </List>
        </Grid>
        <Grid item xs={12}>
          <Divider variant="middle" />
          <Typography variant="body2" align="right">
            {jobDescription.company?.companyName}
          </Typography>
        </Grid>
      </Grid>
    </Container>
  );
}

export default JobDescription;
