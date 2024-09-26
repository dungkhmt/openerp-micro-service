import * as React from "react";
import {useEffect, useRef, useState} from "react";
import PropTypes from "prop-types";

Timer.propTypes = {
  id: PropTypes.string.isRequired,
  time: PropTypes.number.isRequired,
  timeOutHandler: PropTypes.func.isRequired
}

export function Timer(props){
  const id = props.id;
  const time = props.time;
  const timeOutHandler = props.timeOutHandler;

  const timeoutSubmisFunction = () =>{
    timeOutHandler();
  }

  const [timer, setTimer] = useState('00:00:00');
  const Ref = useRef(null);

  useEffect(() =>{

    let a = "startTime-"+time.toString()+"-"+id;

    if(localStorage.getItem(a) == null){
      console.log("set start time");
      let now = new Date();
      now.setMinutes(now.getMinutes()+time%60);
      now.setHours(now.getHours()+time/60);
      localStorage.setItem(a, now);
      clearTimer(now);
    }else{
      let now = new Date();
      if(localStorage.getItem(a) + time < now.getHours()*60 + now.getMinutes()){
        // localStorage.removeItem(a);
      }else{
        clearTimer(new Date(localStorage.getItem(a)));
      }
    }
  }, []);




  const clearTimer = (e) => {

    // If you adjust it you should also need to
    // adjust the Endtime formula we are about
    // to code next
    // If you try to remove this line the
    // updating of timer Variable will be
    // after 1000ms or 1sec
    if (Ref.current) clearInterval(Ref.current);
    const id = setInterval(() => {
      startTimer(e);
    }, 1000)
    console.log("done");

    Ref.current = id;

  }

  const startTimer = (e) => {
    // console.log("start timer ", e);
    let { total, hours, minutes, seconds } = getTimeRemaining(e);
    if (total >= 0) {

      // update the timer
      // check if less than 10 then we need to
      // add '0' at the begining of the variable
      let a = (hours > 9 ? hours : '0' + hours) + ':' +
        (minutes > 9 ? minutes : '0' + minutes) + ':' +
        (seconds > 9 ? seconds : '0' + seconds);
      setTimer(a);


      // setTest(a);
      // setTimer(
      //   (hours > 9 ? hours : '0' + hours) + ':' +
      //   (minutes > 9 ? minutes : '0' + minutes) + ':'
      //   + (seconds > 9 ? seconds : '0' + seconds)
      // );
    }else{
      //  submit
      timeoutSubmisFunction();
      clearInterval(Ref.current);
      // this.props.timoutSubmit();
    }
  }

  const getTimeRemaining = (e) => {
    const total = Date.parse(e) - Date.parse(new Date());
    const seconds = Math.floor((total / 1000) % 60);
    const minutes = Math.floor((total / (1000 * 60)) % 60);
    const hours = Math.floor((total / (1000 * 60 * 60)) % 24);
    return {
      total, hours, minutes, seconds
    };
  }

  return(
    <div>
      <b><span style={{color:"#FFFFFF"}}>{`${timer}`}</span></b>
    </div>
  )
}