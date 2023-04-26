import {Button, Chip} from "@mui/material";
import DeleteRoundedIcon from "@mui/icons-material/DeleteRounded";
import DeleteIcon from '@mui/icons-material/Delete';

export const columnsAdvanced = [
    {
        title: "ID",
        field: "id",
        render: (rowData) => (
            <a href="https://www.google.com" target="_blank">{rowData.id}</a>
        ),
    },
    {
        title: "Name",
        field: "name",
        render: (rowData) => (
            <Chip
                size="small"
                label={rowData.name}
                sx={{marginRight: "6px", marginBottom: "6px", border: "1px solid lightgray", fontStyle: "italic"}}
            />
        ),
    },
    {title: "Price", field: "price"},
    {title: "Description", field: "description", sorting: false},
    {
        title: "Demo action",
        render: (rowData) => (
            <Button
                variant="contained"
                color="error"
                onClick={() => demoFunction(rowData)}
                startIcon={<DeleteIcon/>}
            >
                DELETE
            </Button>
        ),
    },
];

const demoFunction = (rowData) => {
    alert("You want to delete item: " + rowData.name);
}

export const sampleDataAdvanced = [
    {id: 1, name: "bread", price: 40, description: "This is a description text"},
    {id: 2, name: "milk", price: 20, description: "This is a description text"},
    {id: 3, name: "Iphone", price: 100, description: "This is a description text"},
    {id: 4, name: "hotdog", price: 15, description: "This is a description text"},
    {id: 5, name: "sticker", price: 20, description: "This is a description text"},
    {id: 6, name: "chocolate bar", price: 80.5, description: "This is a description text"},
    {id: 7, name: "Ipad", price: 200, description: "This is a description text"},
    {id: 8, name: "pencil", price: 4.25, description: "This is a description text"},
];

const demoFunction2 = () => {
    alert("You want to delete multiple items");
}
export const CommandBarComponent = () => (
    <>
        <Button
            startIcon={<DeleteRoundedIcon/>}
            onClick={() => demoFunction2()}
        >
            Batch Action
        </Button>
    </>
)