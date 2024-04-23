import CardSell from "../../components/CardSell/CardSell";
import {useEffect, useState} from "react";
import MarkerMap from "../../components/MarkerMap/MarkerMap";
import PostRequest from "../../services/PostRequest";
import {toast} from "react-toastify";
import "./ListPageSell.css"
import FilterSell from "../../components/FilterSell/FilterSell";
import {Button, Pagination} from "@mantine/core";

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
                console.log(response)
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
        console.log("gia tri truyen di", params)
        getListPostSell(params);
    }, [page, params]);

    return (
        <div className="listPage">
            <div className="flexColCenter filter" style={{flex: 1}}>
                <FilterSell setParams={setParams}/>
            </div>
            <div className="postContainer" style={{flex: 3}}>
                {listPost.map(item => (
                    <div className="cardContainer" style={{margin: "20px 0"}}>
                        <CardSell key={item.postSellId} item={item}/>
                    </div>
                ))}
                <Pagination total={totalPages} value={params.page} onChange={handleChangePage} />            </div>

            <div className="flexColCenter mapContainer" style={{flex: 2}}>
                <MarkerMap posts={listPost}/>
            </div>
        </div>
    )
}

export default ListPageSell;