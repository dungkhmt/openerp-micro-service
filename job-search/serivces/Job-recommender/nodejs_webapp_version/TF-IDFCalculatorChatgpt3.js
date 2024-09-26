const natural = require('natural');
const TfIdf = natural.TfIdf;
const cosineSimilarity = require('ml-cosine-similarity');

// Your data
const fs = require('fs');
const stopWords = new Set(require('stopwords').english);
const stemmer = natural.PorterStemmer;

const jobTitles = fs.readFileSync('JobTitleEng.txt', 'utf-8').split('\n');

// Define a function to preprocess the text
function preprocess(text) {
  // Lowercase the text
  text = text.toLowerCase();
  // Remove special characters and numbers
  text = text.replace(/[^a-zA-Z ]/g, '');
  // Tokenize
  const tokens = text.split(/\s+/);
  // Remove stopwords and stem the words
  const filteredTokens = tokens.filter(token => !stopWords.has(token)).map(token => stemmer.stem(token));
  return filteredTokens.join(' ');
}

// Preprocess the job titles
const preprocessedJobTitles = jobTitles.map(preprocess);

// Create the Transform
const tfidf = new TfIdf();

// Tokenize and build vocab
preprocessedJobTitles.forEach(title => tfidf.addDocument(title));

const str1 = "java web BackEnd Developer";
const str2 = "backend developer nodejs typescript";

console.log(preprocess(str1));
console.log(preprocess(str2));

const vector1 = tfidf.tfIdfSparse(preprocess(str1));
const vector2 = tfidf.tfIdfSparse(preprocess(str2));

console.log(vector1);
console.log(vector2);

// Calculate Cosine Similarity
const cosSim = cosineSimilarity(vector1, vector2);
console.log(cosSim);
