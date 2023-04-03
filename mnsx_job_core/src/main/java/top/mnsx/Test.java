package top.mnsx;

import java.math.BigInteger;
import java.util.*;

/**
 * @Author Mnsx_x xx1527030652@gmail.com
 */
public class Test {
    public static void main(String[] args) {
        int cnt = 0;
        for (int i = 2022; i <= 2022222022; i++) {
            if (isPalindromeAndMonotonous(i)) {
                cnt++;
            }
        }
        System.out.println(cnt);

    }

    public static boolean isPalindromeAndMonotonous(int num) {
        if (num != 0 && num % 10 == 0 || num < 0) {
            return false;
        }
        int reverseNum = 0;
        while (num > reverseNum) {
            int b = num % 10;
            if (reverseNum % 10 > b) {
                return false;
            }
            reverseNum = reverseNum * 10 + b;
            num /= 10;
        }
        return reverseNum == num || reverseNum / 10 == num;
    }
}
