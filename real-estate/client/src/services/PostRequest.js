import BaseRequest from "./BaseRequest";

export default class PostRequest extends BaseRequest {
    addPostSell = async (data) => {
        const url = "/post/sell";
        return await this.post(url, data);
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

    getPostSellOfMe() {
        const url = "/post/sell";
        return this.get(url)
    }

    updateStatus(data) {
        const url = "/post/sell/updateStatus";
        return this.put(url, data)
    }

    updatePost(data) {
        const url = "/post/sell"
        return this.put(url, data)
    }
}