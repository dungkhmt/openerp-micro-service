import * as React from 'react';
import {useState} from 'react';
import {Box, Typography} from '@mui/material';
import {boxChildComponent} from '../ultis/constant';
import {request} from "../../../api";
import {successNoti} from "utils/notification";
import CommentItem from './CommentItem';
import EditCommentModal from './EditCommentModal';

const CommentSection = ({ projectId, listComment, loadComments, setLoadCommentsCallBack }) => {
    const [comment, setComment] = useState("");
    const [commentId, setCommentId] = useState("");
    const [openEditCommentModal, setOpenEditCommentModal] = useState(false);
    const handleCloseEditCmtModal = () => {
        setOpenEditCommentModal(false);
    }
    const onDeleteComment = (id) => {
        request('delete', `comments/${id}`, res => {
            successNoti("Đã xóa thành công bình luận !", true);
            setLoadCommentsCallBack(!loadComments);
        }, err => {
            console.log(err);
        });
    }

    const onUpdateComment = (id, comment) => {
        setOpenEditCommentModal(true);
        setComment(comment);
        setCommentId(id);
        console.log(id, comment);
    }

    const onUpdateCommentCallBack = () => {
        const dataForm = {
            comment: comment,
            projectId: projectId
        };
        console.log(dataForm);
        request('put',
            `/comments/${commentId}`,
            res => {
                console.log(res);
                setLoadCommentsCallBack(!loadComments);
            },
            err => {
                console.log(err);
            },
            dataForm
        )
    }

    return (
        <>
            <Box mb={3}>
                <Box mb={2}>
                    <Typography variant='body2'>
                        Bình luận ({listComment.length})
                    </Typography>
                </Box>
                {listComment.length > 0 &&
                    <Box sx={boxChildComponent} px={3}>
                        {listComment.map((item, index) => {
                            if (index === (listComment.length - 1)) {
                                return (
                                    <CommentItem key={item.id} comment={item} onBottom={true} onDeleteComment={onDeleteComment} onUpdateComment={onUpdateComment} />
                                );
                            } else {
                                return (
                                    <CommentItem key={item.id} comment={item} onBottom={false} onDeleteComment={onDeleteComment} onUpdateComment={onUpdateComment} />
                                )
                            }
                        })}
                    </Box>
                }
            </Box>
            <EditCommentModal
                open={openEditCommentModal}
                handleClose={handleCloseEditCmtModal}
                comment={comment}
                setCommentCallBack={(cmt) => setComment(cmt)}
                onUpdateCommentCallBack={onUpdateCommentCallBack}
            />
        </>
    );
}

export default CommentSection;