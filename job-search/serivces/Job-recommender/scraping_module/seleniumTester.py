from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service

print("ok")
# Khởi tạo trình duyệt và mở trang web
# service = Service(executable_path='/home/thien/Documents/chromedriver_linux64/chromedriver')
# options = webdriver.ChromeOptions()
# driver = webdriver.Chrome(service=service, options=options)
url = "https://dantri.com.vn"
driver = webdriver.Chrome()
driver.get(url)

print(driver)
# Lấy dữ liệu từ trang web
data = driver.find_element(By.CLASS_NAME, 'article-title')
print(data.text)

# Đóng trình duyệt
driver.quit()
