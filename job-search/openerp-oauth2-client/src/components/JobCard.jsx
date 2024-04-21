import * as React from 'react';
import {
  Typography,
  Grid,
  Container,
  Divider,
  Card,
  CardContent,
  CardMedia,
  CardHeader,
  IconButton,
  Avatar,
} from '@mui/material';
import { Menu } from '@mui/material'; // Import Menu for IconButton action (v5.0.3)

const jobDescription = {
    companyLogo: 'https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcRHi40jeiiRjreg9siBaoR6S3HsAcZen4-EG5zM4Tvbrfu0DDcV',
  };
  

function JobCard( {job}) {
  const [anchorEl, setAnchorEl] = React.useState(null); // State for IconButton menu (v5.0.3)

  const handleMenuOpen = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
  };

  return (
    <Card sx={{ maxWidth: 800 }} >
      <CardHeader
        avatar={<Avatar alt={"https://facebook.com"} src={jobDescription.companyLogo} />}
        action={
          <IconButton aria-label="settings" onClick={handleMenuOpen}> {/* Handle menu opening (v5.0.3) */}
            <Menu // Menu component for action options (v5.0.3)
              id="job-card-menu"
              anchorEl={anchorEl}
              anchorOrigin={{
                vertical: 'top',
                horizontal: 'right',
              }}
              transformOrigin={{
                vertical: 'top',
                horizontal: 'right',
              }}
              open={Boolean(anchorEl)}
              onClose={handleMenuClose}
            >
              {/* Add menu options here (e.g., Share, Apply) */}
            </Menu>
            {/* <MoreVertIcon />  -- This icon is not available in MUI v5 by default */}
          </IconButton>
        }
        title={job?.title}
        subheader={
          <>
         location: {job?.locations}  <br></br> salary: {job.salary ? job.salary + " Vnđ" : "Thương lượng"}
          </>
        }
      />
      <CardContent>
        <Typography variant="body2" color="text.secondary">
          posted at: {job?.createdTime} 
        </Typography>
        <Typography variant="body1">{job?.description}</Typography>
        <Divider sx={{ margin: '1rem 0' }} />
        <Typography variant="body2" color="text.secondary">
          requirements: {job?.requirements}
        </Typography>
      </CardContent>
    </Card>
  );
}

export default JobCard;
