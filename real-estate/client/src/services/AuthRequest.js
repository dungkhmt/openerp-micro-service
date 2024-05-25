import BaseRequest from "./BaseRequest";

export default class AuthRequest extends BaseRequest {
    login = async (data) => {
        const url = "/public/account/login";
        return await this.post(url, data);
    }

    signup = async (data) => {
        const url = "/public/account/signup";
        return await this.post(url, data);
    }

    get_current_account = async (data) => {
        const url = "/account";
        return await this.get(url);
    }

    update_info = async (data) => {
        const url = "/account";
        return await this.put(url, data);
    }

    update_password = async (data) => {
        const url = "/account/updatePassword";
        return await this.post(url, data);
    }

    get_info_account_by = async (accountId) => {
        const url = "/public/account";
        return await this.get(url, accountId);
    }
}