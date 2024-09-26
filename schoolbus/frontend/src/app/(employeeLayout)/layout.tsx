"use client";
import * as React from "react";
import { ThemeProviderProps } from "next-themes/dist/types";
import { LayoutEmployee } from "@/components/layout/layout-employee";


export interface ProvidersProps {
    children: React.ReactNode;
}

export default function EmployeeWithLayoutGroup({ children }: ProvidersProps) {

    return (
        <LayoutEmployee>
            {children}
        </LayoutEmployee>
    );

}
