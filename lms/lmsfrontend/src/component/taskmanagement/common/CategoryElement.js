import * as React from 'react';
import {Typography} from '@mui/material';

import {TASK_CATEGORY_COLOR} from "../ultis/constant";

const CategoryElement = ({categoryId, value}) => {
    return (
        <Typography variant="body2" sx={{
            border: 1,
            borderRadius: "20px",
            borderColor: TASK_CATEGORY_COLOR[categoryId],
            backgroundColor: TASK_CATEGORY_COLOR[categoryId],
            px: 2,
            mr: 2,
            color: "#fff"
        }}>
            {value}
        </Typography>
    );
}

export default CategoryElement;