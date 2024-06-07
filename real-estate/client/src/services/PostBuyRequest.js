import BaseRequest from "./BaseRequest";

export class PostBuyRequest extends BaseRequest {
  get_page(params) {
    const url = "/public/post/buy";
    return this.get(url, params);
  }

  add_post(data) {
    const url = "/post/buy";
    return this.post(url, data);
  }

  get_post_by_account_id(accountId) {
    const url = `/public/post/buy/my-post/${accountId}`;
    return this.get(url);
  }

  update_status(data) {
    const url = "/post/buy/updateStatus";
    return this.put(url, data);
  }

  matching(data) {
    const url = "/post/buy/matching";
    return this.get(url, data);
  }
}
