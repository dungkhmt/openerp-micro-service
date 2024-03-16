import * as React from 'react';
import Card from '@mui/material/Card';
import CardActions from '@mui/material/CardActions';
import CardContent from '@mui/material/CardContent';
import CardMedia from '@mui/material/CardMedia';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';

export default function HubPopup({ hub, onHubDelete, onHubEdit }) {

    const handleEdit = () => {
        onHubEdit(hub);
    }
    const handleDelete = () => {
        onHubDelete(hub);
    }

    return (
        <Card sx={{ maxWidth: 345 }}>
            <CardMedia
                sx={{ height: 140 }}
                image="https://elcojsc.com/wp-content/uploads/2021/04/gd-29.jpg"
                title="green iguana"
            />
            <CardContent>
                <Typography gutterBottom variant="h5" component="div">
                    {hub.hubCode}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                    {hub.address}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                    {hub.status}
                </Typography>
            </CardContent>
            <CardActions>
                <Button onClick={handleEdit} size="small">Edit</Button>
                <Button onClick={
                    handleDelete
                } size="small">Delete</Button>
            </CardActions>
        </Card>
    );
}