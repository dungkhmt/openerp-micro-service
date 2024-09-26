import { request } from "api";
import { useEffect, useState } from "react"


export const useGroups = () => {
    const [loading, setLoading] = useState(false);
    const [groups, setGroups] = useState([]);
    const [error, setError] = useState(null);

    useEffect(()=>{
        setLoading(true);
        request(
            "get",
            "/group/get-all",
            (res)=>{
                setGroups(res.data || []);
                setLoading(false);
            },
            (error)=>{
                console.error(error);
                setError(error);
            },
        )
    }, [])

    return {loading, error, groups}
}