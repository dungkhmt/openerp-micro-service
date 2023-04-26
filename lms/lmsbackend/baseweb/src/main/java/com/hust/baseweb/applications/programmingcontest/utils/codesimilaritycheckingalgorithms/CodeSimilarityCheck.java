package com.hust.baseweb.applications.programmingcontest.utils.codesimilaritycheckingalgorithms;

public class CodeSimilarityCheck {
    static int[][] D;
    static int min(int x, int y, int z)
    {
        if (x <= y && x <= z)
            return x;
        if (y <= x && y <= z)
            return y;
        else
            return z;
    }
    static int editDistance(String str1, String str2, int m, int n){
        if (m == 0) {D[m][n] = n;        return n;}
        if (n == 0)  {D[m][n] = m;         return m;}

        if(D[m][n] == -1) {
            if (str1.charAt(m - 1) == str2.charAt(n - 1)) {
                D[m][n] = editDistance(str1, str2, m - 1, n - 1);
                return D[m][n];
            }

            D[m][n] = 1 + min(
                editDistance(str1, str2, m, n - 1),
                editDistance(str1, str2, m - 1, n),
                editDistance(str1, str2, m - 1,
                             n - 1)
            );
            return D[m][n];
        }
        return D[m][n];
    }
    public static double check(String code1, String code2){
        //if(code1.equals(code2)) return 1;
        code1 = code1.replaceAll(" ","");
        code1 = code1.replaceAll("\t","");
        code1 = code1.replaceAll("\n","");

        code2 = code2.replaceAll(" ","");
        code2 = code2.replaceAll("\t","");
        code2 = code2.replaceAll("\n","");

        System.out.println("check, after std, code1 = " + code1 + " code2 = " + code2);

        int n1 = code1.length();
        int n2 = code2.length();
        D = new int[n1+1][n2+1];
        for(int i = 0; i <= n1; i++)
            for(int j = 0; j <= n2; j++)
                D[i][j] = -1;

        int d = editDistance(code1, code2, n1,n2);
        if(d >= n1 || d >= n2)  return 0;

        return ((n1-d)*1.0/n1 + (n2-d)*1.0/n2)*0.5;

    }

}
