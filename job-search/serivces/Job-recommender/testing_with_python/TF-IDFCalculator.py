from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
import re
from nltk.corpus import stopwords
from nltk.stem import PorterStemmer

# Your data
with open('JobTitleEng.txt', 'r', encoding='utf-8') as file:
    job_titles = [line.strip() for line in file]

# Initialize the stemmer
stemmer = PorterStemmer()

# Define a function to preprocess the text
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
    # Remove prefixed 'b'
    text = re.sub(r'^b\s+', '', text)
    # **Remove numbers**
    text = re.sub(r'\d+', '', text)
    # Remove stopwords and stem the words
    return text

# Preprocess the job titles
job_titles = [preprocess(title) for title in job_titles]
# Create the Transform
vectorizer = TfidfVectorizer()

# Tokenize and build vocab
vectorizer.fit_transform(job_titles)
str1= "BackEnd Developer web"
str2 = " developer backend web java spring boot"
print(preprocess(str1))
print(preprocess(str2))

print(vectorizer.transform([preprocess(str1)]))
print("------------------------------------------------------------------------")
print(vectorizer.transform([preprocess(str2)]))

# Calculate Cosine Similarity
cos_sim = cosine_similarity(vectorizer.transform([preprocess(str1)]), vectorizer.transform([preprocess(str2)]))
print(cos_sim)
