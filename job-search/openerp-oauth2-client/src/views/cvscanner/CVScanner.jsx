import React, { useEffect, useState } from "react";
import axios from "axios";
import Backdrop from "@mui/material/Backdrop";
import CircularProgress from "@mui/material/CircularProgress";
import styled from "styled-components";
import { Grid, Paper, TextField } from '@mui/material';
import Avatar from '@mui/material/Avatar';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemAvatar from '@mui/material/ListItemAvatar';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemText from '@mui/material/ListItemText';
import DialogTitle from '@mui/material/DialogTitle';
import Dialog from '@mui/material/Dialog';
import PersonIcon from '@mui/icons-material/Person';
import AddIcon from '@mui/icons-material/Add';
import { blue } from '@mui/material/colors';
import { useHistory } from 'react-router-dom';

const Info = styled.div`
  display: flex;
  flex-direction: column;
  grid-template-columns: 1fr 1fr;
  justify-content: center;
  align-items: center;
  gap: 2rem;
  margin-top: 2rem;
  margin-left: 4rem;
  margin-right: 4rem;
`;
const Title = styled.h1`
  font-size: 2rem;
  font-weight: 800;
  grid-column: 1/3;
`;

const LaybelField = styled.label`
  position: absolute;
  left: 2rem;
  top: -0.75rem;
  background-color: white;
  font-size: ${(props) => (props.screenWidth < 1000 ? "0.9rem" : "1.1rem")};
`;
const InputField = styled.input`
  height: 3rem;
  font-size: 1.1rem;
  border-radius: 5px;
  padding: 0.3rem;
`;
const SelectField = styled.select`
  height: 3.8rem;
  font-size: 1.1rem;
  border-radius: 5px;
  padding: 0.3rem;
`;
const OptionField = styled.option`
  height: 3rem;
  font-size: 1.1rem;
  border-radius: 5px;
  padding: 0.3rem;
`;
const ButtonField = styled.button`
  width: 10%;
  grid-column: 1/3;
  background-color: #33b249;
  color: whitesmoke;
  padding: 0.4rem 0.8rem;
  font-weight: 600;
  font-size: 1.5rem;
  border: none;
  cursor: pointer;
  border-radius: 5px;
  height: 3rem;
`;
const GroupField = styled.div`
  display: flex;
  flex-direction: column;
  position: relative;
`;
const Suggest = styled.div`
  margin-top: 2rem;
  margin-left: 4rem;
  margin-right: 4rem;
`;
const SuggestInfo = styled.div`
  display: grid;
  grid-template-columns: 1fr;
  justify-content: center;
  align-items: center;
  gap: 2rem;
  margin-top: 2rem;
  margin-left: 4rem;
  margin-right: 4rem;
  margin-bottom: 2rem;
`;
const ButtonScore = styled.button`
  color: white;
  background: teal;
  height: 3rem;
  font-size: 1.1rem;
  width: 10rem;
  margin-right: 4rem;
`;
const Container = styled.div``;
const apiUrl = process.env.REACT_APP_CANDIDATE_FINDING_HOST;

function CVScanner() {
    const [isLoading, setIsLoading] = useState(false);
    const [isFetch, setIsFetch] = useState(false);
    const [data, setData] = useState([]);
    const [hide, setHide] = useState(false)
    const [inputs, setInputs] = useState({});

    const handleInputChange = (e) => {
        setInputs((prev) => {
            return { ...prev, [e.target.name]: e.target.value };
        });
    };

    const handleClick = (e) => {
        e.preventDefault()
        setHide(() => !hide)
    }

    const handleSubmit = async () => {
        try {
            const information = {
                requirements: inputs.requirements,
            };

            setIsLoading(true);
            const res = await axios.post(
                `${apiUrl}/api/find-candidate`,
                information,
                {
                    headers: {
                        "Content-Type": "application/json", // Set the content type if needed
                        "Access-Control-Allow-Origin": "*", // Your CORS header
                        "Access-Control-Allow-Headers": "X-Requested-With", // Your CORS header
                    },
                }
            );


            setIsLoading(false);
            setData(res.data);
            setIsFetch(true);
            setHide(true)
        } catch (e) {
            setIsLoading(false);

        }
    };
    return (
        <Container>
            <>
                <Info>
                    <Title>Enter your Job description and requirements here :</Title>

                    <GroupField>
                        <Grid container spacing={2} width={"1000px"}>
                            <Grid item xs={12}>
                                <TextField
                                    label="Requirements"
                                    id="requirements"
                                    name="requirements"
                                    onChange={handleInputChange}
                                    multiline rows={6}
                                    variant="outlined"
                                    fullWidth
                                >
                                </TextField>
                            </Grid>
                            
                        </Grid>
                    </GroupField>
                    <ButtonField onClick={handleSubmit}>Submit</ButtonField>
                </Info>
                <PopupDialog onClick={handleClick} cvs={data.topCV} open={hide}></PopupDialog>
            </>

            <Backdrop
                sx={{ color: "#ffff", backgroundColor: "rgba(0, 0, 0, 0.6)" }}
                open={isLoading}
            >
                <CircularProgress color="inherit" />
                <h1>.....Wait a few minutes ......</h1>
            </Backdrop>
        </Container>
    );
}


function PopupDialog({onClick, cvs, open}) {


    const handleListItemClick = (cv) => {
        // Navigate to the desired page
        window.open(cv, '_blank');
      };

    return (
        <Dialog onClose={onClick} open={open}>
        <DialogTitle>Suitable candidates</DialogTitle>
        <List sx={{ pt: 0 }}>
          {cvs?.map((cv, index) => (
            <ListItem disableGutters >
              <ListItemButton onClick={() => handleListItemClick(cv)}>
                <ListItemAvatar>
                  <Avatar sx={{ bgcolor: blue[100], color: blue[600] }}>
                    <PersonIcon />
                  </Avatar>
                </ListItemAvatar>
                <ListItemText primary={cv} />
              </ListItemButton>
            </ListItem>
          ))}
        </List>
      </Dialog>
    )
}

export default CVScanner