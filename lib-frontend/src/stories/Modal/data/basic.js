import React, {useState} from "react";
import {MenuItem, TextField} from "@mui/material";

export const Body = () => {
    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [gender, setGender] = useState("UNKNOWN");

    return <>
        <TextField
            fullWidth
            required
            label={"Enter your name"}
            value={name}
            onChange={(event) => {
                setName(event.target.value);
            }}
        />
        <TextField
            fullWidth
            label={"Email"}
            placeholder={"123@abc.xyz"}
            value={email}
            onChange={(event) => {
                setEmail(event.target.value);
            }}
            sx={{marginTop: "16px"}}
        />
        <TextField
            fullWidth
            autoFocus
            select
            label="Gender"
            onChange={(event) => {
                setGender(event.target.value);
            }}
            value={gender}
            sx={{marginTop: "32px"}}
        >
            <MenuItem value={"MALE"}>
                MALE
            </MenuItem><MenuItem value={"FEMALE"}>
            FEMALE
        </MenuItem><MenuItem value={"UNKNOWN"}>
            UNKNOWN
        </MenuItem>
        </TextField>
    </>
}