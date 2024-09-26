import React, { useState } from 'react';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';

const LocationForm = () => {
    const [data, setData] = useState({
        name: "",
        description: "",
        email: "",
        phone: "",
        url: "",
        address: ""
    });

    const handleInputChange = (e) => {
        e.preventDefault();
        setData({...data, [e.target.name]: e.target.value});
    }

    const handleSubmit = (e) => {
        e.preventDefault();
        console.log("data", data);
    };

    return (
        <>
        <div>CREATE LOCATION</div>
        <hr/>
        <form onSubmit={handleSubmit}>
            <TextField
                label="Name"
                variant="outlined"
                fullWidth
                margin="normal"
                isRequired
                name='name'
                placeholder='Location name'
                onChange={handleInputChange}
            />
            <TextField
                label="Description"
                variant="outlined"
                fullWidth
                margin="normal"
                name='description'
                placeholder='Location description'
                onChange={handleInputChange}
            />
            <TextField
                label="Email"
                variant="outlined"
                fullWidth
                isRequired
                margin="normal"
                name='email'
                placeholder='Location email'
                onChange={handleInputChange}
            />
            <div style={{display: "flex", gap: "20px"}}>
                <TextField
                    label="Phone"
                    variant="outlined"
                    fullWidth
                    margin="normal"
                    name='phone'
                    placeholder='Location phone'
                    onChange={handleInputChange}
                />
                <TextField
                    label="URL"
                    variant="outlined"
                    fullWidth
                    margin="normal"
                    name='url'
                    placeholder='Location url'
                    onChange={handleInputChange}
                />
            </div>
            <TextField
                label="Address"
                variant="outlined"
                fullWidth
                margin="normal"
                name='address'
                placeholder='Location address'
                onChange={handleInputChange}
            />            
            <div style={{display: "flex", justifyContent: "space-between", marginTop: "20px"}}>
                <Button
                    variant="outlined"
                    color="primary"
                    type="cancel"
                >
                    Cancel
                </Button>
                <Button
                    variant="contained"
                    color="primary"
                    type="submit"
                >
                    Submit
                </Button>
            </div>
        </form>
        </>
    )
}

export default LocationForm