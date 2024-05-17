import privateClient from "../client/private.client";

const endpoints = {
  recent: "/search/recent-activity",
  search: "/search",
};

export const SearchService = {
  async getRecentActivity() {
    return (await privateClient.get(endpoints.recent)).data;
  },

  async search(query) {
    return (
      await privateClient.get(endpoints.search, {
        params: { q: encodeURIComponent(query) },
      })
    ).data;
  },
};
