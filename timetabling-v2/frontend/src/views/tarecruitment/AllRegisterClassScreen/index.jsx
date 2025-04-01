import { useState, useEffect } from "react";
import {
  Button,
  TextField,
  Paper,
  Typography,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
} from "@mui/material";
import { useHistory } from "react-router-dom";
import { DataGrid } from "@mui/x-data-grid";
import styles from "./index.style";
import {
  useClasses,
  useRegisteredClasses,
  useSemesters,
  useCurrentSemester,
} from "services/useClassRegistrationData";

const DEFAULT_PAGINATION_MODEL = {
  page: 0,
  pageSize: 10,
};

const AllRegisterClassScreen = () => {
  const history = useHistory();
  const [search, setSearch] = useState("");
  const [semester, setSemester] = useState("");
  const [paginationModel, setPaginationModel] = useState(
    DEFAULT_PAGINATION_MODEL
  );

  const { data: currentSemesterData } = useCurrentSemester();
  const { data: semestersData } = useSemesters();
  const { data: classesData, isLoading } = useClasses(
    semester,
    paginationModel.page,
    paginationModel.pageSize,
    search
  );
  const { data: registeredClassData } = useRegisteredClasses(semester);

  useEffect(() => {
    if (currentSemesterData?.data) {
      setSemester(currentSemesterData.data);
    }
  }, [currentSemesterData]);

  const handleSearch = (e) => {
    setSearch(e.target.value);
    setPaginationModel({
      ...DEFAULT_PAGINATION_MODEL,
      page: 0,
    });
  };

  const handleRegister = (klass) => {
    history.push("/ta-recruitment/student/class-register/", {
      classId: klass.id,
    });
  };

  const handleChangeSemester = (event) => {
    setSemester(event.target.value);
  };

  const actionCell = (params) => {
    const rowData = params.row;
    const isRegistered = registeredClassData?.data?.some(
      (item) => item.id === rowData.id
    );
    return (
      <div>
        <Button
          variant="outlined"
          disabled={isRegistered}
          onClick={() => handleRegister(rowData)}
          style={styles.registeredButton}
        >
          {isRegistered ? "ĐÃ ĐĂNG KÝ" : "Đăng ký"}
        </Button>
      </div>
    );
  };

  const dataGridColumns = [
    {
      field: "id",
      headerName: "Mã lớp",
      align: "center",
      headerAlign: "center",
    },
    { field: "subjectId", headerName: "Mã môn học", flex: 1 },
    {
      field: "subjectName",
      headerName: "Tên môn học",
      flex: 1,
    },
    {
      field: "day",
      headerName: "Thời gian",
      flex: 1,
    },
    {
      headerName: "Hành động",
      flex: 1,
      align: "center",
      headerAlign: "center",
      renderCell: actionCell,
    },
  ];

  const dataGridRows =
    classesData?.data?.data?.map((klass) => ({
      id: klass.id,
      subjectId: klass.subjectId,
      subjectName: klass.subjectName,
      day: `Thứ ${klass.day}, tiết ${klass.startPeriod} - ${klass.endPeriod}`,
      actions: { rowData: klass },
    })) || [];

  return (
    <Paper elevation={3}>
      <div style={styles.tableToolBar}>
        <Typography variant="h4" style={styles.title}>
          Danh sách lớp học
        </Typography>

        <div style={styles.searchArea} className="w-[200px]">
          <FormControl style={styles.dropdown} size="small" >
            <InputLabel id="semester-label">Học kì</InputLabel>
            <Select
              labelId="semester-label"
              id="semester-select"
              value={semester}
              name="day"
              label="Học kì"
              onChange={handleChangeSemester}
              MenuProps={{ PaperProps: { sx: styles.selection } }}
            >
              {semestersData?.data?.map((sem, index) => (
                <MenuItem key={index} value={sem}>
                  {sem}
                </MenuItem>
              ))}
            </Select>
          </FormControl>

          <TextField
            style={styles.searchBox}
            variant="outlined"
            name="search"
            value={search}
            onChange={handleSearch}
            placeholder="Tìm kiếm"
          />
        </div>
      </div>

      <DataGrid
        loading={isLoading}
        rowHeight={60}
        sx={styles.table}
        rows={dataGridRows}
        columns={dataGridColumns}
        rowCount={classesData?.data?.totalElement || 0}
        pagination
        paginationMode="server"
        paginationModel={paginationModel}
        onPaginationModelChange={setPaginationModel}
        pageSizeOptions={[10, 20, 50]}
        checkboxSelection={false}
        disableRowSelectionOnClick
      />
    </Paper>
  );
};

export default AllRegisterClassScreen;
