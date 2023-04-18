import * as React from 'react';
import {Box, Typography} from '@mui/material';

import {HISTORY_TAGS} from "../ultis/constant";

const HistoryStatus = ({ tag }) => {
    return (
        <Box display={'flex'} flexDirection={'row'}>
            <Typography variant={'body1'} sx={{mr: 1}}>
                đã
            </Typography>
            <Typography variant="body2" sx={{
                border: 1,
                borderRadius: "20px",
                borderColor: HISTORY_TAGS[tag].color,
                backgroundColor: HISTORY_TAGS[tag].color,
                px: 2,
                mr: 1,
                color: "#fff"
            }}>
                {HISTORY_TAGS[tag].action}
            </Typography>
            <Typography variant={'body1'}>
                nhiệm vụ
            </Typography>
        </Box>
    );
}

export default HistoryStatus;