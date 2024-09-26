import React from 'react';

function formatDateTime(dateTimeString) {
    const dateTime = new Date(dateTimeString);
    const options = {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        timeZoneName: 'short'
    };
    return dateTime.toLocaleString('en-US', options);
}

function DateTimeFormatter({ dateTimeString }) {
    const formattedDateTime = formatDateTime(dateTimeString);
    return <span>{formattedDateTime}</span>;
}

export default DateTimeFormatter;
