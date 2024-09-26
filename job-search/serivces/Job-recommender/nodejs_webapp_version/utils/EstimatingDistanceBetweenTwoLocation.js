const axios = require('axios');
const geolib = require('geolib');

// Thay thế 'YOUR_API_KEY' bằng khóa API của bạn từ OpenCage Geocoding API
const API_KEY = 'd8a4611b1a4f4ca398e253ab9e16916c';

async function getCoordinates(address) {
    try {
        const response = await axios.get(`https://api.opencagedata.com/geocode/v1/json?q=${address}&key=${API_KEY}`);
        if (response.data.results.length > 0) {
            const location = response.data.results[0].geometry;
            return { latitude: location.lat, longitude: location.lng };
        } else {
            return null;
        }
    } catch (error) {
        console.error('Error:', error);
        return null;
    }
}

async function calculateDistance(address1, address2) {
    // Địa chỉ của hai địa điểm cần tính khoảng cách
    // const address1 = "Dai hoc Bach Khoa Ha Noi"
    // const address2 = "trường mầm non thành đông"

    // Lấy tọa độ của các địa chỉ
    const coords1 = await getCoordinates(address1);
    const coords2 = await getCoordinates(address2);

    if (coords1 && coords2) {
        // Tính khoảng cách giữa hai tọa độ sử dụng geolib
        const distance = geolib.getDistance(coords1, coords2) / 1000; // Kết quả sẽ ở đơn vị kilometers
        // console.log(`Khoảng cách giữa hai địa điểm là: ${distance.toFixed(2)} kilometers`);
        return distance.toFixed(2)
    } else {
        console.log("Không thể xác định tọa độ cho ít nhất một trong hai địa chỉ.");
    }
}

const address1 = "Dai hoc Bach Khoa Ha Noi"
const address2 = "trường mầm non thành đông"

calculateDistance(address1, address2);

module.exports = calculateDistance;