import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

class Job:
    def __init__(self, position, description, experience, education, programming_language, tool_and_frameworks, soft_skill, attitude, wage, insurance, wishing_enviroment, treatment, location):
        self.position = position
        self.description = description
        self.experience = experience

# Dữ liệu công việc được lưu trữ trong danh sách các đối tượng Job
job_objects = [
    Job('Software Engineer', 'Develops software solutions for clients.', '3+ years experience'),
    Job('Data Analyst', 'Analyzes and interprets complex data sets.', '2+ years experience'),
    Job('Web Developer', 'Designs and develops user-friendly websites.', '4+ years experience')
]

# Người dùng nhập thông tin công việc của họ
user_description = input("Nhập mô tả công việc của bạn: ")
user_experience = input("Nhập số năm kinh nghiệm của bạn: ")

# Tạo đối tượng Job cho công việc của người dùng và sử dụng một vị trí tạm thời
user_job = Job('Temporary Position', user_description, user_experience)

# Sử dụng TF-IDF để biểu diễn văn bản thành vectơ số
vectorizer = TfidfVectorizer()
job_descriptions = [job.description for job in job_objects]
job_descriptions.append(user_job.description)
tfidf_matrix = vectorizer.fit_transform(job_descriptions)

# Tính độ tương tự cosine giữa công việc của người dùng và các công việc trong dữ liệu
cosine_similarities = cosine_similarity(tfidf_matrix[-1], tfidf_matrix[:-1]).flatten()

# Lấy chỉ số công việc có độ tương tự cao nhất
sorted_job_indices = cosine_similarities.argsort()[::-1]

# Gợi ý các công việc dựa trên độ tương tự
print("Các công việc được gợi ý cho bạn:")
for i in sorted_job_indices:
    recommended_job = job_objects[i]
    print(f"Vị trí: {recommended_job.position}, Mô tả: {recommended_job.description}, Kinh nghiệm: {recommended_job.experience}")
