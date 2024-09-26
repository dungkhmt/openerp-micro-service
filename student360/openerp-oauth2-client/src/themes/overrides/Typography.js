export default function Typography(theme) {
  return {
    MuiTypography: {
      variantMapping: {
        contentXlSemiBold: 'div',
        contentXlRegular: 'div',
        contentLBold: 'div',
        contentMBold: 'div',
        contentMLink: 'div',
        contentMRegular: 'div',
        contentSBold: 'div',
        contentSLink: 'div',
        contentSRegular: 'div',
        contentXsBold: 'div',
        contentXsSemiBold: 'div',
        contentXsLink: 'div',
        contentXsRegular: 'div',
        content2xsBold: 'span',
        content2xsRegular: 'span',
        content3xsRegular: 'small',
        break: 'br',
      },
      styleOverrides: {
        paragraph: {
          marginBottom: theme.spacing(2),
        },
        gutterBottom: {
          marginBottom: theme.spacing(1),
        },
      },
    },
  };
}
