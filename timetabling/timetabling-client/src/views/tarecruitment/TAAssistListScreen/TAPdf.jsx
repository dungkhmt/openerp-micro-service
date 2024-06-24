import {
  Document,
  Font,
  Page,
  Text,
  View,
  StyleSheet,
} from "@react-pdf/renderer";

const TAPdf = ({ data }) => {
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

  const styles = StyleSheet.create({
    page: {
      fontFamily: "Inter",
      fontSize: 12,
      padding: 40,
      flexGrow: 1,
    },
    header: {
      textAlign: "center",
      fontWeight: "bold",
      fontSize: 25,
      marginBottom: 20,
    },
    secondHeader: {
      fontWeight: "bold",
      textDecoration: "underline",
      fontSize: 16,
      marginTop: 10,
    },
    spacing: {
      marginTop: 10,
      marginBottom: 10,
    },
    table: {
      width: "100%",
    },
    row: {
      display: "flex",
      flexDirection: "row",
      borderTop: "1px solid #EEE",
      paddingTop: 8,
      paddingBottom: 8,
    },
    headerRow: {
      borderTop: "none",
      fontWeight: "bold",
    },
    col1: {
      width: "10%",
      fontSize: 12,
    },
    col2: {
      width: "15%",
      fontSize: 12,
    },
    col3: {
      width: "15%",
      fontSize: 12,
    },
    col4: {
      width: "25%",
      fontSize: 12,
    },
    col5: {
      width: "20%",
      fontSize: 12,
    },
    col6: {
      width: "25%",
      fontSize: 12,
    },
    signPart: {
      display: "flex",
      flexDirection: "row",
      justifyContent: "flex-end",
      marginTop: 20,
      fontSize: 16,
      alignSelf: "flex-end",
    },
  });

  return (
    <Document>
      <Page style={styles.page} size="A4">
        <Text style={styles.header}>HỢP ĐỒNG TRỢ GIẢNG</Text>
        <Text>Hôm nay, chúng tôi gồm:</Text>
        <Text style={styles.secondHeader}>Bên A: Người sử dụng lao động</Text>
        <Text>Trường Đại học Công nghệ thông tin và Truyền thông</Text>
        <Text>
          Địa chỉ: Số 1 Đại Cồ Việt, quận Hai Bà Trưng, thành phố Hà Nội
        </Text>
        <Text style={styles.secondHeader}>Bên B: Người lao động</Text>
        <Text>Họ và tên: {data?.name}</Text>
        <Text>Mã số sinh viên: {data?.mssv}</Text>
        <Text>Số điện thoại liên hệ: {data?.phoneNumber}</Text>
        <Text>Email: {data?.email}</Text>
        <Text style={styles.spacing}>Cùng thỏa thuận trợ giảng cho kỳ học</Text>
        <View style={styles.table}>
          <View style={[styles.row, styles.headerRow]}>
            <Text style={styles.col2}>Mã lớp</Text>
            <Text style={styles.col3}>Mã môn học</Text>
            <Text style={styles.col4}>Tên môn học</Text>
            <Text style={styles.col5}>Phòng học</Text>
            <Text style={styles.col6}>Thời gian</Text>
          </View>

          <View style={styles.row}>
            <Text style={styles.col2}>{data?.classId}</Text>
            <Text style={styles.col3}>{data?.subjectId}</Text>
            <Text style={styles.col4}>{data?.subjectName}</Text>
            <Text style={styles.col5}>{data?.classRoom}</Text>
            <Text style={styles.col6}>{data?.time}</Text>
          </View>
        </View>
        <Text style={styles.signPart}>Người lao động</Text>
      </Page>
    </Document>
  );
};

export default TAPdf;
