import {Box, Card, CardContent, Divider, Typography} from "@mui/material";
import React from "react";

const HustContainerCard = (props) => {
  const {
    children,
    title,
    action,
    maxWidthPaper = 640,
    minWidthPaper,
    classRoot,
    ...remainProps
  } = props;

  return (
    <Card {...remainProps} className={`${classRoot}`}>
      {title &&
        <Box
          sx={{display: "flex", flexDirection: "row", justifyContent: "space-between", margin: "14px 28px 12px 28px"}}>
          <Typography
            fontWeight="600"
            variant="h5"
            color="#00acc1"
          >
            {title}
          </Typography>
          <Box>
            {action}
          </Box>
        </Box>
      }

      <Divider/>
      <CardContent sx={{padding: title ? "28px" : "0"}}>
        {children}
      </CardContent>

    </Card>
  );
};

export default React.memo(HustContainerCard);
