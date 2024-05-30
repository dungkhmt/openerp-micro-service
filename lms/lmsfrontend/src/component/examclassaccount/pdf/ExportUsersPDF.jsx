import {
  Document,
  Font,
  Image,
  Page,
  StyleSheet,
  Text,
  View,
} from "@react-pdf/renderer";
import parse from "html-react-parser";
// import Footer from "component/education/quiztest/template/Footer";
import { createState } from "@hookstate/core";

export const subPageTotalPagesState = createState({
  fulfilled: false,
  totalPages: [],
});

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
  // fonts: [
  //   {
  //     src: "https://cdnjs.cloudflare.com/ajax/libs/inter-ui/3.19.3/Inter (web)/Inter-Light.woff",
  //     fontWeight: "normal",
  //   },
  //   {
  //     src: "https://cdnjs.cloudflare.com/ajax/libs/inter-ui/3.19.3/Inter (web)/Inter-Regular.woff",
  //     fontWeight: "bold",
  //   },
  // ],
});

// Create styles
const styles = StyleSheet.create({
  page: {
    fontFamily: "Inter",
    fontSize: "12px",
    padding: 40,
    flexGrow: 1,
  },
  question: {
    marginTop: "20px",
    marginBottom: "4px",
  },
  answer: {
    marginTop: "4px",
    marginBottom: "4px",
    flexGrow: 1,
    flexShrink: 1,
    display: "inline",
  },
  bold: {
    fontWeight: "bold",
  },
  textLine: {
    marginBottom: "4px",
  },
  imageContainer: {
    display: "flex",
    alignItems: "flex-start",
    maxHeight: "300px",
  },
  ulChild: {
    paddingLeft: 20,
    marginTop: "4px",
    marginBottom: "4px",
  },
  footer: {
    display: "flex",
    alignItems: "flex-end",
    justifyContent: "center",
    position: "absolute",
    fontFamily: "Inter",
    // fontSize: config.normalFontSize,
    bottom: 15,
    left: 40,
    right: 20,
    // color: config.grayColor,
    borderTopWidth: 1,
    // borderTopColor: grey[400],
    paddingTop: 5,
  },
});

const checkBoxBase64 =
  "iVBORw0KGgoAAAANSUhEUgAAACQAAAAkCAYAAADhAJiYAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAACoSURBVFhH7dexDcMgFEXRn6zADsxFh0TFFOxAx1zsQE3pBERK632chuKdBhkJ6wpZNn5dX3KQ9xqPwSCEQQiDkOOCVC/GWquUUqS1tmb2GGPEOSfW2jVzT7VD/8QMY+24h4Zqh7z3c8w5z3HXzno+1AiDEAYhDEIYhDAIOS5I9bWPMUrvfV09M85EKaV1dU+1QyGEecOnfgc0Df5KIwxCGIQwCDksSOQD5Zw1Tp9gAfMAAAAASUVORK5CYII=";

const Footer = ({ pageNumber, totalPages }) => (
  <View style={styles.footer} fixed>
    <Text fixed>
      Trang {pageNumber} của {totalPages}
    </Text>
  </View>
);

// Create Document Component
function ExportUsersPDF({ data }) {
  return (
    <Document>
      {data?.map(
        (
          {
            realUserLoginId,
            studentCode,
            fullname,
            randomUserLoginId,
            password,
          },
          idx,
          arr
        ) => (
          <>
            <Page size="A4" style={styles.page} wrap>
              <View>
                {/* <Text style={styles.textLine}>Mã số: {realUserLoginId}</Text> */}
                <Text style={styles.textLine}>
                  Mã số sinh viên: {studentCode}
                </Text>
                <Text style={styles.textLine}>Họ và tên: {fullname}</Text>
                <Text style={styles.textLine}>
                  Login ID: {randomUserLoginId}
                </Text>
                <Text style={styles.textLine}>
                  Mật khẩu đăng nhập: {password}
                </Text>
              </View>
              <Footer pageNumber={idx + 1} totalPages={arr.length} />
            </Page>
          </>
        )
      )}
    </Document>
  );
}

export default ExportUsersPDF;
