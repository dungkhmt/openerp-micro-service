import { Autocomplete, TextField } from "@mui/material";

export const generalTableColumns = [
    {
        headerName: "Class Opened ID",
        field: "id",
        width: 170
    },
    {
        headerName: "Kỳ học",
        field: "semester",
        width: 170
    },
    {
        headerName: "Nhóm",
        field: "groupName",
        width: 120
    },
    {
        headerName: "SL thực",
        field: "quantity",
        width: 100
    },
    {
        headerName: "Loại lớp",
        field: "classType",
        width: 100
    },
    {
        headerName: "Mã học phần",
        field: "moduleCode",
        width: 100
    },
    {
        headerName: "Tên học phần",
        field: "moduleName",
        width: 150
    },
    {
        headerName: "Thời lượng",
        field: "mass",
        width: 100
    },
    {
        headerName: "SL MAX",
        field: "quantityMax",
        width: 100
    },
    {
        headerName: "Lớp học",
        field: "studyClass",
        width: 150
    },
    {
        headerName: "Trạng thái",
        field: "state",
        width: 100
    },
    {
        headerName: "Mã lớp",
        field: "classCode",
        width: 100
    },
    {
        headerName: "Kíp",
        field: "crew",
        width: 100
    },
    {
        headerName: "Đợt",
        field: "openBatch",
        width: 100
    },
    {
        headerName: "Khóa",
        field: "course",
        width: 100
    },
    {
        headerName: "Tiết BĐ",
        field: "startPeriod",
        width: 80,
    },
    {
        headerName: "Đổi tiết",
        field: "setPeriod",
        width: 100,
        renderCell: (params) => (
            <Autocomplete
                getOptionLabel={(option) => option.classPeriod}
                style={{ width: 100 }}
                renderInput={(params) => (
                    <TextField
                        {...params}
                        label=""
                        value={params.row ? params.row.classPeriod : null}
                    />
                )}
            />
        ),
    },
    {
        headerName: "Thứ",
        field: "weekday",
        width: 80,
    },
    {
        headerName: "Đổi thứ",
        field: "setWeekday",
        width: 100,
        renderCell: (params) => (
            <Autocomplete
                getOptionLabel={(option) => option.weekDay}
                style={{ width: 100 }}
                renderInput={(params) => (
                    <TextField
                        {...params}
                        label=""
                        value={params.row ? params.row.weekDay : null}
                    />
                )}
            />
        ),
    },
    {
        headerName: "Phòng",
        field: "classroom",
        width: 120,
    },
    {
        headerName: "Đổi phòng",
        field: "setClassroom",
        width: 150,
        renderCell: (params) => (
            <Autocomplete
                getOptionLabel={(option) => option.classroom}
                style={{ width: 150 }}
                renderInput={(params) => (
                    <TextField
                        {...params}
                        label=""
                        value={params.row ? params.row.classroom : null}
                    />
                )}
            />
        ),
    },
    {
        headerName: "Tách lớp",
        field: "isSeparateClass",
        width: 100,
        renderCell: (params) => (
            <input
                type="checkbox"
                checked={params.row.isSeparateClass}
                style={{ transform: "scale(1.5)" }}
            />
        ),
    },
    {
        headerName: "Tiết BĐ",
        field: "secondStartPeriod",
        width: 80,
    },
    {
        headerName: "Đổi tiết",
        field: "setSecondPeriod",
        width: 100,
        renderCell: (params) => (
            <Autocomplete
                getOptionLabel={(option) => option.classPeriod}
                style={{ width: 100 }}
                disabled={!params.row.isSeparateClass}
                renderInput={(params) => (
                    <TextField
                        {...params}
                        label=""
                        value={params.row ? params.row.classPeriod : null}
                    />
                )}
            />
        ),
    },
    {
        headerName: "Thứ",
        field: "secondWeekday",
        width: 80,
    },
    {
        headerName: "Đổi thứ",
        field: "setSecondWeekday",
        width: 100,
        renderCell: (params) => (
            <Autocomplete
                getOptionLabel={(option) => option.weekDay}
                style={{ width: 100 }}
                disabled={!params.row.isSeparateClass}
                renderInput={(params) => (
                    <TextField
                        {...params}
                        label=""
                        value={params.row ? params.row.weekDay : null}
                    />
                )}
            />
        ),
    },
    {
        headerName: "Phòng",
        field: "secondClassroom",
        width: 120,
    },
    {
        headerName: "Đổi phòng",
        field: "setSecondClassroom",
        width: 150,
        renderCell: (params) => (
            <Autocomplete
                getOptionLabel={(option) => option.classroom}
                style={{ width: 150 }}
                disabled={!params.row.isSeparateClass}
                renderInput={(params) => (
                    <TextField
                        {...params}
                        label=""
                        value={params.row ? params.row.classroom : null}
                    />
                )}
            />
        ),
    },
];