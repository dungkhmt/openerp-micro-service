import {
  Button,
  CardContent,
  CardHeader,
  FormControl,
  FormControlLabel,
  FormHelperText,
  FormLabel,
  Radio,
  RadioGroup
} from "@mui/material";
import Card from "@mui/material/Card";
import Typography from "@mui/material/Typography";
import React, {useEffect, useState} from "react";
import HustModal from "../common/HustModal";
import {quizzes} from "./quiz-list";

export default function DailyQuiz() {

  const [todayQuiz, setTodayQuiz] = useState(quizzes[0]);
  const [value, setValue] = useState(null);
  const [isError, setIsError] = useState(false);
  const [openResultModal, setOpenResultModal] = useState(false);
  const [isWrong, setIsWrong] = useState(false);

  useEffect(() => {
    const date = new Date().getDate();
    setTodayQuiz(quizzes[date]);
  }, []);

  const handleChange = (event) => {
    setValue(Number(event.target.value));
  };

  const handleSubmit = (e) => {
    if (value == null) {
      setIsError(true);
      return;
    }
    setIsError(false);

    if (todayQuiz.answer !== value) setIsWrong(true);
    else setIsWrong(false);

    setOpenResultModal(true);
  }

  return <Card elevation={5} sx={{borderRadius: "18px"}}>
    <CardHeader
      title={<Typography variant="h6" color="#139529">Daily Quiz</Typography>}
    />
    <CardContent sx={{paddingTop: 0}}>
      <FormControl error={isError}>
        <FormLabel id="mcq-quiz">
          <Typography sx={{fontSize: "15px", marginBottom: "8px", color: "#000000", opacity: 0.8}}>
            {todayQuiz.question}
          </Typography>
        </FormLabel>
        <RadioGroup
          aria-labelledby="mcq-quiz"
          value={value}
          onChange={handleChange}
        >
          {todayQuiz.options.map((option, idx) => <FormControlLabel
            value={idx + 1}
            control={<Radio size="small" sx={{padding: "4px"}}/>}
            label={<Typography sx={{fontSize: "14px"}}>{option}</Typography>}/>)}
        </RadioGroup>
        {/*{isError && <FormHelperText>* Please select an option</FormHelperText>}*/}
        {/*<Button sx={{marginTop: "12px", marginX: "auto"}} variant="outlined" color="success" onClick={handleSubmit}>*/}
        {/*  Check Answer*/}
        {/*</Button>*/}
      </FormControl>
    </CardContent>

    <HustModal
      title="Daily Quiz"
      open={openResultModal}
      onClose={() => setOpenResultModal(false)}
      isNotShowCloseButton
    >
      <FormControl>
        <FormLabel id="mcq-quiz">
          <Typography sx={{fontSize: "16px", marginBottom: "10px", color: "#000000", opacity: 0.8}}>
            {todayQuiz.question}
          </Typography>
        </FormLabel>
        <RadioGroup
          aria-labelledby="mcq-quiz"
          value={value}
          sx={{marginBottom: "4px"}}
        >
          {todayQuiz.options.map((option, idx) => <FormControlLabel
            value={idx + 1}
            control={<Radio size="small" sx={{padding: "6px"}}/>}
            label={<Typography sx={{fontSize: "15px"}}>{option}</Typography>}/>)}
        </RadioGroup>
        {isWrong && <>
          <Typography variant="subtitle1" color="error">
            {"Wrong. Correct Answer: " + todayQuiz.options[todayQuiz.answer - 1]}
          </Typography>
        </>}
        {!isWrong && <Typography variant="subtitle1" sx={{color: "green"}}>Correct Answer</Typography>}
        <Typography variant="body2" sx={{fontWeight: 600, marginY: "4px"}}>Explanation</Typography>
        <Typography variant="body2" sx={{textAlign: "justify"}}>{todayQuiz.explanation}</Typography>
      </FormControl>
    </HustModal>
  </Card>;
}