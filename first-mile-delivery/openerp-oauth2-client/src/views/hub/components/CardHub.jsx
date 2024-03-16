import * as React from 'react';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Typography from '@mui/material/Typography';
import { CardActionArea } from '@mui/material';

export default function CardHub({ hub, onClick }) {
    return (
        <Card sx={{ maxWidth: 345 }}>
            <CardActionArea
                onClick={() => onClick([hub.latitude, hub.longitude])}
            >
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
            </CardActionArea>
        </Card>
    );
}