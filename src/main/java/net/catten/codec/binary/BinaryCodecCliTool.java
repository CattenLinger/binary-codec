package net.catten.codec.binary;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class BinaryCodecCliTool {
    private Mode mode = Mode.ENCODE;
    private InputStream inputStream = System.in;
    private OutputStream outputStream = System.out;

    private Boolean verboseMode = false;

    private Boolean newLineAfterEncode = true;

    private Codec codec;

    private List<String> options;

    private enum Mode {
        ENCODE, DECODE
    }

    public void run() throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        while(true) {
            int read = inputStream.read();
            if(read == -1) break;
            buffer.write(read);
        }

        byte[] bytes = buffer.toByteArray();

        if(mode == Mode.ENCODE) {
            outputStream.write(codec.encoder.encode(bytes).getBytes());
            if(newLineAfterEncode) outputStream.write("\n".getBytes());
        } else {
            outputStream.write(codec.decoder.decode(new String(bytes)));
        }
    }

    private static class CmdOpt {

        public final String[] names;
        public final String value;
        public final int position;

        private CmdOpt(String[] name, String value, int position) {
            this.names = name;
            this.value = value;
            this.position = position;
        }

        public static CmdOpt booleanFrom(String[] args, String... name) {
            final List<String> names = Arrays.asList(name);
            for(int i = 0; i < args.length; i++) {
                if(names.contains(args[i])) return new CmdOpt(name, null, i);
            }
            return null;
        }

        public static CmdOpt valueFrom(String[] args, String... name) {
            final List<String> names = Arrays.asList(name);
            for(int i = 0; i < args.length; i++) {
                if(names.contains(args[i])) return new CmdOpt(name, args[i + 1], i);
            }
            return null;
        }
    }

    private static class Codec {
        public final ByteArrayToStringEncoder encoder;
        public final StringToByteArrayDecoder decoder;

        public Codec(ByteArrayToStringEncoder encoder, StringToByteArrayDecoder decoder) {
            this.encoder = encoder;
            this.decoder = decoder;
        }
    }

    private static Codec getCodec(String name) {
        switch (name) {
            case "hangul4096plus":
                return new Codec(Hangul4096Plus.getByteArrayToStringEncoder(), Hangul4096Plus.getStringToByteArrayDecoder());
            case "zen128":
                return new Codec(Zen128.getByteArrayToStringEncoder(), Zen128.getStringToByteArrayDecoder());
            case "zen256":
                return new Codec(Zen256.getByteArrayToStringEncoder(), Zen256.getStringToByteArrayDecoder());
            case "hexagram64":
                return new Codec(Hexagram64.getByteArrayToStringEncoder(), Hexagram64.getStringToByteArrayDecoder());
            default:
                throw new IllegalArgumentException("Unknown codec: " + name);
        }
    }

    private static BinaryCodecCliTool fromArgs(String[] args) throws IOException {
        CmdOpt opt;

        BinaryCodecCliTool tool = new BinaryCodecCliTool();

        opt = CmdOpt.valueFrom(args, "--codec", "-c");
        if (opt == null || opt.value == null) throw new IllegalArgumentException("Missing codec name");
        tool.codec = getCodec(opt.value);

        opt = CmdOpt.valueFrom(args, "--input", "-i");
        if(opt != null) {
            if(opt.value == null) throw new IllegalArgumentException("--input option requires an argument");
            tool.inputStream = Files.newInputStream(new File(opt.value).toPath());
        }

        opt = CmdOpt.valueFrom(args, "--output", "-o");
        if(opt != null) {
            if(opt.value == null) throw new IllegalArgumentException("--output option requires an argument");
            tool.outputStream = Files.newOutputStream(new File(opt.value).toPath());
        }

        opt = CmdOpt.booleanFrom(args, "--encode", "-e");
        if(opt != null) tool.mode = Mode.ENCODE;

        opt = CmdOpt.booleanFrom(args, "--decode", "-d");
        if(opt != null) tool.mode = Mode.DECODE;

        opt = CmdOpt.booleanFrom(args, "--verbose", "-v");
        if(opt != null) tool.verboseMode = true;

        opt = CmdOpt.booleanFrom(args, "--no-newline", "-n");
        if(opt != null) tool.newLineAfterEncode = false;

        List<String> options = new ArrayList<>();
        List<String> optionToken = Arrays.asList("--option", "-o");
        for(int i = 0; i < args.length; i++) {
            if(optionToken.contains(args[i])) {
                options.add(args[i + 1]);
                i++;
            }
        }
        tool.options = options;

        return tool;
    }

    private static void printHelpAndExit() {
        System.err.println("Usage: java -jar binary-codec.jar [options]");
        System.err.println("Options:");
        System.err.println("        --codec|-c <name>    Specify codec name");
        System.err.println("        --input|-i <file>    Specify input file");
        System.err.println("       --output|-o <file>    Specify output file");
        System.err.println("       --encode|-e           Encode (default)");
        System.err.println("   --no-newline|-n           Do not add newline after encoded content");
        System.err.println("       --decode|-d           Decode");
        System.err.println("      --verbose|-v           Verbose output");
        System.err.println("       --option|-O           Codec options");
        System.err.println("Codecs:");
        System.err.println("   hangul4096plus: Encode binary using Korean characters.");
        System.err.println("           zen128: Encode binary using 128 Zen words.");
        System.err.println("           zen256: Encode binary using 256 Zen words.");
        System.err.println("       hexagram64: Encode binary using Hexagram characters.");
        System.exit(0);
    }

    public static void main(String[] args) {
        BinaryCodecCliTool tool;
        try {
            tool = fromArgs(args);
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) System.err.println("[ERROR] " + e.getMessage());
            printHelpAndExit();
            // Will not reach here
            throw new IllegalStateException();
        }

        try {
            tool.run();
        } catch (Exception e) {

            if(tool.verboseMode) {
                System.err.printf("[ERROR] Error %s, message: %s\n",e.getClass().getName(), e.getMessage());
                Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).forEach(System.err::println);
            } else {
                System.err.println("[ERROR] " + e.getMessage());
            }
            System.exit(1);
        }
    }
}