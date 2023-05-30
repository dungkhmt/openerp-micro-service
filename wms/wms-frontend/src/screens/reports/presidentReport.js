const { default: ProductCategoryMonthlyReport } = require("./productCategoryMonthlyReport")
const { default: RevuenueReport } = require("./revenueReport")

const PresidentReport = () => {
  return <div>
    <RevuenueReport />
    <ProductCategoryMonthlyReport />
  </div>
}

export default PresidentReport;