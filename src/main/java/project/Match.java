package project;

public record Match(long line, int offset) {

    public String toString() {
        return String.format("[lineOffset=%d, charOffset=%d]", line, offset);
    }

}
