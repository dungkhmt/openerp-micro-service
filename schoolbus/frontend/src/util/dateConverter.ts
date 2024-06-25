export function convertStringInstantToDate(instant: string | null | undefined, timezoneOffset?: number): string {
    if (timezoneOffset === undefined || timezoneOffset === null) {
        timezoneOffset = 7;
    }
    if (instant === null || instant === undefined) {
        return new Date().toISOString().slice(0, 10);
    }
    const date = new Date(instant);
    date.setHours(date.getHours() + timezoneOffset);
    return date.toISOString().slice(0, 10);
}

export function convertStringInstantToDateTime(instant: string | null | undefined, timezoneOffset?: number): string {
    if (timezoneOffset === undefined || timezoneOffset === null) {
        timezoneOffset = 7;
    }
    if (instant === null || instant === undefined) {
        return new Date().toISOString().slice(0, 16);
    }
    const date = new Date(instant);
    date.setHours(date.getHours() + timezoneOffset);
    return date.toISOString().slice(0, 16);
}