import StandardTable from "../table/StandardTable";
import PropTypes from "prop-types";

const MemberTable = ({ members }) => {
  // Command delete button
  const cellStyles = { headerStyle: { padding: 8 }, cellStyle: { padding: 8 } };

  const columns = [
    { title: "Tên thành viên", field: "fullName", ...cellStyles },
    { title: "Tên đăng nhập", field: "id", ...cellStyles },
    // { title: "Thời gian thêm vào dự án", field: "", ...cellStyles },
  ];
  return (
    <>
      <StandardTable
        title=""
        hideCommandBar
        columns={columns}
        data={members}
        onSelectionChange={(selectedRows) => setSelectedRows(selectedRows)}
        options={{ selection: false, pageSize: 10 }}
      />
    </>
  );
};

MemberTable.propTypes = {
  members: PropTypes.array,
};

export default MemberTable;
