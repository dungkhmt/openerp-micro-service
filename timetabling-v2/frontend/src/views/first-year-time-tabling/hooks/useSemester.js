import { request } from "api";
import { useEffect, useState } from "react"


export const useSemesters = () => {
    const [loading, setLoading] = useState(false);
    const [semesters, setSemesters] = useState([]);
    const [error, setError] = useState(null);

    useEffect(()=>{
        setLoading(true);
        request(
            "get",
            "/semester/get-all",
            (res)=>{
                console.log(res);
                setSemesters(res.data || []);
                setLoading(false);
            },
            (error)=>{
                console.error(error);
                setError(error);
            },
        )
    }, [])

    return {loading, error, semesters}
}