import * as React from 'react';
import {
  Container,
  Typography,
  Grid,
   Stack,
  Divider,
  ImageList,
  ImageListItem,
  Badge,
  Avatar,
} from '@mui/material';

// const company = {
//   name: 'KBTG Vietnam',
//   type: 'IT Product',
//   size: '151-300 employees',
//   country: 'Thailand',
//   workingDays: 'Monday - Friday',
//   overtimePolicy: 'No OT',
//   awards: '2024 VIETNAM BEST IT COMPANIES™',
//   logo: 'https://i.imgur.com/iOEb2Ql.png', // replace with actual logo URL
//   images: [
//     'https://i.imgur.com/u1tLSzY.jpg', // replace with first image URL
//     'https://i.imgur.com/7B7LqXE.jpg', // replace with second image URL
//     'https://i.imgur.com/yXzJQzN.jpg', // replace with third image URL
//   ],
// };

function CompanyProfile({company}) {
  return (
    <Container maxWidth="md">
      <Grid container spacing={2}>
        <Grid item xs={12}>
          <Stack direction="row" spacing={2} alignItems="center">
            <img alt={company.company?.name} src={company.company?.companyLogoLink} width={"200px"} height={"160px"} />
            <Typography variant="h5" gutterBottom>
              {company.company?.name}
            </Typography>

          </Stack>
        </Grid>
        <Grid item xs={12} md={8} >
          <Typography variant="subtitle1" gutterBottom item xs={12} style={{ borderBottom: '1px dashed rgba(0, 0, 0, 0.3)' }}>
              Company type:  {company.company?.companyType}
            </Typography>
          <Typography variant="body1" gutterBottom item xs={12} style={{ borderBottom: '1px dashed rgba(0, 0, 0, 0.3)' }}>
            Company Size: {company.company?.companySize} 
          </Typography>
          <Typography variant="body1" gutterBottom item xs={12} style={{ borderBottom: '1px dashed rgba(0, 0, 0, 0.3)' }}>
            Country: {company.company?.country}
          </Typography>
          <Typography variant="body1" gutterBottom item xs={12} style={{ borderBottom: '1px dashed rgba(0, 0, 0, 0.3)' }}>
            Working days: {company.company?.workingDays}
          </Typography>
          <Typography variant="body1" gutterBottom item xs={12} style={{ borderBottom: '1px dashed rgba(0, 0, 0, 0.3)' }}>
            Overtime policy: {company.company?.overtimePolicy}
          </Typography>
        </Grid>
      </Grid>
      <Divider variant="middle" />
      <Grid container spacing={2}>
        {/* Add more content sections here if needed */}
      </Grid>
    </Container>
  );
}

export default CompanyProfile;