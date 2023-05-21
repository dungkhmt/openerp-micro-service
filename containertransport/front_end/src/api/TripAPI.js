import { request } from "api"

// export const getShipmentByCode = async (data) => {
//     const resData = request(
//         "get",
//         `/shipment/${data}`, {},{}, {},{},
//       )
//     return resData;
// }

export const createTrip = (data) => {
    const resData = request(
        "post",
        `/trip/create`, {},{}, data, {},
    )
    return resData;
}