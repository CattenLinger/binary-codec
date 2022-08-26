package net.catten.codec.binary;

import java.io.ByteArrayOutputStream;
import java.util.*;

public final class Zen128 implements BinaryStringDeserializer, BinaryStringSerializer {

    /****************************************************************
     * Constants
     ****************************************************************/

    private final static char[] defaultCodec = new char[]{
            '滅', '苦', '婆', '娑', '耶', '陀', '跋', '多', '漫', '都', '殿', '悉', '夜', '爍', '帝', '吉',
            '利', '阿', '無', '南', '那', '怛', '喝', '羯', '勝', '摩', '伽', '謹', '波', '者', '穆', '僧',
            '室', '藝', '尼', '瑟', '地', '彌', '菩', '提', '蘇', '醯', '盧', '呼', '舍', '佛', '參', '沙',
            '伊', '隸', '麼', '遮', '闍', '度', '蒙', '孕', '薩', '夷', '迦', '他', '姪', '豆', '特', '逝',
            '朋', '輸', '楞', '栗', '寫', '數', '曳', '諦', '羅', '曰', '咒', '即', '密', '若', '般', '故',
            '不', '實', '真', '訶', '切', '一', '除', '能', '等', '是', '上', '明', '大', '神', '知', '三',
            '藐', '耨', '得', '依', '諸', '世', '槃', '涅', '竟', '究', '想', '夢', '倒', '顛', '離', '遠',
            '怖', '恐', '有', '礙', '心', '所', '以', '亦', '智', '道', '。', '集', '盡', '死', '老', '至'
    };

    private final static char[] defaultKeywords = new char[]{
            '冥', '奢', '梵', '呐', '俱', '哆', '怯', '諳', '罰', '侄', '缽', '皤'
    };

    private final static Zen128 defaultZen128 = new Zen128();

    /****************************************************************
     * Static methods
     ****************************************************************/

    public static Zen128 getDefaultZen128() {
        return defaultZen128;
    }

    public static Zen128 useCodecAndKeywords(final char[] codec, final char[] keywords) {
        return new Zen128(codec, keywords);
    }

    /****************************************************************
     * Instance variables
     ****************************************************************/

    private final Random random;

    private final char[] codec;

    private final Map<Character, Integer> reverseCodec;

    private final char[] keywords;

    private final Set<Character> keywordSet;

    /****************************************************************
     * Constructors
     ****************************************************************/

    private Zen128(final char[] codec, final char[] keywords) {
        this.random = new Random(System.currentTimeMillis());
        if (codec.length != 128) throw new IllegalArgumentException("Zen128 require an 128 char array as codec.");
        if (keywords.length == 0) throw new IllegalArgumentException("Zen128 require an non empty char array as keyword codec.");

        reverseCodec = new HashMap<>();
        for (int i = 0; i < codec.length; i++) reverseCodec.put(codec[i], i);
        if (reverseCodec.size() != codec.length) throw new IllegalArgumentException("Duplicate char in codec.");
        this.codec = codec;

        keywordSet = new HashSet<>();
        for (char c : keywords) {
            if (reverseCodec.containsKey(c)) throw new IllegalArgumentException("Codec contains a keyword.");
            keywordSet.add(c);
        }
        if (keywordSet.size() != keywords.length) throw new IllegalArgumentException("Duplicate char in keyword.");
        this.keywords = keywords;
    }

    private Zen128() {
        this(defaultCodec, defaultKeywords);
    }

    /****************************************************************
     * Members
     ****************************************************************/

    /**
     * Get all chars that to be used to encode.
     */
    public char[] getCodec() {
        return Arrays.copyOf(codec, codec.length);
    }

    /**
     * Get all chars that to be used as carrying.
     */
    public char[] getKeywords() {
        return Arrays.copyOf(keywords, keywords.length);
    }

    /****************************************************************
     * Implementations
     ****************************************************************/

    @Override
    public byte[] deserialize(String string) {
        final int length = string.length();
        if (length == 0) return new byte[0];

        boolean carrying = false;
        char[] chars = string.toCharArray();
        ByteArrayOutputStream output = new ByteArrayOutputStream(length * 2);

        for (char c : chars) {
            if (carrying) {
                output.write(reverseCodec.get(c) ^ 0x80);
                carrying = false;
                continue;
            } else if (keywordSet.contains(c)) {
                carrying = true;
                continue;
            }

            output.write((byte) (reverseCodec.get(c) & 0xFF));
        }
        return output.toByteArray();
    }

    @Override
    public String serialize(byte[] data) {
        if (data.length == 0) return "";
        final StringBuilder sb = new StringBuilder();
        for (byte b : data) {
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
