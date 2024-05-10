import CardSell from "../../components/CardSell/CardSell";
import {useEffect, useState} from "react";
import MarkerMap from "../../components/MarkerMap/MarkerMap";
import PostRequest from "../../services/PostRequest";
import {toast} from "react-toastify";
import "./ListPageSell.css"
import FilterSell from "../../components/FilterSell/FilterSell";
import {Pagination, ScrollArea} from "@mantine/core";

const ListPageSell = ({}) => {

    const [listPost, setListPost] = useState([]);
    const [page, setPage] = useState(1);
    const [size, setSize] = useState(10);
    const [totalPages, setTotalPages] = useState(1);
    const [params, setParams] = useState({
        page: 1,
        size: 10,
        province: '',
        district: '',
        minAcreage: 0,
        fromPrice: 0,
        toPrice: 99999999999,
        minFloor: 0,
        minBedroom: 0,
        minBathroom: 0,
        minParking: 0,
        typeProperties: ["LAND", "HOUSE", "APARTMENT"],
        legalDocuments: ["HAVE", "WAIT", "HAVE_NOT"],
        directions: ["NORTH", "SOUTH", "WEST", "EAST", "EAST_NORTH", "EAST_SOUTH", "WEST_SOUTH", "WEST_NORTH"],
    })


    const handleChangePage = (value) => {
        setParams(prevParams => (
            {
                ...prevParams,
                page: value,
            }
        ))
    }
    const getListPostSell = (params) => {
        const postRequest = new PostRequest();
        postRequest.getPageSell(params)
            .then((response) => {
                if (response.code === 200) {
                    setListPost(response.data);
                    setTotalPages(response.metadata.totalPages);
                } else {
                    toast.error(response.data.message)
                }
            })
            .then()
    }

    useEffect(() => {
        getListPostSell(params);
    }, [page, params]);

    return (
        <div className="listPage">
            <div className="flexCenter filter">
                <FilterSell setParams={setParams}/>
            </div>

            <div className="flexCenter post_map_container"
            >
                <div className="postContainer"
                     style={{flex: 3}}
                >
                    <Pagination total={totalPages} value={params.page} onChange={handleChangePage}/>

                    <ScrollArea h={"100%"}>
                        {listPost.map(item => (
                            <div className="cardContainer" style={{margin: "20px 0"}}>
                                <CardSell key={item.postSellId} item={item}/>
                            </div>
                        ))}
                    </ScrollArea>
                </div>

                <div className="mapContainer">
                    <MarkerMap posts={listPost}/>
                </div>
            </div>
        </div>
    )
}

export default ListPageSell;