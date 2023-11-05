import { Box, Grid, Typography } from "@mui/material";
import { boxChildComponent, TASK_STATUS_COLOR } from "../utils/constant";
import BoardItem from "./BoardItem";
import PropTypes from "prop-types";

const BoardContent = (props) => {
  const data = props.data;
  return (
    <>
      <Box height={"100vh"}>
        <Grid container columnSpacing={2}>
          {data.map((item) => (
            <Grid item={true} xs={3} key={item.statusItem.statusId}>
              <Box overflow={"auto"}>
                <Box mb={1} display={"flex"} alignItems={"center"}>
                  <Box
                    borderRadius={"50%"}
                    backgroundColor={
                      TASK_STATUS_COLOR[item.statusItem.statusId]
                    }
                    width={10}
                    height={10}
                    mr={1}
                  ></Box>
                  <Typography variant="body1">
                    {item.statusItem.description} ({item.total})
                  </Typography>
                </Box>
                <Box sx={boxChildComponent}>
                  {item.taskList.map((item) => (
                    <BoardItem key={item.id} task={item} />
                  ))}
                </Box>
              </Box>
            </Grid>
          ))}
        </Grid>
      </Box>
    </>
  );
};

BoardContent.propTypes = {
  data: PropTypes.array,
};

export default BoardContent;
