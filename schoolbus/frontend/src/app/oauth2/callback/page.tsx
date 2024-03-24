"use client"
import { useEffect, useState } from 'react';
import {redirect} from 'next/navigation';
import Cookies from 'js-cookie';
export default function OAuth2Callback() {

    useEffect(() => {
        const urlParams = new URLSearchParams(window.location.search);
        const token = urlParams.get('token');
        const refreshToken = urlParams.get('refreshToken');

        if (token) {
            console.log('token', token);
            localStorage.setItem('accessToken', token);
            Cookies.set('accessToken', token); // Set accessToken in cookies
            if (refreshToken) {
                localStorage.setItem('refreshToken', refreshToken);
                Cookies.set('refreshToken', refreshToken); // Set refreshToken in cookies
            }

            redirect('/'); // Redirect to home page
        }
    }, []);
}