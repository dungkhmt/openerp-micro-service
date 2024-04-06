
import * as React from "react";

export interface Props {
    children: React.ReactNode;

}

export default function WithoutLayoutGroup({
    children,
}: Readonly<{
    children: React.ReactNode;
}>) {
    return (
        <>
            {children}
        </>
    );


}
