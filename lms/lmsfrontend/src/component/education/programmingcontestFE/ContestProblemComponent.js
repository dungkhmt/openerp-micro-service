import * as React from "react";
import {useEffect, useState} from "react";
import SplitPane from "react-split-pane";
import {ScrollBox} from "react-scroll-box";
import Typography from "@material-ui/core/Typography";
import {Button, Divider, MenuItem, TextField} from "@material-ui/core";
import {Markup} from "interweave";
import {request} from "../../../api";
import PropTypes from "prop-types";

ContestProblemComponent.propTypes ={
  problemName: PropTypes.string.isRequired,
  index: PropTypes.number.isRequired,
  problemDescription: PropTypes.string.isRequired,
  problemId: PropTypes.string.isRequired,
  contestId: PropTypes.string.isRequired,
  submitted: PropTypes.array.isRequired,
}

export function ContestProblemComponent(props){
  const {problemName, index, problemDescription, problemId, contestId, submitted} = props
  const [language, setLanguage] = useState("CPP");
  const computerLanguageListDefault = ["CPP", "GOLANG", "JAVA", "PYTHON3"];
  const [source, setSource] = useState("");
  const [screenHeight, setScreenHeight] = useState((window.innerHeight-180) + "px");
  const [showConsole, setShowConsole] = useState(false);
  const [timeLimit, setTimeLimit] = useState(false);
  const [compileError, setCompileError] = useState(false);
  const [accept, setAccept] = useState(false);
  const [output, setOutput] = useState("");
  const [expected, setExpected] = useState();
  const [run, setRun] = useState(false);
  const [input, setInput] = useState("");
  const [testCaseResult, setTestCaseResult] = useState();
  const [runTestCaseLoad, setRunTestCaseLoad] = useState(false);
  const [runTestCaseShow, setRunTestCaseShow] = useState(false);
  const [runCodeLoading, setRunCodeLoading] = useState(false);
  const [consoleTabIndex, setConsoleTabIndex] = useState(0);
  const [valueTab1, setValueTab1] =  useState(0);

  useEffect(() =>{
    let idSource = contestId+"-"+problemId+"-source";
    let tmpSource = localStorage.getItem(idSource);
    let idLanguage = contestId+"-"+problemId+"-language";
    let tmpLanguage = localStorage.getItem(idLanguage);
    if(tmpSource != null ){
      setSource(tmpSource)
    }else{
      setSource("")
      localStorage.setItem(idSource, "");
    }
    if(tmpLanguage != null){
      setLanguage(tmpLanguage);
    }else{
      setLanguage("CPP");
      localStorage.setItem(idLanguage, "CPP");
    }
  });

  const onInputChange = (input) =>{
    setInput(input);
  }
  const onChangeConsoleTabIndex = (value)=>{
    setConsoleTabIndex(value)
  }

  const handleScroll = () =>{
    if(showConsole){
      setScreenHeight((window.innerHeight-180) + "px");
      setShowConsole(false);
    }else{
      setScreenHeight((window.innerHeight-455) + "px");
      setShowConsole(true);
    }
  }
  return(
    <div>
      <SplitPane split="vertical"  primary={"second"} maxSize={"200px"}  >
        <div>
          <ScrollBox style={{width: '100%', overflow:"auto", height:(window.innerHeight-150) + "px"}}>
            <Typography variant={"h5"}><b>{index+1}. {problemName}</b></Typography>
            <Divider />
            <Markup content={problemDescription} />
          </ScrollBox>
        </div>

        <div>
          <TextField
            style={{width:0.075*window.innerWidth, marginLeft:20}}
            variant={"outlined"}
            size={"small"}
            autoFocus
            value={language}
            select
            id="computerLanguage"
            onChange={(event) => {

              setLanguage(event.target.value);
              localStorage.setItem(contestId+"-"+problemId+"-language", event.target.value);
            }}
          >
            {computerLanguageListDefault.map((item) => (
              <MenuItem key={item} value={item}>
                {item}
              </MenuItem>
            ))}
          </TextField>

          <Button
            variant="contained"
            color="light"
            // style={{marginLeft:"90px"}}
            // onClick={handleRunCode(problem.problemId)}
            onClick={() =>{
              console.log("problemId", problemId);
              setRunCodeLoading(true);
              setConsoleTabIndex(1);
              setShowConsole(true);
              setScreenHeight((window.innerHeight-455) + "px");

              let body =  {
                sourceCode: source,
                computerLanguage: language,
                input: input
              }
              request(
                "post",
                 "/problem-detail-run-code/" + problemId,
                (res) => {
                  setRun(true);
                  setRunCodeLoading(false);
                  if (res.data.status == "Time Limit Exceeded") {
                    setTimeLimit(true);
                    setCompileError(false);
                    setAccept(false);
                  } else if (res.data.status == "Compile Error") {
                    setTimeLimit(false);
                    setCompileError(true);
                    console.log("111");
                  } else if (res.data.status == "Accept") {
                    setAccept(true);
                    setTimeLimit(false);
                    setCompileError(false);
                  } else {
                    setAccept(false);
                    setTimeLimit(false);
                    setCompileError(false);
                  }
                  setOutput(res.data.output);
                  setExpected(res.data.expected);
                },
                {},
                body
              ).then();
            }}
            // style={{position}}
            style={{marginLeft:"20px"}}
          >
            Run Code
          </Button>

          <Button
            variant="contained"
            color="light"
            onClick={() =>{
              setScreenHeight((window.innerHeight-455) + "px");
              setRunTestCaseLoad(true);
              setConsoleTabIndex(2);
              setShowConsole(true);
              setValueTab1(1);

              let body ={
                source: source,
                language:language,
                contestId: contestId,
                problemId: problemId
              };
              request(
                "post",
                "/contest-submit-problem",
                (res) =>{
                  setTestCaseResult(res.data);
                  console.log("run all test case");
                  console.log("res ", res.data);

                },
                {},
                body
              ).then(
                ()=>{
                  setRunTestCaseLoad(false);
                  setRunTestCaseShow(true);
                  submitted[index] = true;
                }
              );

            }}
            // style={{position}}
            style={{marginLeft:"20px"}}
          >
            SUBMIT
          </Button>
        </div>
      </SplitPane>
    </div>

  );
}