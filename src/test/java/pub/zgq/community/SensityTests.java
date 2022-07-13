package pub.zgq.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pub.zgq.community.util.SensitiveFilter;

import java.util.Arrays;
import java.util.Scanner;

/**
 * @Author 孑然
 */
@SpringBootTest
public class SensityTests {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter() {
        String text = "这里可以赌博吸毒, 可以开票，可以嫖娼，可以吸毒,哈哈哈！";
        String filter = sensitiveFilter.filter(text);
        System.out.println(filter);
        text = "这里可以⭐赌⭐※博, 可以⭐开⭐票，可以⭐⭐⭐嫖※娼，可以⭐⭐吸⭐⭐毒,哈哈哈！";
        filter = sensitiveFilter.filter(text);
        System.out.println(filter);
    }


    //public static void main(String[] args) {
    //    while (true) {
    //        int[] arr1 = new int[10];
    //
    //        Scanner scanner = new Scanner(System.in);
    //        System.out.println("请输入10个数");
    //        // 初始化arr1
    //        for (int i = 0; i < 10; i++) {
    //            System.out.println("第" + (i+1) +"个：");
    //            arr1[i] = scanner.nextInt();
    //        }
    //
    //        // 输出去原来数组
    //        for (int i = 0; i < arr1.length; i++) {
    //            System.out.print(arr1[i] + " ");
    //        }
    //
    //        // 换行
    //        System.out.println();
    //
    //        // 调用去重方法
    //        int[] remove_arr = remove(arr1);
    //        if (remove_arr.length == arr1.length) {
    //            // 没有重复元素
    //            System.out.println("没有重复元素!");
    //            continue;
    //        }
    //        for (int i = 0; i < remove_arr.length; i++) {
    //            System.out.print(remove_arr[i] + " ");
    //        }
    //        break;
    //    }
    //
    //}
    //
    //// 去重方法
    //public  static int[] remove(int[] arr){
    //
    //    // 定义新数组
    //    int[] arr2 = new int[10];
    //    // 指针
    //    int a = 0;
    //    for (int i = 0; i < arr.length; i++) {
    //        // 标记是否重复
    //        boolean flag = true;
    //        // 遍历新数组 判断是否已经存在当前元素
    //        for (int j = 0; j < arr2.length; j++) {
    //            if (arr[i] == arr2[j]) {
    //                flag = false;
    //            }
    //        }
    //        // 不重复，添加到新数组
    //        if (flag){
    //            arr2[a] = arr[i];
    //            // 指针后移一位
    //            a++;
    //        }
    //    }
    //    // 定义一个去重后大小的数组
    //    int[] arr3 = new int[a];
    //    //去掉后边多余元素
    //    for (int i = 0; i < a; i++) {
    //        arr3[i] = arr2[i];
    //    }
    //    return arr3;
    //}


}
