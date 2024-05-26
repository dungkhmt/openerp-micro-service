const fs = require('fs');
const natural = require('natural');
const TfIdf = natural.TfIdf;
const tokenizer = new natural.WordTokenizer();
const tfidf = new TfIdf();

// Your data
let job_titles = fs.readFileSync('JobTitleEng.txt', 'utf8').split('\n');

// Preprocess the job titles
job_titles = job_titles.map(title => preprocess(title));

// Tokenize and build vocab
job_titles.forEach((title) => {
    tfidf.addDocument(tokenizer.tokenize(title));
});

let str1 = "backend developer";
let str2 = "backend developer nodejs typescript";

console.log(preprocess(str1));
console.log(preprocess(str2));

//console.log(tfidf.tfidfs(preprocess(str1)));
console.log("------------------------------------------------------------------------");
//console.log(tfidf.tfidfs(preprocess(str2)));
tfidf.tfidfs(preprocess('developer'), function(i, measure) {
    console.log('document #' + i + ' is ' + measure);
});
// showTfidfValues(tfidf, preprocess(str2));// Calculate Cosine Similarity
// let cos_sim = cosineSimilarity(tfidf.tfidfs(preprocess(str1)), tfidf.tfidfs(preprocess(str2)));
// console.log(cos_sim);

function preprocess(text) {
    // Lowercase the text
    text = text.toLowerCase();
    // Remove special characters
    text = text.replace(/\W/g, ' ');
    // Remove single characters
    text = text.replace(/\s+[a-zA-Z]\s+/g, ' ');
    // Remove single characters from the start
    text = text.replace(/^\^[a-zA-Z]\s+/g, ' '); 
    // Substitute multiple spaces with single space
    text = text.replace(/\s+/g, ' ');
    // Remove numbers
    text = text.replace(/\d+/g, '');
    return text;
}

function cosineSimilarity(vecA, vecB) {
    console.log("xnxx:", vecA)
    let dotProduct = 0;
    let normA = 0;
    let normB = 0;
    for (let i = 0; i < vecA.length; i++) {
        dotProduct += vecA[i] * vecB[i];
        normA += Math.pow(vecA[i], 2);
        normB += Math.pow(vecB[i], 2);
    }
    return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
}

function showTfidfValues(tfidf, text) {
    console.log("xxxxxxxxxxxxxxxxxxxx")
    let tokenizedText = tokenizer.tokenize(text);
    tfidf.listTerms(tfidf.documents.length - 1).forEach(function(item) {
        if (tokenizedText.includes(item.term)) {
            console.log(item.term + ': ' + item.tfidf);
        }
    });
}