import BaseRequest from "./BaseRequest";

export default class PostSellRequest extends BaseRequest {
  addPostSell(data) {
    const url = "/post/sell";
    return this.post(url, data);
  }

  addPostBuy(data) {
    const url = "/post/buy";
    return this.post(url, data);
  }

  getPostSellById(id) {
    const url = `/public/post/sell/${id}`;
    return this.get(url);
  }

  getPageSell(params) {
    const url = "/public/post/sell";
    return this.get(url, params);
  }

  get_post_by_accountId(accountId) {
    const url = `/public/post/sell/my-post/${accountId}`;
    return this.get(url);
  }

  update_status(data) {
    const url = "/post/sell/updateStatus";
    return this.put(url, data);
  }

  updatePost(data) {
    const url = "/post/sell";
    return this.put(url, data);
  }
}
