// Next.js request middleware
import { NextResponse, type NextRequest } from 'next/server';

export function middleware(request: NextRequest) {

  const accessToken = request.cookies.get('accessToken')
  if (!accessToken) {
    // return NextResponse.redirect('/login')
    return Response.redirect(new URL('/login', request.url));
  }
  return NextResponse.next();
}

export const config = {
  matcher: ['/admin/:path*'],
}
