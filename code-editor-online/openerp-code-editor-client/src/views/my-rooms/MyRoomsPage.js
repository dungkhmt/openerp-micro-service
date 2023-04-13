import { Add, Search } from "@mui/icons-material";
import {
  Button,
  Card,
  CardContent,
  CardHeader,
  Grid,
  InputAdornment,
  OutlinedInput,
  Pagination,
} from "@mui/material";
import React, { useEffect, useState } from "react";
import RoomItemCard from "./components/RoomItemCard";
import { request } from "api";
import { debounce } from "lodash";
import { useDispatch, useSelector } from "react-redux";
import { setIsVisibleRoomForm, setSelectedRoom } from "./reducers/myRoomsReducers";
import RoomForm from "./components/RoomForm";

const MyRoomsPage = () => {
  const dispatch = useDispatch();
  const { reloadData } = useSelector((state) => state.myRooms);
  const [roomList, setRoomList] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPage, setTotalPage] = useState(0);
  const [keyword, setKeyword] = useState(null);
  const handleSearchRoom = (keyword, page) => {
    request(
      "get",
      `/code-editor/rooms/search?page=${page}&size=10&sort=createDate&keyword=${
        keyword ? keyword : ""
      }`,
      (response) => {
        if (response && response.status === 200) {
          setRoomList(response.data.content);
          setTotalPage(response.data.totalPages);
        }
      }
    );
  };
  useEffect(() => {
    handleSearchRoom(keyword, currentPage - 1);
  }, [currentPage, keyword, reloadData]);
  const handleChangeKeyWord = debounce((value) => {
    setKeyword(value);
  }, 300);
  return (
    <div>
      <Card>
        <CardHeader title="Danh sách phòng" />
        <CardContent>
          <Grid container justifyContent="space-between" sx={{ marginBottom: "30px" }}>
            <Grid item xs={4}>
              <OutlinedInput
                name="searchText"
                onChange={(e) => {
                  handleChangeKeyWord(e.target.value);
                }}
                sx={{ borderRadius: "9999px", height: "45px" }}
                placeholder="Nhập từ khóa tìm kiếm"
                variant="outlined"
                fullWidth
                startAdornment={
                  <InputAdornment position="start">
                    <Search />
                  </InputAdornment>
                }
              />
            </Grid>

            <Grid item>
              <Button
                fullWidth
                variant="contained"
                sx={{ textTransform: "none" }}
                startIcon={<Add />}
                onClick={() => {
                  dispatch(setIsVisibleRoomForm(true));
                  dispatch(setSelectedRoom(null));
                }}
              >
                Thêm phòng
              </Button>
            </Grid>
          </Grid>
          {roomList?.map((e) => {
            return <RoomItemCard item={e} />;
          })}
          <br />

          <Grid container justifyContent="center">
            <Pagination
              count={totalPage}
              color="primary"
              onChange={(event, value) => {
                setCurrentPage(value);
              }}
            />
          </Grid>
        </CardContent>
      </Card>
      <RoomForm />
    </div>
  );
};

export default MyRoomsPage;
