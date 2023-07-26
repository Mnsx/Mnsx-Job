package top.mnsx.job.utils;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class IdUtil {

    public IdUtil() {

    }

    private static String[] chars = new String[] { "a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z" };

    private static final AtomicInteger CUR_ID = new AtomicInteger();

    /**
     * 获取原子Id
     * @return int
     */
    public static int generateAtomicId() {
        return CUR_ID.getAndIncrement();
    }

    /**
     * 生成长UUID
     * @return String
     */
    public static String generateTicket() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replace("-", "");
    }

    /**
     * 生成短8位UUID
     * @return String
     */
    public static String generateShortUuid() {
        StringBuffer sb = new StringBuffer();
        // 生成长UUID
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replace("-", "");

        // 将UUID分为8组，每组生成一个数
        for (int i = 0; i < 8; ++i) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int temp = Integer.parseInt(str);

            // 将每组数据对64取模作为索引获取新值
            sb.append(chars[temp % 0x3e]);
        }

        return sb.toString();
    }
}
