import BaseRequest from "./BaseRequest";

export default class DistrictRequest extends BaseRequest {
  get_province() {
    const url = "/public/address/provinces";
    return this.get(url);
  }
  get_districts(data) {
    const url = "/public/address/district";
    return this.get(url, data);
  }

  get_price_districts(params) {
    const url = "/public/address/price-district";
    return this.get(url, params);
  }
}
