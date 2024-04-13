import React from "react";


export default function FormError(props) {
    /* nếu isHidden = true, return null ngay từ đầu */
    if (props.isHidden) { return null;}

    return ( <div style={{color:'red'}}>{props.errorMessage}</div>)
}