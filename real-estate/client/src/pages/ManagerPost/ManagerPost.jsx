import "./ManagerPost.css";
import React, { useEffect, useState } from "react";
import PostSellRequest from "../../services/PostSellRequest";
import { Link, useNavigate, useParams } from "react-router-dom";
import CardSell from "../../components/CardSell/CardSell";
import {
  ActionIcon,
  Button,
  Menu,
  Modal,
  rem,
  Tabs,
  Table,
  Avatar,
  Group,
  Text,
  Anchor,
  ScrollArea,
} from "@mantine/core";
import { IoIosMore } from "react-icons/io";
import { MdOutlineDone } from "react-icons/md";
import { FaTrash } from "react-icons/fa";
import { IoHammerOutline } from "react-icons/io5";
import { HiOutlineLockOpen } from "react-icons/hi2";
import { toast } from "react-toastify";
import FixPostSell from "../../components/FixPostSell/FixPostSell";
import InfoPostSell from "../../components/InfoPostSell/InfoPostSell";
import ContactBox from "../../components/ContactBox/ContactBox";
import { useDispatch, useSelector } from "react-redux";
import AccountRequest from "../../services/AccountRequest";
import { set_current_account } from "../../store/account";
import CardBuy from "../../components/CardBuy/CardBuy";
import { PostBuyRequest } from "../../services/PostBuyRequest";
import LikeRequest from "../../services/LikeRequest";
import { IMaskInput } from "react-imask";
import { IconHeart } from "@tabler/icons-react";

const ManagerPost = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const { accountId } = useParams();

  const currentAccount = useSelector((state) => state.account.currentData);
  const isOwnerPost = currentAccount.accountId == accountId;

  const [infoOwner, setInfoOwner] = useState({});

  const sellRequest = new PostSellRequest();
  const [listPostSell, setListPostSell] = useState([]);
  const [sellSelect, setSellSelect] = useState({});

  const buyRequest = new PostBuyRequest();
  const [listPostBuy, setListPostBuy] = useState([]);
  const [sellsFit, setSellsFit] = useState([]);
  const [buySelect, setBuySelect] = useState({});
  const [showPost, setShowPost] = useState(false);

  const likeRequest = new LikeRequest();
  const [listLike, setListLike] = useState([]);
  const [listLikerPost, setListLikerPost] = useState([]);

  const getLikerPost = (item) => {
    likeRequest
      .getLiker(
        item?.postSellId > 0
          ? { postId: item.postSellId, typePost: "SELL" }
          : { postId: item.postBuyId, typePost: "BUY" },
      )
      .then((response) => {
        if (response.code === 200) {
          if (response.data.length > 0) {
            setListLikerPost(response.data);
          } else {
            toast.info("Chưa có người nào thích bài viết của bạn");
          }
        }
      });
  };

  const changeItemInSellFit = (item) => {
    setSellsFit((prevListPost) =>
      prevListPost.map((post) =>
        post.postSellId === item.postSellId ? item : post,
      ),
    );
  };
  const changeItemSell = (item) => {
    setListPostSell((prevListPost) =>
      prevListPost.map((post) =>
        post.postSellId === item.postSellId ? item : post,
      ),
    );

    if (item.likeId > 0) {
      setListLike([item, ...listLike]);
    }
  };

  const changeItemBuy = (item) => {
    setListPostBuy((prevListPost) =>
      prevListPost.map((post) =>
        post.postBuyId === item.postBuyId ? item : post,
      ),
    );
    if (item.likeId > 0) {
      setListLike([item, ...listLike]);
    }
  };

  const deleteItemLike = (item) => {
    const updatedListLike = listLike.filter(
      (like) => like.createdAt !== item.createdAt,
    );
    setListLike(updatedListLike);
    if (item.authorId === currentAccount.accountId) {
      if (item?.postBuyId > 0) {
        setListPostBuy(
          listPostBuy.map((post) =>
            post.postBuyId === item.postBuyId ? item : post,
          ),
        );
      } else {
        setListPostSell(
          listPostSell.map((post) =>
            post.postSellId === item.postSellId ? item : post,
          ),
        );
      }
    }
    setTimeout(() => {
      toast.info("Hãy tải lại trang để nhận dữ liệu mới");
    }, 500);
  };
  const handleChangeStatusSell = (status, postSellId) => {
    sellRequest
      .update_status({
        status: status,
        postId: postSellId,
      })
      .then((response) => {
        if (response.code === 200) {
          setListPostSell((prevListPostSell) =>
            prevListPostSell.map((post) =>
              post.postSellId === postSellId
                ? { ...post, postStatus: status }
                : post,
            ),
          );
          toast.success(response.data);
        } else {
          toast.error(response.data.message);
        }
      });
  };

  const handleChangeStatusBuy = (status, postBuyId) => {
    buyRequest
      .update_status({
        status: status,
        postId: postBuyId,
      })
      .then((response) => {
        if (response.code === 200) {
          setListPostBuy((prevListPostBuy) =>
            prevListPostBuy.map((post) =>
              post.postBuyId === postBuyId
                ? { ...post, postStatus: status }
                : post,
            ),
          );
          toast.success(response.data);
        } else {
          toast.error(response.data.message);
        }
      });
  };

  const handleUpdatePost = () => {
    sellRequest.updatePost(sellSelect).then((response) => {
      if (response.code === 200) {
        toast.success(response.data);
        setListPostSell((prevListPostSell) =>
          prevListPostSell.map((post) =>
            post.postSellId === sellSelect.postSellId ? sellSelect : post,
          ),
        );
        setSellSelect({});
        setShowPost(false);
      } else {
        toast.error(response.data.message);
      }
    });
  };

  const refreshPostSells = () => {
    sellRequest.get_post_by_accountId(accountId).then((response) => {
      if (response.code === 200) {
        setListPostSell(response.data);
      }
    });
  };

  const refreshPostBuys = () => {
    buyRequest.get_post_by_account_id(accountId).then((response) => {
      if (response.code === 200) {
        setListPostBuy(response.data);
      }
    });
  };

  const refreshLike = () => {
    likeRequest.getLike().then((response) => {
      if (response.code === 200) {
        setListLike(response.data);
      }
    });
  };

  const match_buy = (itemBuy) => {
    buyRequest
      .matching({
        postBuyId: itemBuy.postBuyId,
      })
      .then((response) => {
        if (response.code === 200 && response.data.length > 0) {
          setSellsFit(response.data);
        } else {
          toast.error("Không có bài viết nào phù hợp");
        }
      });
  };
  const get_account = () => {
    const accountRequest = new AccountRequest();
    accountRequest
      .get_info_account_by({
        accountId,
      })
      .then((response) => {
        if (response.code === 200) {
          setInfoOwner(response.data);
          if (isOwnerPost) {
            dispatch(set_current_account(response.data));
          }
        } else {
          toast.error(response.data.message);
          navigate("/", { replace: true });
        }
      });
  };

  useEffect(() => {
    get_account();
    if (isOwnerPost) {
      refreshLike();
    }
    Object.keys(sellSelect).length === 0 && refreshPostSells();
    Object.keys(buySelect).length === 0 && refreshPostBuys();
  }, [accountId]);
  if (
    listPostSell.length === 0 &&
    listPostBuy.length === 0 &&
    listLike.length === 0
  ) {
    return (
      <div>
        <h2>Bạn chưa có bài viết nào</h2>
        <Link to="/add-post-sell">Thêm bài viết mới</Link>
      </div>
    );
  } else {
    return (
      <div className="list-post-sell-container">
        <Tabs
          style={{
            width: "70%",
            margin: "10px 50px 10px 10%",
            display: "flex",
            // backgroundColor: "aqua",
            position: "relative",
          }}
          defaultValue="sell"
          color="green"
          variant="pills"
          orientation="vertical"
        >
          <Tabs.List
            style={{
              position: "sticky",
              left: "5px",
              top: "80px",
              height: "150px",
              marginRight: "5px",
            }}
          >
            <Tabs.Tab value="sell">
              Bài viết bán ({listPostSell.length})
            </Tabs.Tab>
            <Tabs.Tab value="buy">Bài viết mua ({listPostBuy.length})</Tabs.Tab>
            {isOwnerPost && (
              <Tabs.Tab value="like">
                Bài viết đã thích ({listLike.length})
              </Tabs.Tab>
            )}
          </Tabs.List>

          <Tabs.Panel value="sell">
            {listPostSell.map((item) => (
              <div
                key={item.postSellId}
                className="manager-post-sell-container"
              >
                <CardSell
                  key={item.postSellId}
                  item={item}
                  changeItem={changeItemSell}
                />
                {isOwnerPost && (
                  <div className="feature-more">
                    <Menu shadow="sm">
                      <Menu.Target>
                        <ActionIcon variant="subtle" color="gray">
                          <IoIosMore
                            style={{ margin: "0 auto" }}
                            className="icon"
                          />
                        </ActionIcon>
                      </Menu.Target>

                      <Menu.Dropdown>
                        {item.postStatus === "OPENING" ? (
                          <Menu.Item
                            onClick={() =>
                              handleChangeStatusSell("DONE", item.postSellId)
                            }
                            leftSection={
                              <MdOutlineDone
                                style={{ width: rem(14), height: rem(14) }}
                              />
                            }
                          >
                            Đã bán
                          </Menu.Item>
                        ) : (
                          <Menu.Item
                            onClick={() =>
                              handleChangeStatusSell("OPENING", item.postSellId)
                            }
                            leftSection={
                              <HiOutlineLockOpen
                                style={{ width: rem(14), height: rem(14) }}
                              />
                            }
                          >
                            Mở bán
                          </Menu.Item>
                        )}
                        <Menu.Item
                          onClick={() =>
                            handleChangeStatusSell("CLOSED", item.postSellId)
                          }
                          leftSection={
                            <FaTrash
                              style={{ width: rem(14), height: rem(14) }}
                            />
                          }
                        >
                          Xóa bài viết
                        </Menu.Item>
                        <Menu.Item
                          onClick={() => setSellSelect(item)}
                          leftSection={
                            <IoHammerOutline
                              style={{ width: rem(14), height: rem(14) }}
                            />
                          }
                          color="red"
                        >
                          Sửa
                        </Menu.Item>
                        <Menu.Item
                          onClick={() => getLikerPost(item)}
                          leftSection={
                            <IconHeart
                              style={{ width: rem(14), height: rem(14) }}
                            />
                          }
                          color="red"
                        >
                          Đã thích
                        </Menu.Item>
                      </Menu.Dropdown>
                    </Menu>
                  </div>
                )}
              </div>
            ))}
          </Tabs.Panel>
          <Tabs.Panel value="buy">
            {listPostBuy.map((item) => (
              <div key={item.postBuyId} className="manager-post-buy-container">
                <CardBuy item={item} changeItem={changeItemBuy} />
                {isOwnerPost && (
                  <div className="feature-more">
                    <Menu shadow="sm">
                      <Menu.Target>
                        <ActionIcon variant="subtle" color="gray">
                          <IoIosMore
                            style={{ margin: "0 auto" }}
                            className="icon"
                          />
                        </ActionIcon>
                      </Menu.Target>

                      <Menu.Dropdown>
                        {item.postStatus === "OPENING" ? (
                          <Menu.Item
                            onClick={() =>
                              handleChangeStatusBuy("DONE", item.postBuyId)
                            }
                            leftSection={
                              <MdOutlineDone
                                style={{ width: rem(14), height: rem(14) }}
                              />
                            }
                          >
                            Đã tìm thấy
                          </Menu.Item>
                        ) : (
                          <Menu.Item
                            onClick={() =>
                              handleChangeStatusBuy("OPENING", item.postBuyId)
                            }
                            leftSection={
                              <HiOutlineLockOpen
                                style={{ width: rem(14), height: rem(14) }}
                              />
                            }
                          >
                            Mở
                          </Menu.Item>
                        )}
                        <Menu.Item
                          onClick={() =>
                            handleChangeStatusBuy("CLOSED", item.postBuyId)
                          }
                          leftSection={
                            <FaTrash
                              style={{ width: rem(14), height: rem(14) }}
                            />
                          }
                        >
                          Xóa bài viết
                        </Menu.Item>
                        <Menu.Item
                          onClick={() => match_buy(item)}
                          leftSection={
                            <IoHammerOutline
                              style={{ width: rem(14), height: rem(14) }}
                            />
                          }
                          color="red"
                        >
                          Khớp
                        </Menu.Item>
                        <Menu.Item
                          onClick={() => getLikerPost(item)}
                          leftSection={
                            <IconHeart
                              style={{ width: rem(14), height: rem(14) }}
                            />
                          }
                          color="red"
                        >
                          Đã thích
                        </Menu.Item>
                      </Menu.Dropdown>
                    </Menu>
                  </div>
                )}
              </div>
            ))}
          </Tabs.Panel>
          <Tabs.Panel value="like">
            {listLike?.map((item, index) => (
              <div style={{ width: "100%" }} key={index}>
                {item?.postSellId > 0 ? (
                  <div className="manager-post-sell-container">
                    <CardSell item={item} changeItem={deleteItemLike} />
                  </div>
                ) : (
                  <div className="manager-post-buy-container">
                    <CardBuy item={item} changeItem={deleteItemLike} />
                  </div>
                )}
              </div>
            ))}
          </Tabs.Panel>
        </Tabs>
        <div className="information-author-post">
          <ContactBox author={infoOwner} isOwner={isOwnerPost} />
        </div>

        <Modal
          opened={Object.keys(sellSelect).length > 0}
          onClose={() => setSellSelect({})}
          size={"90%"}
          title="Thay đổi thông tin"
          scrollAreaComponent={ScrollArea.Autosize}
          yOffset="15vh"
        >
          {showPost && Object.keys(sellSelect).length > 0 && (
            <div>
              <InfoPostSell propertyDetails={sellSelect} />
              <div>
                <Button
                  style={{
                    margin: "0 15px",
                  }}
                  onClick={() => setShowPost(false)}
                >
                  Chỉnh sửa
                </Button>
                <Button onClick={handleUpdatePost}>Cập nhập</Button>
              </div>
            </div>
          )}

          {!showPost && Object.keys(sellSelect).length > 0 && (
            <FixPostSell
              propertyDetails={sellSelect}
              setPropertyDetails={setSellSelect}
              setShowPost={setShowPost}
            />
          )}
        </Modal>

        <Modal
          opened={sellsFit.length > 0}
          onClose={() => setSellsFit([])}
          size={"90%"}
          title="Những bài viết phù hợp"
          scrollAreaComponent={ScrollArea.Autosize}
          yOffset="15vh"
        >
          {sellsFit.map((item) => (
            <CardSell
              key={item.postSellId}
              item={item}
              changeItem={changeItemInSellFit}
            />
          ))}
        </Modal>

        <Modal
          opened={listLikerPost.length > 0}
          onClose={() => setListLikerPost([])}
          size="auto"
          title="Danh sách người đã lưu bài viết của bạn"
          zIndex={1100}
          yOffset="15vh"
        >
          <Table.ScrollContainer minWidth={500}>
            <Table verticalSpacing="sm">
              <Table.Thead>
                <Table.Tr>
                  <Table.Th>Người dùng</Table.Th>
                  <Table.Th>Email</Table.Th>
                  <Table.Th>Số điện thoại</Table.Th>
                  <Table.Th />
                </Table.Tr>
              </Table.Thead>
              <Table.Tbody>
                {listLikerPost.map((item, index) => {
                  return (
                    <Table.Tr key={index}>
                      <Table.Td>
                        <Group gap="sm">
                          {/*<Link to={"/manager-post/" + item.accountId}>*/}
                          <Avatar
                            size={30}
                            src={item.avatar}
                            radius={30}
                            component="a"
                            href={"/manager-post/" + item.accountId}
                            target="_blank"
                          />
                          {/*</Link>*/}
                          <Text fz="sm" fw={500}>
                            {item.name}
                          </Text>
                        </Group>
                      </Table.Td>

                      <Table.Td>
                        <Anchor component="button" size="sm">
                          {item.email}
                        </Anchor>
                      </Table.Td>
                      <Table.Td>
                        <Text
                          fz="sm"
                          // component={IMaskInput}
                          // mask="0000-000-000"
                        >
                          {item.phone}
                        </Text>
                      </Table.Td>
                    </Table.Tr>
                  );
                })}
              </Table.Tbody>
            </Table>
          </Table.ScrollContainer>
        </Modal>
      </div>
    );
  }
};

export default ManagerPost;
