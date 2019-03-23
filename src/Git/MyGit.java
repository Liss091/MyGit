package Git;

import java.io.IOException;

public interface MyGit {
    void init(String origin) throws IOException;
    void commit(String filePath) throws IOException;
    void branch(String b) throws IOException;
    void checkout(String b) throws IOException;
    void compare() throws IOException;
}
