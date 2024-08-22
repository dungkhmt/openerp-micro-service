import pandas as pd
import re
import nltk
from nltk.stem import WordNetLemmatizer
from nltk.corpus import stopwords
from sklearn.feature_extraction.text import TfidfVectorizer

stop_words = stopwords.words('english')
stop_words = list(stop_words)

def remove_punctuation(text):
    return re.sub(r'[^\w\s]', '', text)

def lemmatize_text(text):
    lemmatizer = WordNetLemmatizer()
    return ' '.join([lemmatizer.lemmatize(word) for word in nltk.word_tokenize(text)])

df = pd.read_csv('../openerp-resource-server/src/main/resources/course.csv')
df['combined_text'] = df['title'].str.lower() + ' ' + df['subtitle'].str.lower() + ' ' + df['level'].astype(str).str.lower()


def remove_strong_tags(text):
    return re.sub(r'<strong>.*?</strong>', '', text)

df['combined_text'] = df['combined_text'].apply(remove_strong_tags).apply(remove_punctuation).apply(lemmatize_text)

tfidf_vectorizer = TfidfVectorizer(stop_words=stop_words)
tfidf_matrix = tfidf_vectorizer.fit_transform(df['combined_text'].fillna(''))

basickeywords = ['basic', 'introduction', 'begin', 'beginner', 'introduct', 'overview', 'fundamental']
advancedkeywords = ['advanced', 'expert', 'master', 'professional', 'advanced','intermediate']

def tf_idf_begin():
    terms = tfidf_vectorizer.get_feature_names_out().tolist()

    # Tính toán TF-IDF cho basickeywords
    basic_keyword_indices = {keyword: terms.index(keyword) for keyword in basickeywords if keyword in terms}
    basic_keyword_tfidf_scores = tfidf_matrix[:, list(basic_keyword_indices.values())]
    sum_basic_tfidf_scores = basic_keyword_tfidf_scores.sum(axis=1).A1
    df['tf-idf-for-basic-student'] = sum_basic_tfidf_scores

    # Tính toán TF-IDF cho advancedkeywords
    advanced_keyword_indices = {keyword: terms.index(keyword) for keyword in advancedkeywords if keyword in terms}
    advanced_keyword_tfidf_scores = tfidf_matrix[:, list(advanced_keyword_indices.values())]
    sum_advanced_tfidf_scores = advanced_keyword_tfidf_scores.sum(axis=1).A1
    df['tf-idf-for-advanced-student'] = sum_advanced_tfidf_scores

    df.drop(columns=['combined_text'], inplace=True)

    df.to_csv('../openerp-resource-server/src/main/resources/course.csv', index=False)

tf_idf_begin()
