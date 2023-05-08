import * as React from 'react';
import {Typography} from '@mui/material';

import {TASK_STATUS_COLOR} from "../ultis/constant";

const StatusElement = ({statusId, value}) => {
    return (
        <Typography variant="body2" sx={{
            border: 1,
            borderRadius: "20px",
            borderColor: TASK_STATUS_COLOR[statusId],
            backgroundColor: TASK_STATUS_COLOR[statusId],
            px: 2,
            mr: 2,
            color: "#fff"
        }}>
            {value}
        </Typography>
    );
}

export default StatusElement;