import * as React from 'react';
import { Button, Typography, Grid, Container } from '@mui/material';
import { ApplyJobPost } from 'views/jobpost/ApplyJobPost';
import { Card, CardContent, CardActions } from '@mui/material';
// const jobListing = {
//   title: 'Java Engineer (Middle-Senior, English)',
//   company: 'KBTG Vietnam',
//   salary: 10000,
//   location: 'Friendship Tower, 31 Le Duan, Ben Nghe Ward, District 1, Ho Chi Minh',
//   workType: 'Hybrid',
//   posted: '15 days ago',
//   skills: ['Java', 'MySQL', 'Spring'],
// };

function JobTitleCard({ jobListing, open, onClose, jobId, jobName, handleClick }) {
  return (
    <Container maxWidth="md">
      <Grid container spacing={2}>
        <Grid item xs={12}>
          <Typography variant="h4" gutterBottom style={{ fontWeight: 'bold' }}>
            {jobListing.jobPost?.title}
          </Typography>
          <Typography variant="subtitle1" gutterBottom>
            {jobListing.company?.companyName}
          </Typography>
        </Grid>
        <Grid item xs={12} sm={12}>
          <Typography variant="subtitle1" gutterBottom>
            salary: {jobListing.jobPost?.salary ? jobListing.jobPost?.salary : "thương lượng"}
          </Typography>
        </Grid>
        <Grid item xs={12} sm={12}>
        <CardActions>
          <Button variant="contained" color="secondary" fullWidth onClick={handleClick}>
            Apply Now
          </Button>
          <ApplyJobPost open={open} onClose={onClose} jobId={jobId} jobName={jobName} />
        </CardActions>
        </Grid>
        <Grid item xs={12}>
          <Typography variant="body1" gutterBottom>
            {jobListing.jobPost?.locations}
          </Typography>
          <Typography variant="body1" gutterBottom>
            {jobListing.company?.companyType}
          </Typography>
          <Typography variant="body1" gutterBottom>
            Posted: {jobListing.jobPost?.createdTime}
          </Typography>
        </Grid>
        <Grid item xs={12}>
          <Typography variant="body2" align="right">
            {jobListing.company?.companyName} {jobListing.jobPost?.locations}
          </Typography>
        </Grid>
      </Grid>
    </Container>
  );
}

export default JobTitleCard;