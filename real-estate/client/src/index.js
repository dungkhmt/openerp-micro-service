import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import { BrowserRouter } from 'react-router-dom'
import {createTheme, MantineProvider} from "@mantine/core";
import '@mantine/core/styles.css';

const root = ReactDOM.createRoot(document.getElementById('root'));

const theme = createTheme({
    /** Your theme override here */
});
root.render(
    // <BrowserRouter>
    //     <App />
    // </BrowserRouter>
    <MantineProvider theme={theme}>
        <React.StrictMode>
            <App />
        </React.StrictMode>,
    </MantineProvider>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
// reportWebVitals();
