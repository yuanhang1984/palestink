package library.array;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Comparator;
import java.util.Collections;

public class ArrayKit {
        /**
         * 根据key值排序HashMap中个数据的位置
         * 
         * @param p HashMap对象
         * @return 排序后的ArrayList
         */
        public static ArrayList<Entry<String, Object>> sortByKey(HashMap<String, Object> p) {
                ArrayList<Entry<String, Object>> list = new ArrayList<Entry<String, Object>>(p.entrySet());
                Collections.sort(list, new Comparator<Entry<String, Object>>() {
                        public int compare(Entry<String, Object> m1, Entry<String, Object> m2) {
                                return m1.getKey().compareTo(m2.getKey());
                        }
                });
                return list;
        }
}
