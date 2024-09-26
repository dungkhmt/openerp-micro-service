import requests
import json


START_PAGE_TO_CLONE = int(input("press started page you want to clone: "))
END_PAGE_TO_CLONE = int(input("press end page you want to clone: "))

API_END_POINT = "http://api.topdev.vn/td/v2/jobs?fields[job]=id,slug,title,salary,company,extra_skills,\
    skills_str,skills_arr,skills_ids,job_types_str,job_levels_str,job_levels_arr,job_levels_ids,addresses,\
        status_display,detail_url,job_url,salary,published,refreshed,applied,candidate,requirements_arr,packages,\
            benefits,content,features,is_free,is_basic,is_basic_plus,is_distinction&fields[company]=slug,tagline/ ,\
                addresses,skills_arr,industries_arr,industries_str,image_cover,image_galleries,benefits&page=9&locale=vi_VN"
USER_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6ImU5MGFhMGE0ZmJjZDhlOWQ1ODMwMmE3MTM5MzNkMzE1MTY0MjFhMjRkYTJkNjg3NmMwODM5MzM3YmY2NGY0ZTYzNmY0MjAyMWQ5YmU5ZWQ3In0.eyJhdWQiOiIxOCIsImp0aSI6ImU5MGFhMGE0ZmJjZDhlOWQ1ODMwMmE3MTM5MzNkMzE1MTY0MjFhMjRkYTJkNjg3NmMwODM5MzM3YmY2NGY0ZTYzNmY0MjAyMWQ5YmU5ZWQ3IiwiaWF0IjoxNjk3MzYzODExLCJuYmYiOjE2OTczNjM4MTEsImV4cCI6MTY5NzQyMzgxMSwic3ViIjoiMjkyNTM0OSIsInNjb3BlcyI6W119.QlDkH6XHXAMelzIZulFu8if1BxaHoReQom25BqalAzUagSE-0e1MXVrGh4ZduK32U1y8CbQx7m5mi7pV1CPZ8I8nQ6YMpIp2B7usKMWUR-apZyzpwZ9LVqfGtwiqbg3BkBY4OLcxeslJOLj64FwpKypBvNyzLqmHDaBCFJURDeE"
USER_COOKIE = "apitopdev_session=eyJpdiI6Ilk3dmFuUEF5aEJ3d0pvRnpIVjNZRUE9PSIsInZhbHVlIjoiM1dEVVhLOXEycFBcL09NNEkybERaZDh6cFROUG4wRWx\
    5WHRkbnRmbUFGeWhLRXI1WktGaWZZSlU2enZWNVVZRXQ0aHFrUUthU1wvQ29RVEwwVWdVakRCVXVGMUl0bmYzWFdWOVpPdURBSmY2V3F1R1ZaaWxRQlNcLzl5MTlcL0M5X\
        C9PWiIsIm1hYyI6IjQ5NTE0MDg5NjY2OTQ2NDAxZmJiMjAwYmNjYTQwZDRiNWNhNDk2ZDU5Mjc3MDFhMmNjMmZkZWI0NmVjZDFhMjQifQ%3D%3D"

headers = {
    'Content-Type': 'application/json',
    "Authorization": f"Bearer {USER_TOKEN}",
}

def get_jobs_from_url(index, url):
# Gửi yêu cầu GET đến API
    try:
        # url = API_END_POINT  # Thay thế URL của API bằng địa chỉ thực sự của API bạn muốn sử dụng
        response = requests.get(url, headers=headers)

        # Kiểm tra xem yêu cầu đã thành công hay không (status code 200)
        if response.status_code == 200:
            # Parse dữ liệu JSON từ phản hồi
            data = response.json()

            # Lưu dữ liệu vào một file JSON
            with open(f'./scraped_data/jobs_page_{index}.json', 'w') as json_file:
                json.dump(data, json_file)

            print("Dữ liệu đã được lưu vào file data.json.")
        else:
            print("Không thể lấy dữ liệu từ API.")
    except:
        print("some bug has occured, please try again later")

def clone(START_PAGE_TO_CLONE, END_PAGE_TO_CLONE):
    if START_PAGE_TO_CLONE <= 0:
        START_PAGE_TO_CLONE = 1
    if END_PAGE_TO_CLONE - START_PAGE_TO_CLONE > 100:
        print("warning!!! maximum pages to clone is 100, automated to set end page to proper range")
        END_PAGE_TO_CLONE = START_PAGE_TO_CLONE + 100
    for i in range(START_PAGE_TO_CLONE, END_PAGE_TO_CLONE + 1):
        url = f"http://api.topdev.vn/td/v2/jobs?fields[job]=id,slug,title,salary,company,extra_skills,\
        skills_str,skills_arr,skills_ids,job_types_str,job_levels_str,job_levels_arr,job_levels_ids,addresses,\
            status_display,detail_url,job_url,salary,published,refreshed,applied,candidate,requirements_arr,packages,\
                benefits,content,features,is_free,is_basic,is_basic_plus,is_distinction&fields[company]=slug,tagline/ ,\
                    addresses,skills_arr,industries_arr,industries_str,image_cover,image_galleries,benefits&page={i}&locale=vi_VN"
        get_jobs_from_url(i, url)

clone(START_PAGE_TO_CLONE, END_PAGE_TO_CLONE)