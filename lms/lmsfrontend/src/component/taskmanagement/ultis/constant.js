import {Typography} from "@material-ui/core";

export const boxComponentStyle = {
    backgroundColor: "#fff",
    // <!-- boxShadow: "0 4px 8px rgba(0,0,0,0.07)", -->
    // boxShadow: "rgba(0, 0, 0, 0.16) 0px 3px 6px, rgba(0, 0, 0, 0.23) 0px 3px 6px",
    boxShadow: "rgba(60, 64, 67, 0.3) 0px 1px 2px 0px, rgba(60, 64, 67, 0.15) 0px 2px 6px 2px",
    p: 3,
    mb: 3,
    borderRadius: '5px'
};

export const boxChildComponent = {
    backgroundColor: "#fff",
    boxShadow: "rgba(60, 64, 67, 0.3) 0px 1px 2px 0px, rgba(60, 64, 67, 0.15) 0px 1px 3px 1px",
    px: 3,
    py: 2,
    borderRadius: '5px'
}

export const centerBox = {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center'
}


export const Header = ({ children }) => {
    return (
        <Typography variant="h4" mb={4} component={'h4'}>
            {children}
        </Typography>
    );
}

export const TASK_STATUS_COLOR = {
    TASK_INPROGRESS: '#4488c5',
    TASK_OPEN: '#ed8077',
    TASK_RESOLVED: '#5eb5a6',
    TASK_CLOSED: '#a1af2f',
    ASSIGNMENT_ACTIVE: '#dc9925',
    ASSIGNMENT_ACTIVE: '#dc9925'
}

export const TASK_CATEGORY_COLOR = {
    OTHER: '#3b9dbd',
    REQUEST: '#dc9925',
    TASK: '#a1af2f',
    BUG: '#ea733b'
}

export const PRIORITY_COLOR = {
    BUG: '#eb0c0c',
    HIGH: '#eb580c',
    LOW: '#1a70eb',
    NORMAL: '#b4eb1a'
}

export const HISTORY_TAGS = {
    comment: {
        color: "#4caf93",
        action: "bình luận"
    },
    issue: {
        color: "#25de30",
        action: "thêm mới"
    },
    updated: {
        color: "#42a7e7",
        action: "cập nhật"
    }
}