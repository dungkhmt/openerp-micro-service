package localsearch.domainspecific.vehiclerouting.vrp.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * @author: Nguyen Thanh Hoang (thnbk56@gmail.com)
 * date: 14/4/2015
 */
// 
public class ScannerInput {

    BufferedReader br = null;
    StringTokenizer st = null;

    public ScannerInput(InputStream in) {
        br = new BufferedReader(new InputStreamReader(in));
    }

    public ScannerInput(String s) {
        try {
            br = new BufferedReader(new FileReader(s));
        } catch (FileNotFoundException ex) {
            //System.out.println("ko ton tai file" + s);
        }
    }

    public String nextToken() {
        while (st == null || !st.hasMoreElements()) {
            try {
                st = new StringTokenizer(br.readLine(), " :\t");
            } catch (IOException ex) {
                Logger.getLogger(ScannerInput.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return st.nextToken();
    }

    public int readInt() {
        return Integer.parseInt(nextToken());
    }

    public long readLong() {
        return Long.parseLong(nextToken());
    }

    public double readDouble() {
        return Double.parseDouble(nextToken());
    }

    public String readLine() {
        try {
            return br.readLine();
        } catch (IOException ex) {
            Logger.getLogger(ScannerInput.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String readString() {
        return nextToken();
    }

    public void close() {
        try {
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(ScannerInput.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
