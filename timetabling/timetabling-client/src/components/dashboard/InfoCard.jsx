import React from "react";
import {
    Avatar,
    Card,
    CardContent,
    Grid,
    SvgIcon,
    Typography,
} from "@mui/material";

const IconComponent = ({ icon }) => {
    const Icon = icon;
    return <Icon />;
};

const InfoCard = (props) => {
    const { icon, iconColor, mainTitle, subTitle } = props;
    return (
        <Card elevation={3} sx={{ borderRadius: "8px" }}>
            <CardContent sx={{ paddingBottom: "16px !important" }}>
                <Grid container spacing={4}>
                    <Grid item xs={2} sx={{ display: "flex", alignItems: "center" }}>
                        <Avatar
                            sx={{
                                backgroundColor: iconColor,
                                height: 38,
                                width: 38,
                            }}
                        >
                            <SvgIcon>
                                <IconComponent icon={icon} />
                            </SvgIcon>
                        </Avatar>
                    </Grid>
                    <Grid item xs={10} sx={{ textAlign: "left" }}>
                        <Typography variant="h6" color={iconColor} sx={{ fontWeight: 600 }}>
                            {mainTitle}
                        </Typography>
                        <Typography
                            variant="overline"
                            color="#111927"
                            sx={{ fontWeight: 500, fontSize: "11px" }}
                        >
                            {subTitle}
                        </Typography>
                    </Grid>
                </Grid>
            </CardContent>
        </Card>
    );
};

export default InfoCard;
