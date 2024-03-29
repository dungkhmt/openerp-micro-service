import BaseRequest from "./BaseRequest";

export default class PostRequest extends BaseRequest {
    addPostSell(data) {
        const url = "/public/post/sell";
        return this.post(url, data);
    }
}