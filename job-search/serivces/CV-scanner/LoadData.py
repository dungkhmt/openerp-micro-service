import os
from PyPDF2 import PdfReader
import json
import docx2txt
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
import re
import pickle
import urllib.parse

def extract_text_from_pdf(pdf_path):
    text = ""
    with open(pdf_path, "rb") as f:
        reader = PdfReader(f)
        for page in reader.pages:
            text += page.extract_text()
    return text

contentlist = []
titlelist = []
def preprocess(text):
    # Lowercase the text
    text = text.lower()
    # Remove special characters
    text = re.sub(r'\W', ' ', text)
    # Remove single characters
    text = re.sub(r'\s+[a-zA-Z]\s+', ' ', text)
    # Remove single characters from the start
    text = re.sub(r'\^[a-zA-Z]\s+', ' ', text) 
    # Substitute multiple spaces with single space
    text = re.sub(r'\s+', ' ', text, flags=re.I)
    # **Remove numbers**
    text = re.sub(r'\d+', '', text)
    #remove high length word
    text = ' '.join(word for word in text.split() if len(word) <= 20)
    return text

def load_pdfs(folder_path):
    pdf_contents = {}
    for file_name in os.listdir(folder_path):
        if file_name.endswith(".pdf"):
            file_path = os.path.join(folder_path, file_name)
            text = extract_text_from_pdf(file_path)
            pdf_contents[file_name] = {
                "content": text
            }
            contentlist.append(preprocess(text))
            titlelist.append(file_name)
    return pdf_contents

# Folder containing PDF files
folder_path = r"C:\Users\ADMIN\Desktop\CVSearchEngine\cv_english"
job = docx2txt.process(r"C:\Users\ADMIN\Desktop\CVSearchEngine\python-job-description.docx")
# Load PDFs and store content and file names in a dictionary
pdf_contents = load_pdfs(folder_path)

vectorizer = CountVectorizer()
count_matrix = vectorizer.fit_transform(contentlist)

index = 0
maxCosine = 0
idx = 0
for i in count_matrix:
    cos_sim = cosine_similarity(vectorizer.transform([preprocess(job)]), i)
    if maxCosine < cos_sim:
        maxCosine = cos_sim[0][0]
        index = idx
    idx += 1

print("max cosine score: ", maxCosine)
print("most matched cv ", titlelist[index])
print("total cvs: ", titlelist)

with open('vectorizer.pkl', 'wb') as f:
    pickle.dump(vectorizer, f)

with open('countMatrix.pkl', 'wb') as f:
    pickle.dump(count_matrix, f)

with open('titleList.pkl', 'wb') as f:
    pickle.dump(titlelist, f)
url = []
for titile in titlelist:
    encoded_file_name = urllib.parse.quote(titile)
    url.append("https://firebasestorage.googleapis.com/v0/b/cv-data-e11e1.appspot.com/o/" + encoded_file_name + "?alt=media")

with open('url.pkl', 'wb') as f:
    pickle.dump(url, f)
# Save dictionary to a file
output_file = "pdf_contents.json"
with open(output_file, "w") as f:
    json.dump(contentlist, f)
output_file = "url.json"
with open("xnxx.json", "w") as f:
    json.dump(url, f)

# print("PDF contents saved to:", output_file)

