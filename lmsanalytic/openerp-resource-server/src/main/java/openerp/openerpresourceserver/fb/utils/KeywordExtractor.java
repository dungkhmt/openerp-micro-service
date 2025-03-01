package openerp.openerpresourceserver.fb.utils;
import java.util.*;
import java.util.regex.Pattern;

public class KeywordExtractor {
    // Class to hold word and its TF-IDF score
    static class WordScore {
        String word;
        double score;

        WordScore(String word, double score) {
            this.word = word;
            this.score = score;
        }
    }

    // Method to extract keywords from text
    public List<String> extractKeywords(String text, int numKeywords) {
        // Split text into words and clean them
        List<String> words = preprocessText(text);

        // Calculate term frequency (TF) for the document
        Map<String, Integer> termFreq = calculateTermFrequency(words);

        // Since this is a single document, IDF will be simplified
        // In a real multi-document scenario, IDF would be calculated across documents
        Map<String, Double> tfidfScores = calculateTFIDF(termFreq, words.size());

        // Sort words by TF-IDF scores
        List<WordScore> scoredWords = new ArrayList<>();
        for (Map.Entry<String, Double> entry : tfidfScores.entrySet()) {
            scoredWords.add(new WordScore(entry.getKey(), entry.getValue()));
        }

        scoredWords.sort((a, b) -> Double.compare(b.score, a.score));

        // Return top N keywords
        List<String> keywords = new ArrayList<>();
        for (int i = 0; i < Math.min(numKeywords, scoredWords.size()); i++) {
            keywords.add(scoredWords.get(i).word);
        }

        return keywords;
    }

    // Preprocess text: remove punctuation, convert to lowercase, split into words
    private List<String> preprocessText(String text) {
        // Remove punctuation and convert to lowercase
        String cleanedText = text.replaceAll("[^a-zA-Z\\s]", "").toLowerCase();

        // Split into words and remove stop words
        String[] wordsArray = cleanedText.split("\\s+");
        List<String> words = new ArrayList<>();
        Set<String> stopWords = getStopWords();

        for (String word : wordsArray) {
            if (!word.isEmpty() && !stopWords.contains(word) && word.length() > 2) {
                words.add(word);
            }
        }

        return words;
    }

    // Calculate term frequency
    private Map<String, Integer> calculateTermFrequency(List<String> words) {
        Map<String, Integer> termFreq = new HashMap<>();

        for (String word : words) {
            termFreq.put(word, termFreq.getOrDefault(word, 0) + 1);
        }

        return termFreq;
    }

    // Calculate TF-IDF scores
    private Map<String, Double> calculateTFIDF(Map<String, Integer> termFreq, int totalWords) {
        Map<String, Double> tfidf = new HashMap<>();

        for (Map.Entry<String, Integer> entry : termFreq.entrySet()) {
            // TF = frequency of term / total words
            double tf = (double) entry.getValue() / totalWords;

            // For single document, IDF is simplified (typically log(N/df) where N is num of docs)
            // Here we'll use a simplified version since we have one document
            double idf = Math.log((double) totalWords / (entry.getValue() + 1));

            tfidf.put(entry.getKey(), tf * idf);
        }

        return tfidf;
    }

    // Common English stop words
    private Set<String> getStopWords() {
        return new HashSet<>(Arrays.asList(
                "this","might","that","the", "is", "at", "which", "on", "a", "an", "and", "or", "but",
                "in", "to", "for", "of", "with", "by", "from", "up", "about"
        ));
    }

    // Main method for testing
    public static void main(String[] args) {
        String sampleText = "Business intelligence (BI) is a set of technological processes for collecting, managing and analyzing organizational data to yield insights that inform business strategies and operations.\n" +
                "\n" +
                "Business intelligence analysts transform raw data into meaningful insights that drive strategic decision-making within an organization. BI tools enable business users to access different types of data—historical and current, third-party and in-house, as well as semistructured data and unstructured data such as social media. Users can analyze this information to gain insights into how the business is performing and what it should do next.\n" +
                "\n" +
                "According to CIO magazine: “Although business intelligence does not tell business users what to do or what will happen if they take a certain course, neither is BI only about generating reports. Rather, BI offers a way for people to examine data to understand trends and derive insights.”1\n" +
                "\n" +
                "Organizations can use the insights gained from BI and data analysis to improve business decisions, identify problems or issues, spot market trends and find new revenue or business opportunities.\n" +
                "\n" +
                "3D design of balls rolling on a track\n" +
                "The latest AI News + Insights \u2028\n" +
                "Discover expertly curated insights and news on AI, cloud and more in the weekly Think Newsletter. \n" +
                "\n" +
                "Subscribe today\n" +
                "Business intelligence versus business analytics\n" +
                "Business intelligence (BI) is descriptive, enabling better business decisions that are based on a foundation of current business data. Business analytics (BA) is then a subset of BI, with business analytics providing the prescriptive, forward-looking analysis. It is the umbrella of BI infrastructure that includes the tools for the identification and storage of the data for decision-making.\n" +
                "\n" +
                "BI might tell an organization how many new customers were acquired last month and whether order size was up or down for the month. As opposed to this, business analytics might predict which strategies, based on that data, would most benefit the organization. For example: What happens if we increase advertising spending to give new customers a special offer?";

        String text1 = "BI platforms traditionally rely on data warehouses for their baseline information. The strength of a data warehouse is that it aggregates data from multiple data sources into one central system to support business data analytics and reporting. BI presents the results to the user in the form of reports, charts and maps, which might be displayed through a dashboard.\n" +
                "\n" +
                "Data warehouses can include an online analytical processing (OLAP) engine to support multidimensional queries. For example: “What are the sales for our eastern region versus our western region this year, compared to last year?”\n" +
                "\n" +
                "OLAP provides powerful technology for data discovery, facilitating BI, complex analytic calculations and predictive analytics. One of the main benefits of OLAP is the consistency of its calculations that can improve product quality, customer interactions and business process.\n" +
                "\n" +
                "Data lakehouses are now also being used for BI. The benefit of a data lakehouse is that it seeks to resolve the core challenges across both data warehouses and data lakes to yield a more ideal data management solution for organizations. A lakehouse represents the next evolution of data management solutions.\n" +
                "\n" +
                "The steps taken in BI usually flow in this order:\n" +
                "\n" +
                "Data sources: Identify the data to be reviewed and analyzed, such as from a data warehouse or data lake, cloud, Hadoop, industry statistics, supply chain, CRM, inventory, pricing, sales, marketing or social media.\n" +
                "\n" +
                "Data collection: Gather and clean data from various sources. This data preparation might be manually gathering information in a spreadsheet or an automatic extract, transform and load (ETL) program.\n" +
                "\n" +
                "Analysis: Look for trends or unexpected results in the data. This might use data mining, data discovery or data modeling tools.\n" +
                "\n" +
                "Visualization: Create data visualizations, graphs and dashboards that use business intelligence tools such as Tableau, Cognos Analytics, Microsoft Excel or SAP. Ideally this visualization includes drill-down, drill-through, drill-up features to enable users to investigate various data levels.\n" +
                "\n" +
                "Action plan: Develop actionable insights based on analysis of historical data versus key performance indicators (KPIs). Actions might include more efficient processes, changes in marketing, fixing supply chain issues or adapting customer experience issues.\n" +
                "Some newer BI products can extract and load raw data directly by using technology such as Hadoop, but data warehouses often remain the data source of choice.";
        KeywordExtractor extractor = new KeywordExtractor();
        List<String> keywords = extractor.extractKeywords(text1, 5);

        System.out.println("Top 5 keywords:");
        for (String keyword : keywords) {
            System.out.println(keyword);
        }
    }
}