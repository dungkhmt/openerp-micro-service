import { useQuery } from "@tanstack/react-query";
import { request } from "../api-middleware";
import { endPoint } from "../endpoint";
import { queryKey } from "./querykey";

export const useGetProductCateList = (params) => {
  return useQuery({
    queryKey: [queryKey.category.product_cate_list, params],
    queryFn: async () => {
      const res = await request(
        "get",
        endPoint.getProductCategory,
        (res) => {},
        () => {}
      );
      if (res.data && res.data?.code === 1) {
        return res.data?.data;
      }
    },
    keepPreviousData: true,
    onSuccess: (data) => {
      console.log("Data success: ", data);
    },
  });
};
