"use client";
import * as React from "react";
import { ThemeProviderProps } from "next-themes/dist/types";
import { Layout } from "@/components/layout/layout";


export interface ProvidersProps {
    children: React.ReactNode;
    themeProps?: ThemeProviderProps;
}

export default function WithLayoutGroup({ children, themeProps }: ProvidersProps) {

    return (
        <Layout>
            {children}
        </Layout>
    );

}
