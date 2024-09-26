"use client";

import * as React from "react";
import { Auth } from "@/components";
import { redirect } from 'next/navigation'

export default function Home() {
    return (
      <main className="overflow-hidden">
        <Auth />
      </main>
    );

}
