from flask import Flask, jsonify, request
import pickle
from sklearn.metrics.pairwise import cosine_similarity
import re
app = Flask(__name__)

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

@app.route('/')
def home():
    return "Hello, World!"

@app.route('/api/data', methods=['GET'])
def get_data():
    data = {"name": "John", "age": 30, "city": "New York"}
    return jsonify(data)

@app.route('/api/find-candidate', methods=['POST'])
def echo():
    data = request.get_json()
    requirements = data.get('requirements', '')
   
    with open('vectorizer.pkl', 'rb') as f:
        vectorizer = pickle.load(f)
    with open('countMatrix.pkl', 'rb') as f:
        count_matrix = pickle.load(f)
    with open('url.pkl', 'rb') as f:
        titleList = pickle.load(f)
    cosine_similarities = cosine_similarity( vectorizer.transform([preprocess(requirements)]), count_matrix[:-1])
    top_indices = cosine_similarities.argsort()[0][-10:][::-1]
    # requirements_process = [vectorizer.transform([preprocess(requirements)])]
    return_data = []
    for i in top_indices:
        return_data.append(titleList[i])
    return jsonify({'topCV': return_data})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
