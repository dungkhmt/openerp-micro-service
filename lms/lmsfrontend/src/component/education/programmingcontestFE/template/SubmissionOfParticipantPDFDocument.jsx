import {Document, Font, Page, StyleSheet, Text, View,} from "@react-pdf/renderer";
import Footer from "./Footer";

Font.register({
  family: "Inter",
  fonts: [
    {
      src: "https://cdnjs.cloudflare.com/ajax/libs/ink/3.1.10/fonts/Roboto/roboto-light-webfont.ttf",
      fontWeight: "normal",
    },
    {
      src: "https://cdnjs.cloudflare.com/ajax/libs/ink/3.1.10/fonts/Roboto/roboto-regular-webfont.ttf",
      fontWeight: "bold",
    },
  ],
});

Font.register({
  family: 'Ubuntu',
  fonts: [
    {
      src: 'https://fonts.gstatic.com/s/questrial/v13/QdVUSTchPBm7nuUeVf7EuStkm20oJA.ttf',
    },
    {
      src: 'https://fonts.gstatic.com/s/questrial/v13/QdVUSTchPBm7nuUeVf7EuStkm20oJA.ttf',
      fontWeight: 'bold',
    },
    {
      src: 'https://fonts.gstatic.com/s/questrial/v13/QdVUSTchPBm7nuUeVf7EuStkm20oJA.ttf',
      fontWeight: 'normal',
      fontStyle: 'italic',
    },
  ],
});

// Create styles
const styles = StyleSheet.create({
  page: {
    fontFamily: "Inter",
    fontSize: "12px",
    padding: 40,
    flexGrow: 1,
  },
  sourceCodeContainer: {
    marginTop: "10px",
    marginLeft: "12px",
    marginBottom: "4px",
  },
  sourceCode: {
    fontFamily: "Ubuntu",
    flexGrow: 1,
    flexShrink: 1,
    display: "inline",
    fontSize: "10px",
  },
  textLine: {
    marginBottom: "4px",
  },
  textLineBold: {
    marginBottom: "4px",
    fontWeight: "bold",
  },
  textLineUnderline: {
    marginBottom: "4px",
    textDecoration: "underline",
  },
});

// Create Document Component
function SubmissionOfParticipantPDFDocument({ data }) {
  return (
    <Document>
      {data?.map(
        ({
          fullName,
          point,
          problemName,
          status,
          submissionSourceCode,
          testCasePassed,
        }) => (
          <Page size="A4" style={styles.page} wrap>
            <View>
              <Text style={styles.textLineBold}>PROBLEM: {problemName}</Text>
              <Text style={styles.textLine}>Full name: {fullName}</Text>
              <Text style={styles.textLine}>Point: {point}</Text>
              <Text style={styles.textLine}>Status: {status}</Text>
              <Text style={styles.textLine}>
                Test cases passed: {testCasePassed}
              </Text>
              <View style={styles.sourceCodeContainer}>
                <Text style={styles.textLineUnderline}>Source CODE:</Text>
                <Text style={styles.sourceCode}>{submissionSourceCode}</Text>
              </View>
            </View>
            <Footer />
          </Page>
        )
      )}
    </Document>
  );
}

export default SubmissionOfParticipantPDFDocument;
