"use client";
import React from "react";
import { SessionProvider as SessionAuth } from "next-auth/react";

export interface SessionProviderProps {
  children: React.ReactNode;
}

export const SessionProvider: React.FC<SessionProviderProps> = ({
  children,
}) => {
  return <SessionAuth>{children}</SessionAuth>;
};
