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
import { useState, useEffect } from "react"
import { request } from "../../src/api"

const jobDescription = {
    companyLogo: 'https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcRHi40jeiiRjreg9siBaoR6S3HsAcZen4-EG5zM4Tvbrfu0DDcV',
  };
  

function UserApplicantCard({applicant}) {
  const [anchorEl, setAnchorEl] = React.useState(null); // State for IconButton menu (v5.0.3)
  const [CV, setCV] = useState([])

  const handleMenuOpen = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
  };

  useEffect(() => {
    request("get", `/employee-cv/${applicant.cvId}`, (res) => {
        setCV(res.data)
    }).then();
}, [])

  return (
    <Card sx={{ maxWidth: 800 }}  style={{ width: "100%" }}>
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
        title={applicant.jobId?.title}
        subheader={
          <>
         location: {applicant.jobId?.locations}  <br></br> salary: {applicant.jobId?.salary ? applicant.jobId?.salary + " Vnđ" : "Thương lượng"}
          </>
        }
      />
      <CardContent>

        <Typography variant="body1">Applicant Name: {applicant.user?.firstName + " "+applicant.user?.lastName}</Typography>
        <Typography variant="body1">ApplicantCV: <a target='blank' href={CV?.employeeCV?.cvLink ? CV?.employeeCV?.cvLink + "?alt=media": "https://facebook.com"}>applicant CV</a></Typography>
        <Typography variant="body1">status: {applicant.status}</Typography>
        <Typography variant="body1">description : {applicant.jobId?.description}</Typography>

        <Divider sx={{ margin: '1rem 0' }} />
        <Typography variant="body2" color="text.secondary">
          requirements: {applicant.jobId?.requirements}
        </Typography>        
        <Typography variant="body2" color="text.secondary">
          posted at: {applicant?.createdTime} 
        </Typography>
      </CardContent>
    </Card>
  );
}

export default UserApplicantCard;