import requests
from geopy.distance import geodesic

# Thay thế 'YOUR_API_KEY' bằng khóa API của bạn từ OpenCage Geocoding API
API_KEY = 'd8a4611b1a4f4ca398e253ab9e16916c'

def get_coordinates(address):
    endpoint = f"https://api.opencagedata.com/geocode/v1/json?q={address}&key={API_KEY}"
    response = requests.get(endpoint)
    data = response.json()
    if data['results']:
        location = data['results'][0]['geometry']
        return location['lat'], location['lng']
    else:
        return None

# Địa chỉ của hai địa điểm cần tính khoảng cách
address1 = "Dai hoc Bach Khoa Ha Noi"
address2 = "trường mầm non thành đông"

# Lấy tọa độ của các địa chỉ
coords1 = get_coordinates(address1)
coords2 = get_coordinates(address2)

if coords1 and coords2:
    # Tính khoảng cách giữa hai tọa độ sử dụng geopy
    distance = geodesic(coords1, coords2).kilometers
    print("Khoảng cách giữa hai địa điểm là:", distance, "kilometers")
else:
    print("Không thể xác định tọa độ cho ít nhất một trong hai địa chỉ.")
