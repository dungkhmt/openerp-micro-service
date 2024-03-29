import axiosDefault from 'axios'

const mapbox_token = process.env.REACT_APP_MAPBOX_TOKEN;
export const apiGetPublicProvinces = async () => {
    const response = await axiosDefault({
        method: 'get',
        url: 'http://localhost:2805/public/address/provinces'
    });
    return response;
};


export const apiGetPublicDistrict = async (nameProvince) => {
    const encodedNameProvince = encodeURIComponent(nameProvince);
    const response = await axiosDefault({
        method: 'get',
        url: `http://localhost:2805/public/address/district?nameProvince=${encodedNameProvince}`
    })
    return response
}

export const apiAddPostSell = async (data) => {
    const response = await axiosDefault({
        method: 'post',
        url: `http://localhost:2805/public/post/sell`,
        data: data
    })
    return response
}

export const apiGetPosition = async (address) => {
    console.log(process.env.REACT_APP_MAPBOX_TOKEN)
    const response = await axiosDefault({
        method: "get",
        url: `https://api.mapbox.com/geocoding/v5/mapbox.places/${address}.json?access_token=${mapbox_token}`
    })
    return response;
}