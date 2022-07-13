package pub.zgq.community.entity;

import java.util.Scanner;

public class test {
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        int[] arr=new int[10];
        for (int i = 0; i <arr.length ; i++) {
            System.out.println("请输入第"+(i+1)+"个数字");
            arr[i]=sc.nextInt();


        }
        int[] oarray = remove_arr(arr);
        for (int j = 0; j <oarray.length ; j++) {
            System.out.println(oarray[j]);
        }
    }
    public static int[] remove_arr(int[] arr){
        int[] arr1 = new int[10];
        // 标记重复元素
        int[] arr2 = new int[10];
        for (int i = 0; i < arr.length; i++) {

            boolean flag = true;
            for (int j = 0; j < arr1.length; j++) {
                if (arr[i] == arr1[j]) {
                    flag = false;

                }
            }
        }

        return arr;
    }
}