package com.hust.baseweb.applications.education.teacherclassassignment.utils;

public class MathUtils {

    /**
     * To find LCM
     *
     * @param num1
     * @param num2
     * @return
     */
    public static int gcd(int num1, int num2) {
        if (num1 == 0 || num2 == 0) {
            return num1 + num2;
        } else {
            int absNumber1 = Math.abs(num1);
            int absNumber2 = Math.abs(num2);
            int biggerValue = Math.max(absNumber1, absNumber2);
            int smallerValue = Math.min(absNumber1, absNumber2);
            return gcd(biggerValue % smallerValue, smallerValue);
        }
    }

    /**
     * Find LCM of array. lcm(a,b) = (a*b/gcd(a,b))
     *
     * @return
     */
    public static int lcm(int[] array, int index) {
        if (index == array.length - 1) {
            return array[index];
        }

        int a = array[index];
        int b = lcm(array, index + 1);
        return (a * b / gcd(a, b)); //
    }

}
