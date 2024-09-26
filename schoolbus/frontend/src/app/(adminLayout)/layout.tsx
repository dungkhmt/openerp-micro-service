"use client";
import * as React from "react";
import { ThemeProviderProps } from "next-themes/dist/types";
import { Layout } from "@/components/layout/layout";


export interface AdminProvidersProps {
    children: React.ReactNode;
}

export default function AdminWithLayoutGroup({ children }: AdminProvidersProps) {

    return (
        <Layout>
            {children}
        </Layout>
    );

}
