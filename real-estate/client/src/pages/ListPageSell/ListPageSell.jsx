import CardSell from "../../components/CardSell/CardSell";
import React, { useEffect, useState } from "react";
import MarkerMap from "../../components/MarkerMap/MarkerMap";
import { toast, ToastContainer } from "react-toastify";
import "./ListPageSell.css";
import FilterSell from "../../components/FilterSell/FilterSell";
import { Pagination, ScrollArea } from "@mantine/core";
import PostSellRequest from "../../services/PostSellRequest";

const ListPageSell = ({}) => {
  const [listPost, setListPost] = useState([]);
  const [page, setPage] = useState(1);
  const [size, setSize] = useState(10);
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
          toast.error(response.data.message);
        }
      })
      .then();
  };

  useEffect(() => {
    getListPostSell(params);
  }, [page, params]);

  return (
    <div className="listPage">
      <div className="flexCenter filter">
        <FilterSell setParams={setParams} />
      </div>

      <div className="flexCenter post_map_container">
        <div className="postSellContainer" style={{ flex: 3 }}>
          <Pagination
            total={totalPages}
            value={params.page}
            onChange={handleChangePage}
          />

          <ScrollArea h={"95%"}>
            {listPost.map((item) => (
              <div key={item.postSellId} className="cardContainer">
                <CardSell key={item.postSellId} item={item} />
              </div>
            ))}
          </ScrollArea>
        </div>

        <div className="mapContainer">
          <MarkerMap posts={listPost} />
        </div>
      </div>
      <ToastContainer
        position="top-left"
        autoClose={3000}
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
      />
    </div>
  );
};

export default ListPageSell;
