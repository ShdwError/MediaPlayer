package Tools.Files;

import java.util.ArrayList;
import java.util.List;

import Tools.Files.Data.Exceptions.DataTypeException;
import Tools.Files.Data.Exceptions.InvalidFormatException;
import Tools.Files.Data.Return2;

public class DataParser {
    private List<ParsingStep> steps;
    public DataParser() {
        steps = new ArrayList<>();
    }
    public List<List<String>> parse(String s) throws DataTypeException {
        List<List<String>> ret = new ArrayList<>();
        for(ParsingStep parsingStep: steps) {
            Return2<List<String>, String> r2 = parsingStep.parse(s);
            if(r2.one != null) ret.add(r2.one);
            s = r2.two;
        }
        if(!s.isEmpty())
            throw new InvalidFormatException("Expected " + s + " to be empty after parsing");
        return ret;
    }

    public static DataParser start(String s) {
        return new DataParser().expect(s);
    }
    public static DataParser start() {
        return new DataParser();
    }
    public DataParser expect(String s) {
        steps.add(new ExpectString(s));
        return this;
    }
    public DataParser parse() {
        steps.add(new ParseString());
        return this;
    }
    public DataParser parseWhile(char separator) {
        steps.add(new ParseWhile(separator));
        return this;
    }


    
    public abstract class ParsingStep {
        public abstract Return2<List<String>, String> parse(String s) throws DataTypeException;
    }
    public class ExpectString extends ParsingStep {
        private String expected;
        public ExpectString(String expected) {
            this.expected = expected;
        }
        @Override
        public Return2<List<String>, String> parse(String s) throws InvalidFormatException {
            if(s.length() < expected.length() || !s.substring(0, expected.length()).equals(s))
                throw new InvalidFormatException("Expected " + expected + "at " + s);
            return new Return2<>(null, s.substring(expected.length()));
        }
    }
    public class ParseString extends ParsingStep {
        @Override
        public Return2<List<String>, String> parse(String s) {
            Return2<String, String> r2 = Util.getStringPart(s);
            return new Return2<>(List.of(r2.one), r2.two);
        }
    }
    public class ParseWhile extends ParsingStep {
        private char separator;
        public ParseWhile(char separator) {
            this.separator = separator;
        }
        @Override
        public Return2<List<String>, String> parse(String s) {
            return Util.getStringParts(s, separator, -1);
        }
    }
}
