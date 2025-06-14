import BaseRequest from "./BaseRequest";

export default class ConversationRequest extends BaseRequest {
  getConversations() {
    const url = "/conversation/all";
    return this.get(url);
  }

  createConversation(data) {
    const url = "/conversation";
    return this.post(url, data);
  }
}
