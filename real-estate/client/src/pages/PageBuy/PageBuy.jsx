import "./PageBuy.css";
import React, { useEffect, useRef, useState } from "react";
import { toast } from "react-toastify";
import { PostBuyRequest } from "../../services/PostBuyRequest";
import CardBuy from "../../components/CardBuy/CardBuy";
import { Spoiler, Image, LoadingOverlay, Loader } from "@mantine/core";
import { Link, useParams } from "react-router-dom";
const PageBuy = () => {
  const provinceId = useParams();
  const [listPost, setListPost] = useState([]);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);
  const [countPost, setCountPost] = useState([]);
  const getListPost = (params) => {
    setLoading(true);
    if (page <= totalPages) {
      const postRequest = new PostBuyRequest();
      postRequest
        .get_page(params)
        .then((response) => {
          if (response.code === 200) {
            if (params.page === 1) {
              setListPost([...response.data]);
            } else {
              setListPost((prevPosts) => [...prevPosts, ...response.data]);
            }
            setTotalPages(response.metadata.totalPages);
          } else {
            // setListPost([])
            toast.error(response.data.message);
          }
        })
        .then(() => {
          // Thêm delay 2 giây trước khi đặt setLoading(false)
          setTimeout(() => {
            setLoading(false);
          }, 2000);
        });
    } else {
      setLoading(false);
    }
  };

  const getCountPost = () => {
    const buyRequest = new PostBuyRequest();
    buyRequest.countPost().then((response) => {
      if (response.code === 200) {
        let totalPosts = 0;
        console.log(response.data);
        response.data.forEach((item) => (totalPosts += item.totalPost));
        setCountPost([
          {
            provinceId: "",
            nameProvince: "Tất cả",
            totalPost: totalPosts,
          },
          ...response.data,
        ]);
      }
    });
  };

  const handleScroll = () => {
    const scrollHeight = document.documentElement.scrollHeight;
    const scrollTop = document.documentElement.scrollTop;
    const clientHeight = document.documentElement.clientHeight;

    // console.log(scrollHeight, scrollTop, clientHeight)
    if (scrollTop + clientHeight >= scrollHeight && loading === "") {
      getListPost({
        page: page + 1,
        size: 10,
        provinceId: provinceId,
      });
      setPage((prevPage) => prevPage + 1); // Lấy thêm 10 bài viết
    }
  };

  useEffect(() => {
    window.addEventListener("scroll", handleScroll);
    return () => window.removeEventListener("scroll", handleScroll);
  }, [handleScroll]);

  useEffect(() => {
    getCountPost();
    getListPost({
      page: 1,
      size: 10,
      provinceId: provinceId,
    });
    setPage(1);
  }, [provinceId]);

  // useEffect(() => {
  //   getCountPost();
  //   getListPost({
  //     page: page,
  //     size: 10,
  //     provinceId: provinceId,
  //   });
  // }, [page]);
  // console.log(countPost);
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

        {loading ? (
          <Loader color="blue" size="xl" />
        ) : (
          <h2 color={"#f2f2f2"}>Đã hết bài viết</h2>
        )}
      </div>

      {countPost.length > 0 && (
        <div
          style={{
            position: "fixed",
            // height: "300px",
            // minHeight: "150px",
            width: "150px",
            top: "85px",
            right: "50px",
            border: "1px solid #F2F2F2",
            backgroundColor: "#f2f2f2",
          }}
          // className="flexColStart"
        >
          <h3>Khu Vực</h3>
          <Spoiler
            maxHeight={150}
            showLabel="Mở rộng"
            hideLabel="Thu gọn"
            transitionDuration={1}
          >
            {countPost.map((item, index) => (
              <div key={index} className="province">
                <Link to={"/buy/properties?provinceId=" + item.provinceId}>
                  {item.nameProvince} ({item.totalPost})
                </Link>
              </div>
            ))}
          </Spoiler>
        </div>
      )}
    </div>
  );
};

export default PageBuy;
