import * as React from 'react';
import {Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField} from '@mui/material';

const EditCommentModal = ({ open, handleClose, comment, setCommentCallBack, onUpdateCommentCallBack }) => {
    return (
        <>
            <Box minWidth={"500px"}>
                <Dialog open={open} onClose={handleClose} fullWidth={true}>
                    <DialogTitle>
                        Chỉnh sửa bình luận
                    </DialogTitle>
                    <DialogContent>
                        <TextField
                            fullWidth={true}
                            variant="filled"
                            value={comment}
                            onChange={(e) => setCommentCallBack(e.target.value)}
                        />
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={handleClose} variant="contained" color="success">Hủy</Button>
                        <Button onClick={() => {onUpdateCommentCallBack(); handleClose();}} variant="contained" color="primary">Cập nhật</Button>
                    </DialogActions>
                </Dialog>
            </Box>
        </>
    );
}

export default EditCommentModal;