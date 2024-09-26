import React from 'react';
import InputComment from './InputComment';
import CommentItem from './CommentItem';

const listComments = [
	{
		id: 1,
		user_id: 1,
		content: "Video nay hay qua, thay co the huowng dan chi tiet hon ve React duoc khong?",
		course_id: 112232,
	},
	{
		id: 2,
		user_id: 12,
		content: "Thay giao day hay, de hieu",
		course_id: 112232,
	},
	{
		id: 3,
		user_id: 5,
		content: "Thay day them ve Java Spring lam backend di thay!",
		course_id: 112232,
	},
]

export default function Comment(){
  return(
    <div>
			<h3>Viêt bình luận</h3>
			<InputComment />
			<h3>Bình luận</h3>
			{listComments.map( comment => (
				<CommentItem comment={comment} key={comment.id}/>
			))}
    </div>
  )
}