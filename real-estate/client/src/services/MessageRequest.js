import BaseRequest from "./BaseRequest";

export default class MessageRequest extends BaseRequest {
  getMoreMessage(params) {
    const url = "/message";
    return this.get(url, params);
  }
}
