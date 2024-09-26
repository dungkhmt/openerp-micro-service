"use client";
import React from "react";
import { useSession, signIn, signOut } from "next-auth/react";
import Image from "next/image";
import { Button, ButtonGroup } from "@nextui-org/react";

export const Auth: React.FC = () => {
  // extracting data from usesession as session
  const { data: session } = useSession();

  // checking if sessions exists
  if (session) {
    // rendering components for logged in users
    return (
      <section>
        <div className="w-full h-screen flex flex-col justify-center items-center">
          <div className="w-44 h-44 relative mb-4">
            <Image
              src={session.user?.image as string}
              fill
              alt=""
              className="object-cover rounded-full"
            />
          </div>
          <p className="text-2xl mb-2">
            Welcome <span className="font-bold">{session.user?.name}</span>.
            Signed In As
          </p>
          <p className="font-bold mb-4">{session.user?.email}</p>
          <button
            className="bg-red-600 py-2 px-6 rounded-md"
            onClick={() => signOut()}
          >
            Sign out
          </button>
        </div>
      </section>
    );
  }

  // rendering components for not logged in users
  return (
    <section>
      <div className="w-full h-screen flex flex-col justify-center items-center">
        <p className="text-2xl mb-2">Not Signed In</p>
        <button
          className="bg-blue-600 py-2 px-6 rounded-md mb-2"
          onClick={() => signIn("google")}
        >
          Sign in with google
        </button>
        <button
          className="bg-none border-gray-300 border py-2 px-6 rounded-md mb-2"
          onClick={() => signIn("github")}
        >
          Sign in with github
        </button>
      </div>
    </section>
  );
};
