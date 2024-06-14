import BaseRequest from "./BaseRequest";

export default class LikeRequest extends BaseRequest {
  createLike(data) {
    const url = "/like";
    return this.post(url, data);
  }

  deleteLike(params) {
    const url = "/like";
    return this.delete(url, params);
  }

  getLike() {
    const url = "/like";
    return this.get(url);
  }

  getLiker(params) {
    const url = "/like/liker";
    return this.get(url, params);
  }
}
