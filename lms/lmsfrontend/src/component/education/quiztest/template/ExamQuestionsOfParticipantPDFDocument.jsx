import {Document, Font, Image, Page, StyleSheet, Text, View,} from "@react-pdf/renderer";
import parse from "html-react-parser";
import Footer from "./Footer";
import {createState} from "@hookstate/core";

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
});

const checkBoxBase64 =
  "iVBORw0KGgoAAAANSUhEUgAAACQAAAAkCAYAAADhAJiYAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAACoSURBVFhH7dexDcMgFEXRn6zADsxFh0TFFOxAx1zsQE3pBERK632chuKdBhkJ6wpZNn5dX3KQ9xqPwSCEQQiDkOOCVC/GWquUUqS1tmb2GGPEOSfW2jVzT7VD/8QMY+24h4Zqh7z3c8w5z3HXzno+1AiDEAYhDEIYhDAIOS5I9bWPMUrvfV09M85EKaV1dU+1QyGEecOnfgc0Df5KIwxCGIQwCDksSOQD5Zw1Tp9gAfMAAAAASUVORK5CYII=";

// Create Document Component
function ExamQuestionsOfParticipantPDFDocument({ data }) {
  return (
    <Document>
      {data?.map(
        (
          {
            userDetail,
            testName,
            scheduleDatetime,
            courseName,
            duration,
            quizGroupId,
            groupCode,
            viewTypeId,
            listQuestion,
          },
          idx
        ) => (
          <>
            <Page size="A4" style={styles.page} wrap>
              <View>
                <Text style={styles.textLine}>Quiz test: {testName}</Text>
                <Text style={styles.textLine}>Code: {groupCode}</Text>
                <Text style={styles.textLine}>Học phần: {courseName}</Text>
                <Text style={styles.textLine}>MSSV: {userDetail.id}</Text>
                <Text style={styles.textLine}>
                  FullName: {userDetail?.fullName}
                </Text>
                <Text style={styles.textLine}>
                  Start Time: {scheduleDatetime}
                </Text>
                <Text style={styles.textLine}>
                  Duration: {duration} minutes{" "}
                </Text>

                {/* Questions */}
                {listQuestion?.map((q, qIndex) => (
                  <View key={q.questionId} style={styles.question}>
                    {parse(q.statement).map((ele, eIndex) => {
                      if (eIndex > 0) {
                        if (ele.type === "ul") {
                          return (
                            <View
                              style={{
                                display: "flex",
                                flexGrow: 1,
                                flexShrink: 1,
                              }}
                            >
                              {ele.props.children.map((ulChild) => {
                                if (ulChild.type === "li")
                                  return (
                                    <Text style={styles.ulChild}>
                                      &#8226; {ulChild.props.children}
                                    </Text>
                                  );
                                else if (ulChild !== "\n")
                                  return (
                                    <Text style={styles.ulChild}>
                                      {ulChild}
                                    </Text>
                                  );
                              })}
                            </View>
                          );
                        } else if (ele !== "\n") {
                          return <Text>{ele}</Text>;
                        }
                      } else {
                        return (
                          <Text>
                            <Text style={styles.bold}>Question {qIndex + 1}. </Text>
                            {ele}
                          </Text>
                        );
                      }
                    })}

                    {/* Question images */}
                    {q.attachment?.length > 0 &&
                      q.attachment.map((imageBase64, index) => (
                        <View key={index} style={styles.imageContainer}>
                          <Image
                            src={`data:application/pdf;base64,${imageBase64}`}
                            style={{
                              objectFit: "scale-down",
                            }}
                          />
                        </View>
                      ))}

                    {/* Question answers */}
                    {q.quizChoiceAnswerList.map((ans) => (
                      <View
                        key={ans.choiceAnswerId}
                        style={{
                          display: "flex",
                          flexDirection: "row",
                          alignItems: "center",
                          justifyContent: "center",
                        }}
                      >
                        <Image
                          src={`data:application/pdf;base64,${checkBoxBase64}`}
                          style={{
                            width: "24px",
                            height: "24px",
                          }}
                        />
                        <Text style={styles.answer}>
                          {parse(ans.choiceAnswerContent)}
                        </Text>
                      </View>
                    ))}
                  </View>
                ))}
              </View>
              <Footer />

              {/* Calculate value for subPageTotalPagesState[idx] */}
              <Text
                render={({
                  pageNumber,
                  totalPages,
                  subPageNumber,
                  subPageTotalPages,
                }) => {
                  if (subPageTotalPages !== undefined) {
                    subPageTotalPagesState.totalPages.merge({
                      [idx]: (4 - (subPageTotalPages % 4)) % 4,
                    });
                  }

                  if (idx === data.length - 1) {
                    subPageTotalPagesState.merge({ fulfilled: true });
                  }

                  return null;
                }}
              />
            </Page>
            {/* Blank pages */}
            {subPageTotalPagesState.totalPages[idx].get() > 0 &&
              Array.from(
                new Array(subPageTotalPagesState.totalPages[idx].get())
              ).map((_) => <Page size="A4" />)}
          </>
        )
      )}
    </Document>
  );
}

export default ExamQuestionsOfParticipantPDFDocument;
