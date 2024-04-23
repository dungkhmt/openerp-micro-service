
import {Card, CardActions, CardContent, Button, TextField, Typography,} from "@mui/material/";

import {useEffect, useState} from "react";
import {useHistory} from "react-router-dom";
import {request} from "../../api";

function ExamClassCreate(){
    const [name, setName] = useState();
    const [description, setDescription] = useState();
    const [executeDate, setExecuteDate] = useState();
    const history = useHistory();
    
    function handleSubmit(){
        let body = {
            name: name,
            description: description,
            executeDate: executeDate
          };
      
          request(
            "post",
            "/create-exam-class",
            (res) => {
              console.log("Create Exam Class = ", res.data);
              history.push("/exam-class/list");
            },
            {},
            body
          );
      
    }
    return(
        <>
        <TextField
                required
                id="name"
                label="Exam Class Name"
                placeholder="Exam Class Name"
                value={name}
                onChange={(event) => {
                    setName(event.target.value);
                }}
              />
              <TextField
                required
                id="description"
                label="Description"
                placeholder="Description"
                value={description}
                onChange={(event) => {
                    setDescription(event.target.value);
                }}
              /><TextField
              required
              id="execute_date"
              label="Date"
              placeholder="Date"
              value={executeDate}
              onChange={(event) => {
                  setExecuteDate(event.target.value);
              }}
            />
          <Button
            variant="contained"
            color="primary"
            style={{ marginLeft: "45px" }}
            onClick={handleSubmit}
          >
            Lưu
          </Button>
          <Button variant="contained" onClick={() => history.push("exam-class/list")}>
            Hủy
          </Button>
        </>
    );
}

export default ExamClassCreate;