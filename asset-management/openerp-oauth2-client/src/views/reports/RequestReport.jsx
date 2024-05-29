import { Grid, Typography } from "@mui/material";
import InfoCard from "components/reports/InfoCard";
import { motion } from "framer-motion";
import CodeBracket from "@heroicons/react/24/solid/CodeBracketIcon";
import CommandLine from "@heroicons/react/24/solid/CommandLineIcon";
import AcademicCap from "@heroicons/react/24/solid/AcademicCapIcon";
import Users from "@heroicons/react/24/solid/UsersIcon";
import React from "react";
import BarsDataset from "components/reports/BarsDataset";
import PieActiveArc from "components/reports/PieActiveArc";
import BasicTable from "components/reports/BasicTable";

const RequestReport = () => {
  return (
    <div>
      <Grid container>
        <Grid item xs={12} sx={{ textAlign: "center" }}>
          <motion.div
            initial={{ opacity: 0.1 }}
            animate={{ opacity: 1 }}
            transition={{ delay: 0.2, duration: 0.5 }}
          >
            <Typography
              variant="h6"
              sx={{
                color: "#ae1d2c",
                fontSize: "24px",
                fontWeight: 800,
                textShadow: "2px 1px 2px #e3e3e3",
              }}
            >
              HUSTack
            </Typography>
            <Typography variant="body2">
              Emplower your programming journey and solve real-world problems
            </Typography>
          </motion.div>
        </Grid>
        <Grid
          container
          justifyContent={"space-between"}
          sx={{ marginTop: "16px" }}
        >
          <Grid item xs={2.75}>
            <motion.div
              initial={{ opacity: 0, scale: 0.8 }}
              animate={{ opacity: 1, scale: 1 }}
              transition={{ delay: 0.6, duration: 0.5 }}
              children={
                <InfoCard
                  icon={CodeBracket}
                  iconColor="#0d2d80"
                  mainTitle="100"
                  subTitle="Requests"
                />
              }
            />
          </Grid>
          <Grid item xs={2.75}>
            <motion.div
              initial={{ opacity: 0, scale: 0.8 }}
              animate={{ opacity: 1, scale: 1 }}
              transition={{ delay: 0.9, duration: 0.5 }}
              children={
                <InfoCard
                  icon={AcademicCap}
                  iconColor="#1976d2"
                  mainTitle="450+"
                  subTitle="Assets"
                />
              }
            />
          </Grid>
          <Grid item xs={2.75}>
            <motion.div
              initial={{ opacity: 0, scale: 0.8 }}
              animate={{ opacity: 1, scale: 1 }}
              transition={{ delay: 1.2, duration: 0.5 }}
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
              initial={{ opacity: 0, scale: 0.8 }}
              animate={{ opacity: 1, scale: 1 }}
              transition={{ delay: 1.4, duration: 0.5 }}
              children={
                <InfoCard
                  icon={CommandLine}
                  iconColor="#b5ba0d"
                  mainTitle="500,000+"
                  subTitle="Code Submissions"
                />
              }
            />
          </Grid>
        </Grid>
        <Grid
          container
          justifyContent={"space-between"}
          sx={{ marginTop: "30px" }}
        >
          <Grid item xs={5.5} sx={{marginLeft: "10px"}}>
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 1.8, duration: 0.8 }}
              children={<BarsDataset />}
            />
          </Grid>
          <Grid item xs={5.5}>
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 1.8, duration: 0.8 }}
              children={<PieActiveArc />}
            />
          </Grid>
        </Grid>
				<Grid
					container
					justifyContent={"space-between"}
					sx={{marginTop: "30px"}}
				>
					<Grid item xs={12}>
					<motion.div
						initial={{opacity: 0, y: 20}}
						animate={{opacity: 1, y: 0}}
						transition={{delay: 2.3, duration: 1.1}}
						children={<BasicTable/>}
					/>
					</Grid>
				</Grid>
      </Grid>
    </div>
  );
};

export default RequestReport;
