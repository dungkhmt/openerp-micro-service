import React from "react";
import {StandardTable} from "../../components";
import {columnsBasic, sampleDataBasic} from "./data/basic";
import {columnsAdvanced, CommandBarComponent, sampleDataAdvanced} from "./data/advanced";

export default {
    title: 'Components/StandardTable',
    component: StandardTable,
    tags: ['autodocs'],
};


export const SimpleTable = {
    args: {
        title: 'Basic Table',
        columns: columnsBasic,
        data: sampleDataBasic,
        options: {
            selection: false,
            pageSize: 5,
            search: true,
            sorting: true,
        }
    }
}

export const AdvancedTable = {
    args: {
        title: 'Advanced Table',
        columns: columnsAdvanced,
        data: sampleDataAdvanced,
        options: {
            selection: true,
            pageSize: 5,
            search: true,
            sorting: true,
        },
        commandBarComponents: <CommandBarComponent/>
    }
}
