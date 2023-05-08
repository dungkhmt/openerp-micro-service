import {Card, CardContent, Divider, Typography} from "@mui/material";
import React from "react";

const HustContainerCard = (props) => {
  const {
    children,
    title,
    maxWidthPaper = 640,
    minWidthPaper,
    classRoot,
    ...remainProps
  } = props;

  return (
    <Card {...remainProps} className={`${classRoot}`}>
      {title &&
        <Typography
          fontWeight="600"
          variant="h5"
          component="div"
          sx={{margin: "12px 0 10px 18px"}}
          color="#00acc1"
        >
          {title}
        </Typography>}
      <Divider/>
      <CardContent sx={{padding: title ? "28px" : "0"}}>
        {children}
      </CardContent>

    </Card>
  );
};

export default React.memo(HustContainerCard);
