import CardSell from "../../components/CardSell/CardSell";
import React, { useEffect, useState } from "react";
import MarkerMap from "../../components/MarkerMap/MarkerMap";
import { toast } from "react-toastify";
import "./PageSell.css";
import FilterSell from "../../components/FilterSell/FilterSell";
import { Pagination, ScrollArea } from "@mantine/core";
import PostSellRequest from "../../services/PostSellRequest";

const PageSell = ({}) => {
  const [listPost, setListPost] = useState([]);
  const [totalPages, setTotalPages] = useState(1);
  const [params, setParams] = useState({
    page: 1,
    size: 10,
    typeProperties: ["LAND", "HOUSE", "APARTMENT"],
    directions: [
      "NORTH",
      "SOUTH",
      "WEST",
      "EAST",
      "EAST_NORTH",
      "EAST_SOUTH",
      "WEST_SOUTH",
      "WEST_NORTH",
    ],
  });

  const handleChangePage = (value) => {
    setParams((prevParams) => ({
      ...prevParams,
      page: value,
    }));
  };
  const getListPostSell = (params) => {
    const postRequest = new PostSellRequest();
    postRequest
      .getPageSell(params)
      .then((response) => {
        if (response.code === 200) {
          setListPost(response.data);
          setTotalPages(response.metadata.totalPages);
        } else {
          setListPost([]);
          setTotalPages(0);
          toast.info(response.data.message);
        }
      })
      .then();
  };

  const changeItemInList = (item) => {
    setListPost((prevListPost) =>
      prevListPost.map((post) =>
        post.postSellId === item.postSellId ? item : post,
      ),
    );
  };

  useEffect(() => {
    getListPostSell(params);
  }, [params]);

  return (
    <div className="listPage">
      <div className="flexCenter filter">
        <FilterSell setParams={setParams} />
      </div>

      <div className="flexCenter post_map_container">
        <div className="postSellContainer" style={{ flex: 3 }}>
          {listPost.length > 0 ? (
            <div
              style={{
                width: "100%",
                height: "100%",
              }}
            >
              <Pagination
                total={totalPages}
                value={params.page}
                onChange={handleChangePage}
              />

              <ScrollArea h={"95%"} offsetScrollbars>
                {listPost.map((item) => (
                  <div key={item.postSellId} className="cardContainer">
                    <CardSell
                      key={item.postSellId}
                      item={item}
                      changeItem={changeItemInList}
                    />
                  </div>
                ))}
              </ScrollArea>
            </div>
          ) : (
            <p>Chưa có bài đăng</p>
          )}
        </div>

        <div className="mapContainer">
          <MarkerMap posts={listPost} />
        </div>
      </div>
    </div>
  );
};

export default PageSell;
