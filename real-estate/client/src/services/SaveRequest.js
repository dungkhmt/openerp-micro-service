import BaseRequest from "./BaseRequest";

export default class SaveRequest extends BaseRequest {
  createSave(data) {
    const url = "/save";
    return this.post(url, data);
  }

  deleteSave(params) {
    const url = "/save";
    return this.delete(url, params);
  }

  getPostSave() {
    const url = "/save";
    return this.get(url);
  }

  getSaverOfPost(params) {
    const url = "/save/saver";
    return this.get(url, params);
  }
}
