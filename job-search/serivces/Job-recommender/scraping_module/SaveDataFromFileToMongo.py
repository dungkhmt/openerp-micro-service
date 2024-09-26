from pymongo import MongoClient

#open json file
import json
# Kết nối đến MongoDB (mặc định sẽ kết nối đến localhost:27017)
client = MongoClient('mongodb://localhost:27017/')
db = client['job-recommender-database']
collection = db['jobs']
# Chọn cơ sở dữ liệu (nếu chưa tồn tại, nó sẽ tự động được tạo ra)
def save_data(index):
    global collection
    # Mở tệp JSON để đọc
    with open(f'./scraped_data/jobs_page_{index}.json', 'r') as json_file:
        # Load dữ liệu từ tệp JSON
        data = json.load(json_file)

    jobs_list = data['data']
    # Chèn dữ liệu vào bảng
    try:
        collection.insert_many(jobs_list)
    except:
        print("can not import data to mongodb")

def save_data_by_range(start, end):
    for i in range(start, end):
        save_data(i)

save_data_by_range(1, 101)

