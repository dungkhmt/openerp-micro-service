import React from 'react'
import DefenseJuryDetail from 'components/thesisdefensejury/DefenseJuryDetail';
import { useParams, useHistory } from "react-router-dom";
import { Box } from '@mui/material';
import PrimaryButton from 'components/button/PrimaryButton';
export const ManagerDefenseJuryDetail = () => {
    const { id, juryId } = useParams();
    const history = useHistory();
    return (
        <Box >
            <Box sx={{ float: 'right' }}>
                <PrimaryButton onClick={() => history.push(`/thesis/thesis_defense_plan/${id}/defense_jury/${juryId}/reassign`)}>Chỉnh sửa phân công hội đồng</PrimaryButton>
            </Box>
            <DefenseJuryDetail />
        </Box>
    )
}
