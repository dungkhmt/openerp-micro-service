import {grey} from "@mui/material/colors";
import {StyleSheet, Text, View} from "@react-pdf/renderer";

const styles = StyleSheet.create({
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
    borderTopColor: grey[400],
    paddingTop: 5,
  },
});

const Footer = () => (
  <View style={styles.footer} fixed>
    <Text
      fixed
      render={({ pageNumber, totalPages, subPageNumber, subPageTotalPages }) =>
        `Trang ${subPageNumber} của ${subPageTotalPages}`
      }
    />
  </View>
);

export default Footer;
