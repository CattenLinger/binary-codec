package net.catten.codec.binary;

import java.io.ByteArrayOutputStream;
import java.util.*;

public final class Zen128 {
    private final static char[] codec = new char[] {
            '滅', '苦', '婆', '娑', '耶', '陀', '跋', '多', '漫', '都', '殿', '悉', '夜', '爍', '帝', '吉',
            '利', '阿', '無', '南', '那', '怛', '喝', '羯', '勝', '摩', '伽', '謹', '波', '者', '穆', '僧',
            '室', '藝', '尼', '瑟', '地', '彌', '菩', '提', '蘇', '醯', '盧', '呼', '舍', '佛', '參', '沙',
            '伊', '隸', '麼', '遮', '闍', '度', '蒙', '孕', '薩', '夷', '迦', '他', '姪', '豆', '特', '逝',
            '朋', '輸', '楞', '栗', '寫', '數', '曳', '諦', '羅', '曰', '咒', '即', '密', '若', '般', '故',
            '不', '實', '真', '訶', '切', '壹', '除', '能', '等', '是', '上', '明', '大', '神', '知', '弎',
            '藐', '耨', '得', '依', '諸', '世', '槃', '涅', '竟', '究', '想', '夢', '倒', '顛', '離', '遠',
            '怖', '恐', '有', '礙', '心', '所', '以', '亦', '智', '道', '磐', '集', '盡', '死', '老', '至'
    };

    public static char[] getCodec() {
        return Arrays.copyOf(codec, codec.length);
    }

    private final static Map<Character, Integer> reverseCodec = new HashMap<>();

    private final static char[] keywords = new char[] { '冥', '奢', '梵', '呐', '俱', '哆', '怯', '諳', '罰', '侄', '缽', '皤' };

    public static char[] getKeywords() {
        return Arrays.copyOf(keywords, keywords.length);
    }

    private final static Set<Character> keywordSet = new HashSet<>();

    static {
        for(char c : keywords) keywordSet.add(c);
        for(int i = 0; i < codec.length; i++) reverseCodec.put(codec[i], i);
    }

    /*
     * String to ByteArray decoder
     */

    public static StringToByteArrayDecoder getStringToByteArrayDecoder() {
        return decoder;
    }

    private static final Decoder decoder = new Decoder();

    public static class Decoder implements StringToByteArrayDecoder {
        private Decoder() {

        }

        @Override
        public byte[] decode(String str) {
            if(str.length() == 0) return new byte[0];
            boolean carrying = false;
            char[] chars = str.toCharArray();
            ByteArrayOutputStream output = new ByteArrayOutputStream(str.length() * 2);
            int writePos = 0;
            for(char c : chars) {
                if(carrying) {
                    output.write(reverseCodec.get(c) ^ 0x80);
                    carrying = false;
                    writePos++;
                    continue;
                } else if(keywordSet.contains(c)) {
                    carrying = true;
                    continue;
                }

                output.write((byte) (reverseCodec.get(c) & 0xFF));
                writePos++;
            }
            return Arrays.copyOf(output.toByteArray(), writePos);
        }
    }

    /*
     * ByteArray to String encoder
     */

    public static ByteArrayToStringEncoder getByteArrayToStringEncoder() {
        return encoder;
    }

    private static final Encoder encoder = new Encoder();

    public static class Encoder implements ByteArrayToStringEncoder {
        private final Random random = new Random(System.currentTimeMillis());

        private Encoder() {

        }

        @Override
        public String encode(byte[] bytes) {
            if (bytes.length == 0) return "";
            final StringBuilder sb = new StringBuilder();
            for(byte b : bytes) {
                final int index = b & 0xFF;
                if (index >= 0x80) {
                    sb.append(keywords[random.nextInt(keywords.length)]);
                    sb.append(codec[index ^ 0x80]);
                    continue;
                }

                sb.append(codec[index]);
            }

            return sb.toString();
        }
    }
}
