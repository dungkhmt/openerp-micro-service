export function validateColor(color: string): "primary" | "default" | "secondary" | "success" | "warning" | "danger" | undefined {
    const validColors = ["primary", "default", "secondary", "success", "warning", "danger"];
    return validColors.includes(color) ? color as "primary" | "default" | "secondary" | "success" | "warning" | "danger" : "default";
}