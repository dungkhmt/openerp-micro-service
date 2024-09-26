import NextAuth from "next-auth";
import GithubProvider from "next-auth/providers/github";
import GoogleProvider from "next-auth/providers/google";
import CredentialsProvider from "next-auth/providers/credentials";

const handler = NextAuth({
  providers: [
    GithubProvider({
      clientId: process.env.GITHUB_ID as string,
      clientSecret: process.env.GITHUB_SECRET as string,
    }),
    GoogleProvider({
      clientId: process.env.GOOGLE_CLIENT_ID as string,
      clientSecret: process.env.GOOGLE_CLIENT_SECRET as string,
    }),
    // CredentialsProvider({
    //   name: "Credentials",
    //   credentials: {
    //     // username: { label: "Username", type: "text", placeholder: "jsmith" },
    //     // password: { label: "Password", type: "password" },
    //   },
    //   async authorize(credentials, req) {
    //     const res = await fetch("/your/endpoint", {
    //       method: 'POST',
    //       body: JSON.stringify(credentials),
    //       headers: { "Content-Type": "application/json" }
    //     })
    //     const user = await res.json()
    //     if (res.ok && user) {
    //       return user
    //     }
    //     return null;
    //   }
    // }),
  ],
  // callbacks: {
  //   async jwt({ token, account, profile }) {
  //     // Persist the OAuth access_token and or the user id to the token right after signin
  //     if (account) {
  //       token.accessToken = account.access_token
  //       token.id = profile.id
  //     }
  //     return token
  //   }
  // }
});

export { handler as GET, handler as POST };
