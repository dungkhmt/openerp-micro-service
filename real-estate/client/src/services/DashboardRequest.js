import BaseRequest from "./BaseRequest";

export default class DashboardRequest extends BaseRequest {
  get_dashboard(params) {
    const url = "/public/dashboard";
    return this.get(url, params);
  }

  get_top(params) {
    const url = "/public/dashboard/top";
    return this.get(url, params);
  }
}
