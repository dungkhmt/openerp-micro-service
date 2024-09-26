var natural = require('natural');
var TfIdf = natural.TfIdf;
var tfidf = new TfIdf();

tfidf.addDocument('this document is about node.');
tfidf.addDocument('this document is about ruby.');
tfidf.addDocument('this document is about ruby and node.');
tfidf.addDocument('this document is about node. it has node examples');
console.log("-----------------combined-----------------")
tfidf.tfidfs('node ruby', function(i, measure) {
    console.log('document #' + i + ' is ' + measure);
});
console.log("-----------------node-----------------")
tfidf.tfidfs('node', function(i, measure) {
    console.log('document #' + i + ' is ' + measure);
});
console.log("-----------------ruby-----------------")
tfidf.tfidfs('ruby', function(i, measure) {
    console.log('document #' + i + ' is ' + measure);
});