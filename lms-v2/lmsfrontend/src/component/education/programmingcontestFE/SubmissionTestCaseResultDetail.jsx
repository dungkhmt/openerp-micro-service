import {makeStyles} from "@material-ui/core/styles";
import {Stack} from "@mui/material";
import Box from "@mui/material/Box";
import HustCopyCodeBlock from "component/common/HustCopyCodeBlock";
import {FacebookCircularProgress} from "component/common/progressBar/CustomizedCircularProgress";
import CustomizedDialogs from "component/dialog/CustomizedDialogs";

const useStyles = makeStyles((theme) => ({
  paper: (props) => ({maxWidth: props.showStderr ? 1341 : 900}),
  content: {
    minWidth: 640,
    maxHeight: 500,
    overflowY: "auto",
    marginBottom: 12,
    padding: "4px 12px",
  },
}));

export const SubmissionTestCaseResultDetail = ({
                                                 open,
                                                 handleClose,
                                                 loading,
                                                 data,
                                               }) => {
  const classes = useStyles({showStderr: data?.stderr});

  return (
    <CustomizedDialogs
      open={open}
      handleClose={handleClose}
      contentTopDivider
      classNames={{paper: classes.paper, content: classes.content}}
      content={
        loading ? (
          <Stack
            direction="row"
            justifyContent="center"
            alignItems="center"
            sx={{minHeight: 128}}
          >
            <FacebookCircularProgress/>
          </Stack>
        ) : (
          <Box sx={{minWidth: data?.stderr ? 1300 : 859}}>
            <Stack
              direction="row"
              justifyContent="space-between"
              spacing={2}
              sx={{mb: 2}}
            >
              <Box sx={{width: data?.stderr ? "calc(33% - 8px)" : "calc(50% - 8px)"}}>
                <HustCopyCodeBlock
                  title="Correct output"
                  text={data?.testCaseAnswer}
                />
              </Box>
              <Box sx={{width: data?.stderr ? "calc(33% - 8px)" : "calc(50% - 8px)"}}>
                <HustCopyCodeBlock
                  title="User output"
                  text={data?.participantAnswer}
                />
              </Box>
              {data?.stderr &&
                (<Box sx={{width: "calc(33% - 8px)"}}>
                  <HustCopyCodeBlock
                    title="Stderr"
                    text={data?.stderr}
                  />
                </Box>)}
            </Stack>
            <HustCopyCodeBlock title="Input" text={data?.testCase}/>
          </Box>
        )
      }
    />
  );
};
