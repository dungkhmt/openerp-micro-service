export const convertMillisecondsToHours = (milliseconds) => {
    const seconds = milliseconds / 1000;
    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    const date = Math.floor(hours / 24);
    if(date > 0) {
        return `${date} ngày ${hours} giờ ${minutes} phút`;
    }
    return `${hours} giờ ${minutes} phút`;
  }