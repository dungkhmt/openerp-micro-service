import React from "react";
import {Grid} from "@mui/material";
import Typography from "@mui/material/Typography";
import CodeBracket from '@heroicons/react/24/solid/CodeBracketIcon';
import CommandLine from '@heroicons/react/24/solid/CommandLineIcon';
import AcademicCap from '@heroicons/react/24/solid/AcademicCapIcon';
import Users from '@heroicons/react/24/solid/UsersIcon';
import InfoCard from "./dashboard/InfoCard";
import DailyProgramming from "./dashboard/DailyProgramming";
import DailyQuiz from "./dashboard/DailyQuiz";
import {motion} from 'framer-motion';
import {Box} from "@material-ui/core";
import TopicWordCloud from "./dashboard/TopicWordCloud";

export default function Home() {

  return <>
    {/*<HustContainerCard>*/}
    <Grid container>
      <Grid item xs={12} sx={{textAlign: "center"}}>
        <motion.div
          initial={{opacity: 0.1}}
          animate={{opacity: 1}}
          transition={{delay: 0.2, duration: 0.5}}
        >
          <Typography
            variant="h6"
            sx={{color: "#ae1d2c", fontSize: "24px", fontWeight: 800, textShadow: "2px 1px 2px #e3e3e3"}}
          >
            HUSTack
          </Typography>
          <Typography
            variant="body2"
          >
            Empower your programming journey and solve real-world problems
          </Typography>
        </motion.div>
      </Grid>
      <Grid container justifyContent="space-between" sx={{marginTop: "16px"}}>
        <Grid item xs={2.75}>
          <motion.div
            initial={{opacity: 0, scale: 0.8}}
            animate={{opacity: 1, scale: 1}}
            transition={{delay: 0.6, duration: 0.5}}
            children={
              <InfoCard
                icon={CodeBracket}
                iconColor="#0d2d80"
                mainTitle="1500+"
                subTitle="Coding Problems"
              />
            }
          />
        </Grid>
        <Grid item xs={2.75}>
          <motion.div
            initial={{opacity: 0, scale: 0.8}}
            animate={{opacity: 1, scale: 1}}
            transition={{delay: 0.9, duration: 0.5}}
            children={
              <InfoCard
                icon={AcademicCap}
                iconColor="#1976d2"
                mainTitle="450+"
                subTitle="Quiz Tests"
              />
            }
          />
        </Grid>
        <Grid item xs={2.75}>
          <motion.div
            initial={{opacity: 0, scale: 0.8}}
            animate={{opacity: 1, scale: 1}}
            transition={{delay: 1.2, duration: 0.5}}
            children={
              <InfoCard
                icon={Users}
                iconColor="#139529"
                mainTitle="10,000+"
                subTitle="Active Users"
              />
            }
          />
        </Grid>
        <Grid item xs={2.75}>
          <motion.div
            initial={{opacity: 0, scale: 0.8}}
            animate={{opacity: 1, scale: 1}}
            transition={{delay: 1.4, duration: 0.5}}
            children={
              <InfoCard
                icon={CommandLine}
                iconColor="#b5ba0d"
                mainTitle="1030,000+"
                subTitle="Code Submissions"
              />
            }
          />
        </Grid>
      </Grid>
      <Grid container justifyContent="space-between" sx={{marginTop: "20px"}}>
        <Grid item xs={7}>
          <motion.div
            initial={{opacity: 0, y: 20}}
            animate={{opacity: 1, y: 0}}
            transition={{delay: 1.8, duration: 0.8}}
            children={<Box>
              {/*<DailyQuiz/>*/}
              {/*<Box sx={{height: "24px"}}/>*/}
              <DailyProgramming/>
            </Box>}
          />
        </Grid>
        <Grid item xs={4.75}>
          <motion.div
            initial={{opacity: 0, y: 20}}
            animate={{opacity: 1, y: 0}}
            transition={{delay: 1.8, duration: 0.8}}
            children={<TopicWordCloud/>}
          />

        </Grid>
      </Grid>
    </Grid>

    {/*</HustContainerCard>*/}
  </>
}
