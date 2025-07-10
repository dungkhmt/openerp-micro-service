import React from "react";
import Breadcrumbs from "@mui/material/Breadcrumbs";
import Typography from "@mui/material/Typography";
import NavigateNextIcon from "@mui/icons-material/NavigateNext";
import { Link } from "react-router-dom";

const BreadcrumbsCustom = ({ paths }) => {
    return (
        <Breadcrumbs
            separator={<NavigateNextIcon fontSize="small" />}
            aria-label="breadcrumb"
            sx={{ 
                fontSize: "14px", 
                marginBottom: "16px",
            }}
        >
            {paths.map((path, index) =>
                index === paths.length - 1 ? (
                    <Typography 
                        key={index} 
                        color="text.primary" 
                        sx={{ display: "flex", alignItems: "center" }} // Đảm bảo text cũng căn giữa
                    >
                        {path.label}
                    </Typography>
                ) : (
                    <Link
                        key={index}
                        to={path.link}
                        style={{ 
                            textDecoration: "none", 
                            color: "inherit", 
                            display: "flex", 
                            alignItems: "center"  // Căn giữa cho link
                        }}
                    >
                        {path.label}
                    </Link>
                )
            )}
        </Breadcrumbs>
    );
};

export default BreadcrumbsCustom;
