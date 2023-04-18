import React, {useState} from "react";
import {Button, TextField} from "@mui/material";
import {request} from "../../api";

export default function ForgetPassword() {
  const [userLogin, setUserLogin] = useState("");
  const [msg, setMsg] = useState("");

  function handleChange(e) {
    setUserLogin(e.target.value);
  }

  function handleClick() {
    request(
      "get",
      "/public/user/resetpassword/" + userLogin,
      (res) => {
        console.log("new password sent to email, res = ", res);
        setMsg(res.data.message);
      },
      {}
    );
  }

  return (
    <div
      style={{
        display: "flex",
        justifyContent: "center",
      }}
    >
      <div
        style={{
          display: "flex",
          paddingTop: "50px",
          justifyContent: "center",
        }}
      >
        <div
          style={{
            display: "flex",
            paddingRight: "30px",

            justifyContent: "center",
          }}
        >
          <TextField autoFocus required label="Username" onChange={handleChange} variant="standard"/>
        </div>
        <div
          style={{
            display: "flex",
            justifyContent: "center",
          }}
        >
          <Button variant="contained" onClick={handleClick}>
            Reset password
          </Button>
        </div>
        <h2>{msg}</h2>
      </div>
    </div>
  );
}
