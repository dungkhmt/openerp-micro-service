import {Divider, Grid, MenuItem, TextField} from "@mui/material";
import React, {useState} from "react";
import "react-draft-wysiwyg/dist/react-draft-wysiwyg.css";
import {request} from "../../../api";
import {useTranslation} from "react-i18next";
import {errorNoti, successNoti} from "../../../utils/notification";
import {LoadingButton} from "@mui/lab";
import HustContainerCard from "../../common/HustContainerCard";

function CreateProblem() {
  const {t} = useTranslation(
    ["education/programmingcontest/problem", "common", "validation"]
  );

  const [course, setCourse] = useState("Data Structure & Algorithm");
  const [level, setLevel] = useState("easy");
  const defaultLevel = ["easy", "medium", "hard"];
  const [topic, setTopic] = useState("");
  const [description, setDescription] = useState("");
  const [creativeLevel, setCreativeLevel] = useState("medium");
  const defaultCreativeLevel = ["medium", "high"];

  const [result, setResult] = useState("");

  const [loading, setLoading] = useState(false);

  function handleSubmit() {

    let body = {
      course: course,
      topic: topic,
      level: level,
      description: description,
      creative: creativeLevel
    };


    setLoading(true);

    request(
      "post",
      "/problems/generate-statement",
      (res) => {
        setLoading(false);
        setResult(res.data);
        successNoti("Problem generated successfully. Check it out!", 2000);
      },
      {
        onError: () => {
          errorNoti(t("error", {ns: "common"}), 3000);
          setLoading(false);
        },
      },
      body
    );

  }

  return (
    <HustContainerCard title={"Problem Suggestion Tool"}>
      <Grid container spacing={2}>
        <Grid item xs={8}>
          <TextField
            fullWidth
            autoFocus={true}
            required
            id={"course"}
            label={"Course"}
            placeholder="Course"
            value={course}
            onChange={(event) => {
              setCourse(event.target.value);
            }}
          />
        </Grid>
        <Grid item xs={2}>
          <TextField
            fullWidth
            required
            select
            id="level"
            label={"Level"}
            value={level}
            onChange={(event) => {
              setLevel(event.target.value);
            }}
          >
            {defaultLevel.map((item) => (
              <MenuItem key={item} value={item}>
                {item}
              </MenuItem>
            ))}
          </TextField>
        </Grid>
        <Grid item xs={2}>
          <TextField
            fullWidth
            required
            select
            id="creativeLevel"
            label={"Creative"}
            value={creativeLevel}
            onChange={(event) => {
              setCreativeLevel(event.target.value);
            }}
          >
            {defaultCreativeLevel.map((item) => (
              <MenuItem key={item} value={item}>
                {item}
              </MenuItem>
            ))}
          </TextField>
        </Grid>
        <Grid item xs={12}>
          <TextField
            fullWidth
            id={"course"}
            label={"Topics"}
            placeholder="Topics related to the problem (e.g Stack, Recursion)"
            value={topic}
            onChange={(event) => {
              setTopic(event.target.value);
            }}
          />
        </Grid>

        <Grid item xs={12}>
          <TextField
            fullWidth
            multiline
            rows={3}
            id={"description"}
            label={"Description"}
            placeholder="If you want to be more detail on the problem, write here"
            value={description}
            onChange={(event) => {
              setDescription(event.target.value);
            }}
          />
        </Grid>
      </Grid>

      <Divider>
        <LoadingButton
          variant="contained"
          loading={loading}
          onClick={handleSubmit}
          sx={{marginTop: "12px", marginBottom: "12px"}}
        >
          Generate
        </LoadingButton>
      </Divider>

      <Grid item xs={12}>
        <TextField
          fullWidth
          multiline
          id={"result"}
          label={"Result"}
          value={result}
          onChange={(event) => {
            setResult(event.target.value);
          }}
        />
      </Grid>

    </HustContainerCard>
  );
}

export default CreateProblem;
