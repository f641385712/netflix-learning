package com.yourbatman.hystrix;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class SimpleLeapWindow {

    public static void main(String[] args) {
        calNormal(new int[]{-1, 4, 7, -3, 8, 5, -2, 6}, 2);
        System.out.println("-------------");
        calBySlidingWindow(new int[]{-1, 4, 7, -3, 8, 5, -2, 6}, 2);
    }

    /**
     * 遍历所有子数组，求和并比较
     * 嵌套循环 时间复杂度：O(n*k)
     */
    public static void calNormal(int[] array, int k) {
        if (array.length == 0 || k <= 0 || k > array.length) {// 非法参数不处理
            return;
        }

        int index = 0;// 记录最大子数组第1个元素的索引，目前是0
        int maxSum = 0;// 记录最大子数组和，目前是从左开始第1个子数组
        for (int i = 0; i < k; i++) {
            maxSum += array[i];
        }

        // 当前maxSum是第一个数组的值，下面将【所有的子数组】相加比较
        // 遍历所有子数组，求和并比较（因为第一个数组已经计算了，所以此处角标从1开始即可）
        for (int i = 1; i <= array.length - k; i++) {
            int curSum = 0;
            for (int j = 0; j < k; j++) {// 计算当前子数组和
                curSum += array[i + j];
            }

            // 如果大于最大和，则记录maxSum为当前值，且记录index为i
            if (curSum > maxSum) {
                maxSum = curSum;
                index = i;
            }
        }

        /**打印结果*/
        System.out.print(maxSum + " // ");// 打印最大和
        System.out.print(array[index]);// 先打印第1个值
        for (int i = 1; i < k; i++) {
            int value = array[i + index];
            System.out.print(value >= 0 ? ("+" + value) : value);// 非负数前面打印+号
        }
        System.out.println();
    }

    /**
     * 窗口向右滑动，通过减失效值加最新值求和并比较
     * 单层循环 时间复杂度：O(n)
     */
    public static void calBySlidingWindow(int[] array, int k) {
        if (array.length == 0 || k <= 0 || k > array.length) {// 非法参数不处理
            return;
        }

        // 同上
        int index = 0;
        int maxSum = 0;
        for (int i = 0; i < k; i++) {
            maxSum += array[i];
        }

        int curWindowSum = maxSum;
        // 从下个元素开始，即窗口向右滑动
        for (int i = 1; i <= array.length - k; i++) {
            // 减去失效值，加上最新值（窗口内元素固定嘛~这就是限流的思想）
            curWindowSum = curWindowSum - array[i - 1] + array[k + i - 1];
            if (curWindowSum > maxSum) {// 如果大于最大和，则记录
                maxSum = curWindowSum;
                index = i;
            }
        }

        /**打印结果*/
        System.out.print(maxSum + " // ");// 打印最大和
        System.out.print(array[index]);// 先打印第1个值
        for (int i = 1; i < k; i++) {
            int value = array[i + index];
            System.out.print(value >= 0 ? ("+" + value) : value);// 非负数前面打印+号
        }
        System.out.println();
    }


    // ===========================================

    @Test
    public void fun1() {

        String s = "abcabcbb"; // 结果：abc
        System.out.println(subStringLengthByNormal(s)); // 3
        System.out.println(lengthOfLongestSubstring(s)); // 3

        System.out.println("-------------------------");

        s = "bbbbb"; // 结果：b
        System.out.println(subStringLengthByNormal(s)); // 1
        System.out.println(lengthOfLongestSubstring(s)); // 1

        System.out.println("-------------------------");

        s = "pwwkew"; // 结果：wke
        System.out.println(subStringLengthByNormal(s)); // 3
        System.out.println(lengthOfLongestSubstring(s)); // 3
    }

    /**
     * 普通方式：把【所有的子串】一个一个的尝试
     */
    public int subStringLengthByNormal(String str) {
        int resLength = 0;
        int strLength = str.length();

        // 两个for循环 i头 j尾 能确定出所有的子串
        for (int i = 0; i < strLength; i++) {
            for (int j = i + 1; j < strLength; j++) {
                Set<String> hashSet = new HashSet<>();

                boolean isExists = false;
                // 遍历这个子串的内容，看看有木有重复的字母
                // 有的话立马break 进入到下一个子串中
                for (int z = i; z < j; z++) {
                    String strChildren = str.substring(z, z + 1);
                    if (hashSet.contains(strChildren)) {
                        isExists = true;
                        break;
                    } else {
                        hashSet.add(strChildren);
                    }
                }

                // 若最终没有重复的，那就看看这个子串的长度和resLength进行对比喽
                // 取最大值
                if (!isExists) {
                    //这里是不存在相同的才给resLength赋值
                    resLength = Math.max(resLength, j - i);
                }

            }
        }
        return resLength;
    }

    public int lengthOfLongestSubstring(String s) {
        if (s == null) return 0;
        if (s.length() == 1) return 1;

        int res = 0, l = 0, r = 0;
        int length = s.length();
        Set<Character> set = new HashSet<>();

        while (l < length && r < length) {
            if (!set.contains(s.charAt(r))) {
                set.add(s.charAt(r));
                r++;
                res = Math.max(res, r - l);
            } else {
                set.remove(s.charAt(l));
                l++;
            }
        }
        return res;
    }

}