import * as React from 'react';
import {Avatar, Box, Typography} from '@mui/material';
import {Link} from 'react-router-dom';
import {boxChildComponent} from '../ultis/constant';
import BasicModal from '../modal/BasicModal';

const MemberModal = ({ open, handleClose, members }) => {
    return (
        <>
            <BasicModal open={open} handleClose={handleClose}>
                <Box>
                    <Box>
                        <Typography>
                            Quản lý thành viên
                        </Typography>
                    </Box>
                    {members.map(member => (
                        <Box sx={boxChildComponent} key={member.partyId} mb={3}>
                            <Link to={`/${member.partyId}`} style={{ textDecoration: 'none', color: '#000' }}>
                                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                    <Avatar sx={{ mr: 2, width: 56, height: 56 }}>{member.fullName.charAt(0).toUpperCase()}</Avatar>
                                    <Typography variant="h5" component="h5">
                                        {member.fullName}
                                    </Typography>
                                </Box>
                            </Link>
                        </Box>
                    ))}
                </Box>
            </BasicModal>
        </>
    );
}

export default MemberModal;