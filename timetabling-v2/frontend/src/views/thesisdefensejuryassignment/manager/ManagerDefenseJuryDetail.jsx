import React from 'react'
import DefenseJuryDetail from 'components/thesisdefensejury/DefenseJuryDetail';
import { useParams, useHistory } from "react-router-dom";
import { Box } from '@mui/material';
import PrimaryButton from 'components/button/PrimaryButton';
import useQuery from 'hooks/useQuery';
// Màn quản lí thông tin chi tiết hội đồng
export const ManagerDefenseJuryDetail = () => {
    const { id, juryId } = useParams();
    let query = useQuery();
    const history = useHistory();
    const isDefenseJuryAssigned = query.get("isassigned");
    return (
        <Box >
            {isDefenseJuryAssigned === "True" && (<Box sx={{ float: 'right' }}>
                <PrimaryButton onClick={() => history.push(`/thesis/thesis_defense_plan/${id}/defense_jury/${juryId}/reassign`)}>Chỉnh sửa phân công hội đồng</PrimaryButton>
            </Box>)}
            <DefenseJuryDetail />
        </Box>
    )
}
