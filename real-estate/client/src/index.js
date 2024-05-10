import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import {createTheme, MantineProvider} from "@mantine/core";
import '@mantine/core/styles.css';
import {store} from "../src/store/index"
import {PersistGate} from "redux-persist/integration/react";
import {persistStore} from "redux-persist";
import {Provider} from "react-redux";

const root = ReactDOM.createRoot(document.getElementById('root'));

const theme = createTheme({
    /** Your theme override here */
});

const myPersistor = persistStore(store)

root.render(
    // <BrowserRouter>
    //     <App />
    // </BrowserRouter>
    <Provider store={store}>
        <PersistGate persistor={myPersistor}>
            <MantineProvider theme={theme}>
                <React.StrictMode>
                    <App/>
                </React.StrictMode>,
            </MantineProvider>
        </PersistGate>
    </Provider>
)
;

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
// reportWebVitals();
