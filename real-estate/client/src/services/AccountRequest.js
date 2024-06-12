import BaseRequest from "./BaseRequest";

export default class AccountRequest extends BaseRequest {
  login(data) {
    const url = "/public/account/login";
    return this.post(url, data);
  }

  logout() {
    const url = "/account/logout";
    return this.get(url);
  }

  signup(data) {
    const url = "/public/account/signup";
    return this.post(url, data);
  }

  get_current_account() {
    const url = "/account";
    return this.get(url);
  }

  update_info(data) {
    const url = "/account";
    return this.put(url, data);
  }

  update_password(data) {
    const url = "/account/updatePassword";
    return this.post(url, data);
  }

  get_info_account_by(params) {
    const url = "/public/account";
    return this.get(url, params);
  }

  reset_pass(params) {
    const url = "/public/account/forgot-password";
    return this.get(url, params);
  }
}
