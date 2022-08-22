package net.catten.codec.binary;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class Zen256 {
    private final static char[] codec = new char[]{
            '謹', '穆', '僧', '室', '藝', '瑟', '彌', '提', '蘇', '醯', '盧', '呼', '舍', '參', '沙', '伊',
            '隸', '麼', '遮', '闍', '度', '蒙', '孕', '薩', '夷', '他', '姪', '豆', '特', '逝', '輸', '楞',
            '栗', '寫', '數', '曳', '諦', '羅', '故', '實', '訶', '知', '三', '藐', '耨', '依', '槃', '涅',
            '竟', '究', '想', '夢', '倒', '顛', '遠', '怖', '恐', '礙', '以', '亦', '智', '盡', '老', '至',
            '吼', '足', '幽', '王', '告', '须', '弥', '灯', '护', '金', '刚', '游', '戏', '宝', '胜', '通',
            '药', '师', '琉', '璃', '普', '功', '德', '山', '善', '住', '过', '去', '七', '未', '来', '贤',
            '劫', '千', '五', '百', '万', '花', '亿', '定', '六', '方', '名', '号', '东', '月', '殿', '妙',
            '尊', '树', '根', '西', '皂', '焰', '北', '清', '数', '精', '进', '首', '下', '寂', '量', '诸',
            '多', '释', '迦', '牟', '尼', '勒', '阿', '閦', '陀', '中', '央', '众', '生', '在', '界', '者',
            '行', '于', '及', '虚', '空', '慈', '忧', '各', '令', '安', '稳', '休', '息', '昼', '夜', '修',
            '持', '心', '求', '诵', '此', '经', '能', '灭', '死', '消', '除', '毒', '害', '高', '开', '文',
            '殊', '利', '凉', '如', '念', '即', '说', '曰', '帝', '毘', '真', '陵', '乾', '梭', '哈', '敬',
            '禮', '奉', '祖', '先', '孝', '雙', '親', '守', '重', '師', '愛', '兄', '弟', '信', '朋', '友',
            '睦', '宗', '族', '和', '鄉', '夫', '婦', '教', '孫', '時', '便', '廣', '積', '陰', '難', '濟',
            '急', '恤', '孤', '憐', '貧', '創', '廟', '宇', '印', '造', '經', '捨', '藥', '施', '茶', '戒',
            '殺', '放', '橋', '路', '矜', '寡', '拔', '困', '粟', '惜', '福', '排', '解', '紛', '捐', '資'
    };

    public static char[] getCodec() {
        return Arrays.copyOf(codec, codec.length);
    }

    private final static Map<Character, Integer> reverseCodec = new HashMap<>();

    static {
        for (int i = 0; i < codec.length; i++) reverseCodec.put(codec[i], i);
    }

    /*
     * String to ByteArray Decoder
     */

    public static StringToByteArrayDecoder getStringToByteArrayDecoder() {
        return decoder;
    }

    private final static Decoder decoder = new Decoder();

    public static class Decoder implements StringToByteArrayDecoder {

        private Decoder() {
        }

        @Override
        public byte[] decode(String str) {
            if (str.length() == 0) return new byte[0];
            final byte[] buffer = new byte[str.length()];
            final char[] chars = str.toCharArray();
            for (int i = 0; i < chars.length; i++) buffer[i] = (byte) (reverseCodec.get(chars[i]) & 0xFF);
            return buffer;
        }
    }

    /*
     * ByteArray to String Encoder
     */

    public static ByteArrayToStringEncoder getByteArrayToStringEncoder() {
        return encoder;
    }

    private static final Encoder encoder = new Encoder();

    public static class Encoder implements ByteArrayToStringEncoder {
        private Encoder() {
        }

        @Override
        public String encode(byte[] bytes) {
            if (bytes.length == 0) return "";
            StringBuilder sb = new StringBuilder(bytes.length);
            for (byte b : bytes) sb.append(codec[b & 0xFF]);
            return sb.toString();
        }
    }
}
