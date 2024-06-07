import "./PageBuy.css";
import React, { useEffect, useRef, useState } from "react";
import { toast } from "react-toastify";
import { PostBuyRequest } from "../../services/PostBuyRequest";
import CardBuy from "../../components/CardBuy/CardBuy";
import { Spoiler, Image, LoadingOverlay } from "@mantine/core";
const PageBuy = () => {
  const [listPost, setListPost] = useState([]);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState("");
  const getListPost = (params) => {
    setLoading("Đang tìm thêm bài viết");
    if (page <= totalPages) {
      const postRequest = new PostBuyRequest();
      postRequest
        .get_page(params)
        .then((response) => {
          if (response.code === 200) {
            setListPost((prevPosts) => [...prevPosts, ...response.data]);
            setTotalPages(response.metadata.totalPages);
          } else {
            // setListPost([])
            toast.error(response.data.message);
          }
        })
        .then(() => {
          // Thêm delay 2 giây trước khi đặt setLoading(false)
          setTimeout(() => {
            setLoading("");
          }, 2000);
        });
    } else {
      setLoading("Đã hết bài viết");
    }
  };

  const handleScroll = () => {
    const scrollHeight = document.documentElement.scrollHeight;
    const scrollTop = document.documentElement.scrollTop;
    const clientHeight = document.documentElement.clientHeight;

    // console.log(scrollHeight, scrollTop, clientHeight)
    if (scrollTop + clientHeight >= scrollHeight && loading === "") {
      setPage((prevPage) => prevPage + 1); // Lấy thêm 10 bài viết
    }
  };

  useEffect(() => {
    window.addEventListener("scroll", handleScroll);
    return () => window.removeEventListener("scroll", handleScroll);
  }, [handleScroll]);

  useEffect(() => {
    getListPost({
      page: page,
      size: 10,
    });
  }, [page]);
  return (
    <div className="page-buy-container">
      <div className="list-card-buy-container">
        {listPost?.map((item) => (
          <div key={item.postBuyId} className="card-buy-container">
            <Spoiler maxHeight={350} showLabel="Đọc thêm..." hideLabel="Ẩn...">
              <CardBuy key={item.postBuyId} item={item} />
            </Spoiler>
          </div>
        ))}

        {loading && <h2 style={{ color: "#F2F2F2" }}>{loading}</h2>}
      </div>
    </div>
  );
};

export default PageBuy;
