"use client"
import { useEffect, useState } from 'react';
import { redirect } from 'next/navigation';
import Cookies from 'js-cookie';
import { jwtDecode } from "jwt-decode";

export default function OAuth2Callback() {

    useEffect(() => {
        const urlParams = new URLSearchParams(window.location.search);
        const token = urlParams.get('token');
        const refreshToken = urlParams.get('refreshToken');

        if (token) {
            console.log('token', token);
            localStorage.setItem('accessToken', token);
            Cookies.set('accessToken', token); // Set accessToken in cookies

            // Decode the token and store the information in cookies
            const decodedToken = jwtDecode(token) as { [key: string]: any };
            console.log('decodedToken', decodedToken);
            for (const property in decodedToken) {
                Cookies.set(property, decodedToken[property]);
            }

            if (refreshToken) {
                localStorage.setItem('refreshToken', refreshToken);
                Cookies.set('refreshToken', refreshToken); // Set refreshToken in cookies
                Cookies.set('refreshToken1', refreshToken); // Set refreshToken1 in cookies
            }

            const role = decodedToken.role.toLowerCase();
            redirect(`/${role}`); // Redirect to home page
        }
    }, []);
}