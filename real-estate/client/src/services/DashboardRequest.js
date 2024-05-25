import BaseRequest from "./BaseRequest";

export default class DashboardRequest extends BaseRequest {
    get_dashboard = (params) => {
        const url = "/public/dashboard";
        return this.get(url, params)
    }
}